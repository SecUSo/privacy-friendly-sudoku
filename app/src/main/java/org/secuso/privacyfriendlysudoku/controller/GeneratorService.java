/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.Pair;

import org.secuso.privacyfriendlysudoku.PFSudoku;
import org.secuso.privacyfriendlysudoku.controller.database.DatabaseHelper;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;
import org.secuso.privacyfriendlysudoku.controller.qqwing.Action;
import org.secuso.privacyfriendlysudoku.controller.qqwing.PrintStyle;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.controller.qqwing.Symmetry;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.MainActivity;
import org.secuso.privacyfriendlysudoku.R;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static org.secuso.privacyfriendlysudoku.controller.NewLevelManager.PRE_SAVES_MIN;

/**
 *
 * @author Christopher Beckmann
 */
public class GeneratorService extends JobIntentService {

    private static final String TAG = GeneratorService.class.getSimpleName();
    public static final String ACTION_GENERATE = TAG + " ACTION_GENERATE";
    public static final String ACTION_STOP = TAG + " ACTION_STOP";
    public static final String EXTRA_GAMETYPE = TAG + " EXTRA_GAMETYPE";
    public static final String EXTRA_DIFFICULTY = TAG + " EXTRA_DIFFICULTY";

    private final QQWingOptions opts = new QQWingOptions();

    private final List<Pair<GameType, GameDifficulty>> generationList = new LinkedList<>();
    private final DatabaseHelper dbHelper = new DatabaseHelper(this);
    //private Handler mHandler = new Handler();


    //public GeneratorService() {
    //    super("Generator Service");
    //}

    //public GeneratorService(String name) { super(name); }


    private void buildGenerationList() {
        generationList.clear();

        for(GameType validType : GameType.getValidGameTypes()) {
            for(GameDifficulty validDifficulty : GameDifficulty.getValidDifficultyList()) {
                int levelCount = dbHelper.getLevels(validDifficulty, validType).size();
                Log.d(TAG, "\tType: "+ validType.name() + " Difficulty: " + validDifficulty.name() + "\t: " + levelCount);
                // add the missing levels to the list
                for(int i = levelCount; i < PRE_SAVES_MIN; i++) {
                    generationList.add(new Pair<>(validType, validDifficulty));
                }
            }
        }

        // PrintGenerationList
        Log.d(TAG, "### Missing Levels: ###");
        int i = 0;
        for(Pair<GameType, GameDifficulty> dataPair : generationList) {
            Log.d(TAG, "\t" + i++ + ":\tType: "+ dataPair.first.name() + " Difficulty: " + dataPair.second.name());
        }
    }

    private void handleGenerationStop() {
        stopForeground(true);
        //mHandler.removeCallbacksAndMessages(null);
    }

    private void handleGenerationStart(Intent intent) {
        GameType gameType;
        GameDifficulty gameDifficulty;
        try {
            gameType = GameType.valueOf(intent.getExtras().getString(EXTRA_GAMETYPE, ""));
            gameDifficulty = GameDifficulty.valueOf(intent.getExtras().getString(EXTRA_DIFFICULTY, ""));
        } catch(IllegalArgumentException | NullPointerException e) {
            gameType = null;
            gameDifficulty = null;
        }

        if(gameType == null) {
            generateLevels();
        } else {
            generateLevel(gameType, gameDifficulty);
        }
    }

    private void generateLevels() {
        // if we start this service multiple times while we are already generating...
        // we ignore this call and just keep generating
        buildGenerationList();
        // generate from the list
        if(generationList.size() > 0) {

            // generate 1 level and wait for it to be done.
            Pair<GameType, GameDifficulty> dataPair = generationList.remove(0);
            GameType type = dataPair.first;
            GameDifficulty diff = dataPair.second;

            generateLevel(type, diff);
        }
    }

