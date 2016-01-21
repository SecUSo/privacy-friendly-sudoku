package org.secuso.privacyfriendlysudoku.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

/**
 * Created by Chris on 23.11.2015.
 */
public class NewLevelManager {

    Context context;
    private SharedPreferences settings;

    private static NewLevelManager instance;

    private static String FILE_EXTENSION = ".txt";
    private static String LEVEL_PREFIX = "level_";
    private static String LEVELS_DIR = "level";
    private static File DIR;

    public static NewLevelManager getInstance() {
        return instance;
    }
    public static NewLevelManager init(Context context, SharedPreferences settings) {
        if(instance == null) {
            instance = new NewLevelManager(context, settings);
        }
        return instance;
    }

    private NewLevelManager(Context context, SharedPreferences settings) {
        this.context = context;
        this.settings = settings;
        DIR = context.getDir(LEVELS_DIR, 0);
    }

    public boolean isLevelLoadable(GameType type, GameDifficulty diff) {
        for(File file : DIR.listFiles()) {
            if (file.isFile()) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("_"));

                StringBuilder sb = new StringBuilder();
                sb.append(LEVEL_PREFIX);
                sb.append(type.name());
                sb.append("_");
                sb.append(diff.name());

                if(name.equals(sb.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] loadLevel(GameType type, GameDifficulty diff) {
        List<int[]> result = new LinkedList<>();
        LinkedList<Integer> availableFiles = new LinkedList<>();

        // go through every file
        for(File file : DIR.listFiles()) {

            // filter so we only work with actual files
            if (file.isFile()) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("_"));
                String number = file.getName().substring(file.getName().lastIndexOf("_")+1, file.getName().lastIndexOf("."));

                StringBuilder sb = new StringBuilder();
                sb.append(LEVEL_PREFIX);
                sb.append(type.name());
                sb.append("_");
                sb.append(diff.name());

                // if file is a level for our gametype and difficulty .. load it
                if(name.equals(sb.toString())) {

                    // load file
                    byte[] bytes = new byte[(int)file.length()];
                    try {
                        FileInputStream stream = new FileInputStream(file);
                        try {
                            stream.read(bytes);
                        } finally {
                            stream.close();
                        }
                    } catch(IOException e) {
                        Log.e("File Manager", "Could not load game. IOException occured.");
                    }

                    // start parsing
                    String gameString = new String(bytes);

                    int[] puzzle = new int[type.getSize()*type.getSize()];

                    if(puzzle.length != gameString.length()) {
                        throw new IllegalArgumentException("Saved level is does not have the correct size.");
                    }

                    for(int i = 0; i < gameString.length(); i++) {
                        puzzle[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(gameString.charAt(i)))+1;
                    }
                    availableFiles.add(Integer.valueOf(number));
                    result.add(puzzle);
                }
            }
        }

        if(result.size() > 0) {
            int chosen = availableFiles.get(0);
            int[] resultPuzzle = result.get(0);

            StringBuilder sb = new StringBuilder();
            sb.append(LEVEL_PREFIX);
            sb.append(type.name());
            sb.append("_");
            sb.append(diff.name());
            sb.append("_");
            sb.append(chosen);
            sb.append(FILE_EXTENSION);
            String filename = sb.toString();

            // select and delete the file
            File file = new File(DIR, filename);
            file.delete();

            // then return the puzzle to load it
            return resultPuzzle;
        }

        return null;
    }

    public void checkAndRestock() {
        new AsyncGenerationTask().execute();
    }

