package tu_darmstadt.sudoku.controller;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Date;
import java.util.concurrent.atomic.AtomicIntegerArray;

import tu_darmstadt.sudoku.controller.qqwing.Action;
import tu_darmstadt.sudoku.controller.qqwing.PrintStyle;
import tu_darmstadt.sudoku.controller.qqwing.QQWing;
import tu_darmstadt.sudoku.controller.qqwing.Symmetry;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 21.11.2015.
 */
public class QQWingController {

    private static final String NL = System.getProperties().getProperty("line.separator");

    final QQWingOptions opts = new QQWingOptions();

    private int[] level;
    private int[] solution;
    private int[] generated;
    private boolean solveImpossible = false;

    public int[] generate(GameType type, GameDifficulty difficulty) {
        // TODO: GameType options.
        opts.gameDifficulty = difficulty;
        opts.action = Action.GENERATE;
        opts.threads = Runtime.getRuntime().availableProcessors();
        doAction(type);
        return generated;
    }

    public int[] solve(GameBoard gameBoard) {

        level = new int[gameBoard.getSize()*gameBoard.getSize()];
        solveImpossible = false;

        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                if(gameBoard.getCell(i,j).isFixed()) {
                    level[gameBoard.getSize() * i + j] = gameBoard.getCell(i,j).getValue();
                }
            }
        }

        opts.action = Action.SOLVE;
        opts.printSolution = true;
        opts.threads = 1;
        doAction(gameBoard.getGameType());
        if(solveImpossible) {
            // TODO: do something else.

        }
        return solution;
    }

    private void doAction(final GameType gameType) {
        // The number of puzzles solved or generated.
        final AtomicInteger puzzleCount = new AtomicInteger(0);
        final AtomicBoolean done = new AtomicBoolean(false);
        final AtomicIntegerArray result = new AtomicIntegerArray(new int[gameType.getSize()*gameType.getSize()]);

        Thread[] threads = new Thread[opts.threads];
        for (int threadCount = 0; threadCount < threads.length; threadCount++) {
            threads[threadCount] = new Thread(
                    new Runnable() {

                        // Create a new puzzle board
                        // and set the options
                        private QQWing ss = createQQWing();

                        private QQWing createQQWing() {
                            QQWing ss = new QQWing(gameType);
                            ss.setRecordHistory(opts.printHistory || opts.printInstructions || opts.printStats || opts.gameDifficulty != GameDifficulty.Unspecified);
                            ss.setLogHistory(opts.logHistory);
                            ss.setPrintStyle(opts.printStyle);
                            return ss;
                        }

                        @Override
                        public void run() {
                            try {

                                // Solve puzzle or generate puzzles
                                // until end of input for solving, or
                                // until we have generated the specified number.
                                while (!done.get()) {

                                    // iff something has been printed for this
                                    // particular puzzle
                                    StringBuilder output = new StringBuilder();

                                    // Record whether the puzzle was possible or
                                    // not,
                                    // so that we don't try to solve impossible
                                    // givens.
                                    boolean havePuzzle = false;

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
                                                // TODO: Puzzle to solve is impossible.
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

                                    int solutions = 0;

                                    if (havePuzzle) {

                                        // Count the solutions if requested.
                                        // (Must be done before solving, as it would
                                        // mess up the stats.)
                                        if (opts.countSolutions) {
                                            solutions = ss.countSolutions();
                                        }

                                        // Solve the puzzle
                                        if (opts.printSolution || opts.printHistory || opts.printStats || opts.printInstructions || opts.gameDifficulty != GameDifficulty.Unspecified) {
                                            ss.solve();
                                            solution = ss.getSolution();
                                        }

                                        // Bail out if it didn't meet the gameDifficulty
                                        // standards for generation
                                        if (opts.action == Action.GENERATE) {
                                            if (opts.gameDifficulty != GameDifficulty.Unspecified && opts.gameDifficulty != ss.getDifficulty()) {
                                                havePuzzle = false;
                                                // check if other threads have
                                                // finished the job
                                                if (puzzleCount.get() >= opts.numberToGenerate) {
                                                    done.set(true);
                                                    generated = ss.getPuzzle();
                                                }
                                            } else {
                                                int numDone = puzzleCount.incrementAndGet();
                                                if (numDone >= opts.numberToGenerate) {
                                                    done.set(true);
                                                    generated = ss.getPuzzle();
                                                }
                                                if (numDone > opts.numberToGenerate)
                                                    havePuzzle = false;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("QQWing", "Exception Occured", e);
                                return;
                            }
                        }

                    }
            );
            threads[threadCount].start();
        }

        for(int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class QQWingOptions {
        // defaults for options
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
        Symmetry symmetry = Symmetry.NONE;
        int threads = Runtime.getRuntime().availableProcessors();
    }

    private static long getMicroseconds() {
        return new Date().getTime() * 1000;
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