    private void generateLevel(final GameType gameType, final GameDifficulty gameDifficulty) {
        showNotification(gameType, gameDifficulty);

        generated.clear();
        opts.gameDifficulty = gameDifficulty;
        opts.action = Action.GENERATE;
        opts.needNow = true;
        opts.printSolution = false;
        opts.gameType = gameType;
        if(gameDifficulty == GameDifficulty.Easy && gameType == GameType.Default_9x9) {
            opts.symmetry = Symmetry.ROTATE90;
        } else {
            opts.symmetry = Symmetry.NONE;
        }
        if(gameType == GameType.Default_12x12 && gameDifficulty != GameDifficulty.Challenge) {
            opts.symmetry = Symmetry.ROTATE90;
        }

        final AtomicInteger puzzleCount = new AtomicInteger(0);
        final AtomicBoolean done = new AtomicBoolean(false);

        Runnable generationRunnable = new Runnable() {
                // Create a new puzzle board
                // and set the options
                private QQWing ss = createQQWing();

                private QQWing createQQWing() {
                    QQWing ss = new QQWing(opts.gameType, opts.gameDifficulty);
                    ss.setRecordHistory(opts.printHistory || opts.printInstructions || opts.printStats || opts.gameDifficulty != GameDifficulty.Unspecified);
                    ss.setLogHistory(opts.logHistory);
                    ss.setPrintStyle(opts.printStyle);
                    return ss;
                }

                @Override
                public void run() {
                    //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    try {

                        // Solve puzzle or generate puzzles
                        // until end of input for solving, or
                        // until we have generated the specified number.
                        while (!done.get()) {

                            // Record whether the puzzle was possible or
                            // not,
                            // so that we don't try to solve impossible
                            // givens.
                            boolean havePuzzle;
                            boolean solveImpossible;

                            if (opts.action == Action.GENERATE) {
                                // Generate a puzzle
                                havePuzzle = ss.generatePuzzleSymmetry(opts.symmetry);

                            } else {
                                // Read the next puzzle on STDIN
                                int[] puzzle = new int[QQWing.BOARD_SIZE];
                                if (getPuzzleToSolve(puzzle)) {
                                    havePuzzle = ss.setPuzzle(puzzle);
                                    if (havePuzzle) {
                                        puzzleCount.getAndDecrement();
                                    } else {
                                        // Puzzle to solve is impossible.
                                        solveImpossible = true;
                                    }
                                } else {
                                    // Set loop to terminate when nothing is
                                    // left on STDIN
                                    havePuzzle = false;
                                    done.set(true);
                                }
                                puzzle = null;
                            }

                            if(opts.gameDifficulty != GameDifficulty.Unspecified) {
                                ss.solve();
                            }

                            if (havePuzzle) {
                                // Bail out if it didn't meet the difficulty
                                // standards for generation
                                if (opts.action == Action.GENERATE) {

                                    // save the level anyways but keep going if the desired level is not yet generated
                                    Level level = new Level();
                                    level.setGameType(opts.gameType);
                                    level.setDifficulty(ss.getDifficulty());
                                    level.setPuzzle(ss.getPuzzle());
                                    dbHelper.addLevel(level);
                                    Log.d(TAG, "Generated: " + level.getGameType().name() + ",\t"+level.getDifficulty().name());

                                    if (opts.gameDifficulty != GameDifficulty.Unspecified && opts.gameDifficulty != ss.getDifficulty()) {
                                        havePuzzle = false;
                                        // check if other threads have finished the job
                                        if (puzzleCount.get() >= opts.numberToGenerate)
                                            done.set(true);
                                    } else {
                                        int numDone = puzzleCount.incrementAndGet();
                                        if (numDone >= opts.numberToGenerate) done.set(true);
                                        if (numDone > opts.numberToGenerate) havePuzzle = false;
                                    }
                                }
                                if (havePuzzle) {
                                    generated.add(ss.getPuzzle());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("QQWing", "Exception Occured", e);
                        return;
                    }
                    generationDone();
                }
            };

        generationRunnable.run();
    }

    // this is called whenever a generation is done..
    private void generationDone() {
        // check if more can be generated
        if(generationList.size() > 0) {
            generateLevels();
        } else {
            // we are done and can close this service
            handleGenerationStop();
        }
    }

    private void showNotification(GameType gameType, GameDifficulty gameDifficulty) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PFSudoku.CHANNEL_ID);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.generating));
        builder.setSubText(getString(gameType.getStringResID()) + ", " + getString(gameDifficulty.getStringResID()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        builder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setWhen(0);
        builder.setSmallIcon(R.drawable.splash_icon);
        startForeground(50, builder.build());
    }

    static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeneratorService.class, 1000, intent);
    }

    @Override
    protected void onHandleWork(@Nullable Intent intent) {
        if (intent != null) {

            String action = intent.getAction();

            if      (ACTION_GENERATE.equals(action))    handleGenerationStart(intent);
            else if (ACTION_STOP.equals(action))        handleGenerationStop();
        }
    }

    private int[] level;
    private LinkedList<int[]> generated = new LinkedList<>();

    private static class QQWingOptions {
        // defaults for options
        boolean needNow = false;
        boolean printPuzzle = false;
        boolean printSolution = false;
        boolean printHistory = false;
        boolean printInstructions = false;
        boolean timer = false;
        boolean countSolutions = false;
        Action action = Action.NONE;
        boolean logHistory = false;
        PrintStyle printStyle = PrintStyle.READABLE;
        int numberToGenerate = 1;
        boolean printStats = false;
        GameDifficulty gameDifficulty = GameDifficulty.Unspecified;
        GameType gameType = GameType.Unspecified;
        Symmetry symmetry = Symmetry.NONE;
        int threads = Runtime.getRuntime().availableProcessors();
    }

    private boolean getPuzzleToSolve(int[] puzzle) {
        if(level != null) {
            if(puzzle.length == level.length) {
                for(int i = 0; i < level.length; i++) {
                    puzzle[i] = level[i];
                }
            }
            level = null;
            return true;
        }
        return false;
    }
}