    public void loadFirstStartLevels() {
        // Default_9x9
        // Default_12x12
        // Default_6x6

        // Easy
        // Moderate
        // Hard

        // 0
        // 1

        saveToFile(GameType.Default_9x9, GameDifficulty.Easy, 0, "000208090027000000000400000090100706408090201000030080200001000100300469000000007");
        saveToFile(GameType.Default_9x9, GameDifficulty.Easy, 1, "000000052000003007500206830002040700070000046640508003000400000000005000050301008");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 0, "000800400008004093009003060000700000000400000060002900091000670200000100403006802");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 1, "000006040000050000600040080043000200500810000100300605209080460004500000001030000");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 0, "086000000000000070090000304968027030000100060200900000072006100000000296000000740");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 1, "450900001001400000600010004006700000500000000100024060002000030000062400040005700");

        saveToFile(GameType.Default_12x12, GameDifficulty.Easy, 0, "B30050A100701600070030800002894000007008000000B550100004020300B0000090000060000A010000000032050C0407008006A000000000400001000C290000000008005000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Easy, 1, "00B4008A09C002A030C00008007850003000030C000408AB000B00052000000000000070069500030C00B00010467000008000000A100000000800000C0020700001700000095400");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 0, "00600500000004000000002050004C00800A28049000050000A900C000000000B00000A00B0560000C900C708A00000B0002000000769000008000B002B0C6000017C00107400908");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 1, "020000B000A608070530000B9050200A03800030980010B001000B6C30900000032000CAB0000000000000067000B000000500000A000C09000081302B0100070950836000100000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 0, "2000000000000B070C00000000AC100000000020050CA00BB60002097800079001000C60400000B0070A0000A7000298005048000010004000070009A10600C00B000C00B0000020");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 1, "01000002000C00640A800070000A0000082020000008100B0C081090000050A0060C079009BC000A04010500097000000000000000000000904000866070B100020000000C00B009");

        saveToFile(GameType.Default_6x6, GameDifficulty.Easy, 0, "000000050004013000004003006050040010");
        saveToFile(GameType.Default_6x6, GameDifficulty.Easy, 1, "000450000600104306630000060005010000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 0, "630010002000001000040020400006003040");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 1, "000000060130006000050603030005000041");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 0, "004200200000003002600050300400046000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 1, "003050200003502000000000640002000045");
    }

    /** Don't use this if you don't know what you are doing! **/
    private void saveToFile(GameType gameType, GameDifficulty gameDifficulty, int saveNumber, String puzzle) {
        StringBuilder sb = new StringBuilder();
        sb.append(LEVEL_PREFIX);
        sb.append(gameType.name());
        sb.append("_");
        sb.append(gameDifficulty.name());
        sb.append("_");
        sb.append(saveNumber);
        sb.append(FILE_EXTENSION);
        // create the file
        File file = new File(DIR, sb.toString());

        // save the file
        try {
            FileOutputStream stream = new FileOutputStream(file);

            try {
                stream.write(puzzle.getBytes());
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            Log.e("File Manager", "Could not save game. IOException occured.");
        }
    }

    private class AsyncGenerationTask extends AsyncTask<int[][], Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(int[][] ... params) {
            int preSaves = 5;
            // init
            final LinkedList<GameType> gameTypes = GameType.getValidGameTypes();
            final LinkedList<GameDifficulty> gameDifficulties = GameDifficulty.getValidDifficultyList();
            final LinkedList<int[]> missing = new LinkedList<>();
            for(int i = 0; i < gameTypes.size(); i++) {
                for(int j = 0; j < gameDifficulties.size(); j++) {
                    for(int k = 0; k < preSaves ; k++) {
                        int[] m = new int[preSaves];
                        m[0] = i;   // gametype
                        m[1] = j;   // difficulty
                        m[2] = k;   // preSaves Puzzles per difficulty and gametype
                        missing.add(m);
                    }
                }
            }

            LinkedList<int[]> removeList = new LinkedList<>();
            // go through every file
            for (File file : DIR.listFiles()) {
                // filter so we only work with actual files
                if (file.isFile()) {
                    String filename = file.getName();
                    for(int i = 0; i < missing.size(); i++) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(LEVEL_PREFIX);
                        sb.append(gameTypes.get(missing.get(i)[0]).name());
                        sb.append("_");
                        sb.append(gameDifficulties.get(missing.get(i)[1]).name());
                        sb.append("_");
                        sb.append(missing.get(i)[2]);
                        sb.append(FILE_EXTENSION);
                        if(filename.equals(sb.toString())) {
                            removeList.add(missing.get(i));
                        }
                    }
                }
            }
            for(int[] i : removeList) {
                missing.remove(i);
            }

            int[][] missingArray = new int[missing.size()][3];
            missing.toArray(missingArray);

            // now generate all the missing puzzles.
            int[] m;
            while ((m = missing.poll()) != null) {
                LinkedList<int[]> deleteList = new LinkedList<>();
                final GameType gameType = gameTypes.get(m[0]);
                final GameDifficulty gameDifficulty = gameDifficulties.get(m[1]);

                int[] missingNumbers = new int[preSaves];
                int c = 0;
                missingNumbers[c++] = m[2];

                for (int j = 0; j < missing.size(); j++) {
                    if (gameType == gameTypes.get(missing.get(j)[0])
                            && gameDifficulty == gameDifficulties.get(missing.get(j)[1])) {
                        missingNumbers[c++] = missing.get(j)[2];
                        deleteList.add(m);
                    }
                }

                int amount = c;
                QQWingController qqWingController = new QQWingController();
                LinkedList<int[]> puzzleList = qqWingController.generateMultiple(gameType, gameDifficulty, amount);

                for (int p = 0; p < puzzleList.size(); p++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LEVEL_PREFIX);
                    sb.append(gameType.name());
                    sb.append("_");
                    sb.append(gameDifficulty.name());
                    sb.append("_");
                    sb.append(missingNumbers[p]);
                    sb.append(FILE_EXTENSION);
                    String filename = sb.toString();



                    // create the file
                    File file = new File(DIR, filename);

                    // convert the puzzle to a string
                    StringBuilder puzzleString = new StringBuilder();
                    for (int digit : puzzleList.get(p)) {
                        if (digit == 0) {
                            puzzleString.append(0);
                        } else {
                            puzzleString.append(Symbol.getSymbol(Symbol.SaveFormat, digit - 1));
                        }
                    }

                    // save the file
                    try {
                        FileOutputStream stream = new FileOutputStream(file);

                        try {
                            stream.write(puzzleString.toString().getBytes());
                        } finally {
                            stream.close();
                        }
                    } catch (IOException e) {
                        Log.e("File Manager", "Could not save game. IOException occured.");
                    }
                }
                for (int[] d : deleteList) {
                    missing.remove(d);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}

