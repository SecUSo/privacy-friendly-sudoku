package tu_darmstadt.sudoku.controller;

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

import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 23.11.2015.
 */
public class SaveLoadLevelManager {

    Context context;
    private SharedPreferences settings;

    private static SaveLoadLevelManager instance;

    private static String FILE_EXTENSION = ".txt";
    private static String LEVEL_PREFIX = "level_";
    private static String LEVELS_DIR = "level";
    private static File DIR;

    public static SaveLoadLevelManager getInstance() {
        return instance;
    }
    public static SaveLoadLevelManager init(Context context, SharedPreferences settings) {
        if(instance == null) {
            instance = new SaveLoadLevelManager(context, settings);
        }
        return instance;
    }

    private SaveLoadLevelManager(Context context, SharedPreferences settings) {
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

        // TODO: make the UI wait. Or just generate a level now.
        return null;
    }

    public void checkAndRestock() {
        new AsyncGenerationTask().execute();
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

