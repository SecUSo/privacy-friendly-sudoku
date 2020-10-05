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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.secuso.privacyfriendlysudoku.controller.database.DatabaseHelper;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Chris on 23.11.2015.
 */
public class NewLevelManager {

    Context context;
    private SharedPreferences settings;

    private static NewLevelManager instance;
    private DatabaseHelper dbHelper;

    private static String FILE_EXTENSION = ".txt";
    private static String LEVEL_PREFIX = "level_";
    private static String LEVELS_DIR = "level";
    private static File DIR;
    public static int PRE_SAVES_MIN = 3;
    public static int PRE_SAVES_MAX = 10;

    private final double CHALLENGE_GENERATION_PROBABILITY = 0.25;
    private final int CHALLENGE_ITERATIONS = 4;


    public static NewLevelManager getInstance(Context context, SharedPreferences settings) {
        if(instance == null) {
            instance = new NewLevelManager(context, settings);
        }
        return instance;
    }

    private NewLevelManager(Context context, SharedPreferences settings) {
        this.context = context;
        this.settings = settings;
        this.dbHelper = new DatabaseHelper(context);
        DIR = context.getDir(LEVELS_DIR, 0);
    }

    public boolean isLevelLoadable(GameType type, GameDifficulty diff) {
        return dbHelper.getLevels(diff, type).size() > 0;
    }

    @Deprecated
    public boolean isLevelLoadableOld(GameType type, GameDifficulty diff) {
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

    public int[] loadDailySudoku() {
        // create a seed from the current date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String toHash = "Sudoku/.PrivacyFriendly/." + dateFormat.format(new Date());
        QQWingController controller = new QQWingController();

        // generate new sudoku using the previously computed seed
        return controller.generateFromSeed(toHash.hashCode(), CHALLENGE_GENERATION_PROBABILITY, CHALLENGE_ITERATIONS);
    }

    public int[] loadLevel(GameType type, GameDifficulty diff) {
        Level level = dbHelper.getLevel(diff, type);
        dbHelper.deleteLevel(level.getId());
        return level.getPuzzle();

//        for(Level level : levels) {
//
//            // delete level from database
//            dbHelper.deleteLevel(level.getId());
//
//            // test if it has the correct length
//            int length = type.getSize() * type.getSize();
//            if (level.getPuzzle().length != length) {
//                // Level is not correctly saved -> discard it and try again
//                continue;
//            } else {
//                return level.getPuzzle();
//            }
//        }
//
//        // if there is no level or every level has the wrong length
//        throw new RuntimeException("No level to load with specified parameters");
    }

    @Deprecated
    public int[] loadLevelOld(GameType type, GameDifficulty diff) {
        List<int[]> result = new LinkedList<>();
        LinkedList<Integer> availableFiles = new LinkedList<>();
        Random r = new Random();

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
            int i = r.nextInt(availableFiles.size());
            int chosen = availableFiles.get(i);
            int[] resultPuzzle = result.get(i);

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
        // Start Generation Service
        Intent i = new Intent(context, GeneratorService.class);
        i.setAction(GeneratorService.ACTION_GENERATE);
        //i.putExtra(ProtocolService.EXTRA_PROTOCOL, current.componentName().flattenToString());
        context.startService(i);

        //new AsyncGenerationTask().execute();
    }

    public void loadFirstStartLevels() {
        saveToDb(GameType.Default_9x9, GameDifficulty.Moderate, "000208090027000000000400000090100706408090201000030080200001000100300469000000007");
        saveToDb(GameType.Default_9x9, GameDifficulty.Moderate, "000000052000003007500206830002040700070000046640508003000400000000005000050301008");
        saveToDb(GameType.Default_9x9, GameDifficulty.Moderate, "950710600063200500100300200078000032016000000000000050000001869029000000800003000");
        saveToDb(GameType.Default_9x9, GameDifficulty.Moderate, "679000300000000050000700020020300001000006000167240000030020004004000500001003792");
        saveToDb(GameType.Default_9x9, GameDifficulty.Moderate, "000465000000000008000320500060000709900006053000043200600000902024070600000030005");

        saveToDb(GameType.Default_9x9, GameDifficulty.Hard, "000800400008004093009003060000700000000400000060002900091000670200000100403006802");
        saveToDb(GameType.Default_9x9, GameDifficulty.Hard, "000006040000050000600040080043000200500810000100300605209080460004500000001030000");
        saveToDb(GameType.Default_9x9, GameDifficulty.Hard, "080000000040000000000071300050130700004006000003508014000000081012600405070000002");
        saveToDb(GameType.Default_9x9, GameDifficulty.Hard, "030200000008000096700600050013720009006000700802009000000903082500010070000000000");
        saveToDb(GameType.Default_9x9, GameDifficulty.Hard, "007500200120093700004007050000000305500800090040300018000000009000715000000400030");

        saveToDb(GameType.Default_9x9, GameDifficulty.Challenge, "086000000000000070090000304968027030000100060200900000072006100000000296000000740");
        saveToDb(GameType.Default_9x9, GameDifficulty.Challenge, "450900001001400000600010004006700000500000000100024060002000030000062400040005700");
        saveToDb(GameType.Default_9x9, GameDifficulty.Challenge, "100000020006020091000600000030070000260040000008010000000380060700000049040100002");
        saveToDb(GameType.Default_9x9, GameDifficulty.Challenge, "040100800059408061700000002500000009080700000007004000000000090801009200000000685");
        saveToDb(GameType.Default_9x9, GameDifficulty.Challenge, "007000050300710000140000000000500406001000907000370005790030001060004000005620000");

        saveToDb(GameType.Default_12x12, GameDifficulty.Easy, "29B0087000060070A0039001000A69000023050007000600040020080590600000002108B03C000000040A50800100C00060004000A073000056C000A009300B0200C00001A00B89");
        saveToDb(GameType.Default_12x12, GameDifficulty.Easy, "0000C5B03000003C000804000B64920005C06002080C1A500A0B0000030890000000C70440B800000007209000004030065340C0800201C0007598400080B0002600000609A30000");
        saveToDb(GameType.Default_12x12, GameDifficulty.Easy, "0000000100C0C01000678000000B804570900A35000BC80047BC0020090001203000000000000003041000500100BA7C00C950006230030274081000000A960003070500C0000000");

        saveToDb(GameType.Default_12x12, GameDifficulty.Moderate, "B30050A100701600070030800002894000007008000000B550100004020300B0000090000060000A010000000032050C0407008006A000000000400001000C290000000008005000");
        saveToDb(GameType.Default_12x12, GameDifficulty.Moderate, "00B4008A09C002A030C00008007850003000030C000408AB000B00052000000000000070069500030C00B00010467000008000000A100000000800000C0020700001700000095400");
        saveToDb(GameType.Default_12x12, GameDifficulty.Moderate, "90B08A00300100A000007B500054000B90000320000C05B0A00C0000090200050200000000000094C2000006000200303140000008006C0800000000003000C0010050007009AC60");
        saveToDb(GameType.Default_12x12, GameDifficulty.Moderate, "74000010000B000005400000900000000000000090000005000B075A0C240208B00007000009000C000207020B000860800300020190C000A000604800470009B00000A068003200");
        saveToDb(GameType.Default_12x12, GameDifficulty.Moderate, "00B000003050000AC06000700000030001800500000040000008A50402000A00100070000000069B200009130A0000000070020195C000A409000300003600000B21B00030000800");

        saveToDb(GameType.Default_12x12, GameDifficulty.Hard, "00600500000004000000002050004C00800A28049000050000A900C000000000B00000A00B0560000C900C708A00000B0002000000769000008000B002B0C6000017C00107400908");
        saveToDb(GameType.Default_12x12, GameDifficulty.Hard, "020000B000A608070530000B9050200A03800030980010B001000B6C30900000032000CAB0000000000000067000B000000500000A000C09000081302B0100070950836000100000");
        saveToDb(GameType.Default_12x12, GameDifficulty.Hard, "0A00070050B107B0060A8000005090020C0A500040001060000000A000040B00219000000107B000602C8090C000001700400A00058B00000000000620000B400090090400800000");
        saveToDb(GameType.Default_12x12, GameDifficulty.Hard, "000000908A0002900080006B0000C000007005800B2C00006020300000043B090806500C090400C000300060A700040500A00400002800000CA0000900000060B000A0B0003970C6");
        saveToDb(GameType.Default_12x12, GameDifficulty.Hard, "0070000000000020009B0100000A030000000C039002004800904005000B100B008070507040C20005A0000870B32000C5000810070980100000000000000A070C000A0500000090");

        saveToDb(GameType.Default_12x12, GameDifficulty.Challenge, "2000000000000B070C00000000AC100000000020050CA00BB60002097800079001000C60400000B0070A0000A7000298005048000010004000070009A10600C00B000C00B0000020");
        saveToDb(GameType.Default_12x12, GameDifficulty.Challenge, "01000002000C00640A800070000A0000082020000008100B0C081090000050A0060C079009BC000A04010500097000000000000000000000904000866070B100020000000C00B009");
        saveToDb(GameType.Default_12x12, GameDifficulty.Challenge, "000C0B020600400900600A020800140000C000000013050000040600900300000C0500B49A0250000000B0C0002000300143C000B0060000800064000000000B10000000020A8B09");
        saveToDb(GameType.Default_12x12, GameDifficulty.Challenge, "08060000000100000765A0000C00400000B0605001C7000019C0A4000002A000000B0040000000000100B500000080740020100005690A000000048B706000009020000020807000");
        saveToDb(GameType.Default_12x12, GameDifficulty.Challenge, "B000730000000000090040000C00B00000003A07180000C00000000CB0000000400A200040B2000000A8000020006700710A06000C00000BA0200170A700603002B0043000090060");

        saveToDb(GameType.Default_6x6, GameDifficulty.Moderate, "000000050004013000004003006050040010");
        saveToDb(GameType.Default_6x6, GameDifficulty.Moderate, "000450000600104306630000060005010000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Moderate, "020003040006000000406000000050060042");
        saveToDb(GameType.Default_6x6, GameDifficulty.Moderate, "024000510042002054000020000060600000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Moderate, "610030400001004000000200060000002600");

        saveToDb(GameType.Default_6x6, GameDifficulty.Hard, "630010002000001000040020400006003040");
        saveToDb(GameType.Default_6x6, GameDifficulty.Hard, "000000060130006000050603030005000041");
        saveToDb(GameType.Default_6x6, GameDifficulty.Hard, "502360000040430050000030650000000000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Hard, "000004300050006010001003000561000000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Hard, "000000042000003140064030000320200000");

        saveToDb(GameType.Default_6x6, GameDifficulty.Challenge, "004200200000003002600050300400046000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Challenge, "003050200003502000000000640002000045");
        saveToDb(GameType.Default_6x6, GameDifficulty.Challenge, "004030000000003020205004400000006500");
        saveToDb(GameType.Default_6x6, GameDifficulty.Challenge, "001650000320100000000060000000065000");
        saveToDb(GameType.Default_6x6, GameDifficulty.Challenge, "500004013005004020000000042500100000");
    }

    private void saveToDb(GameType gametype, GameDifficulty difficulty, String levelString) {
        Level level = new Level();
        level.setDifficulty(difficulty);
        level.setGameType(gametype);
        level.setPuzzle(levelString);
        dbHelper.addLevel(level);
    }

    @Deprecated
    public void loadFirstStartLevelsOld() {
        // Default_9x9
        // Default_12x12
        // Default_6x6

        // Easy
        // Moderate
        // Hard

        // 0
        // 1

        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 0, "000208090027000000000400000090100706408090201000030080200001000100300469000000007");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 1, "000000052000003007500206830002040700070000046640508003000400000000005000050301008");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 2, "950710600063200500100300200078000032016000000000000050000001869029000000800003000");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 3, "679000300000000050000700020020300001000006000167240000030020004004000500001003792");
        saveToFile(GameType.Default_9x9, GameDifficulty.Moderate, 4, "000465000000000008000320500060000709900006053000043200600000902024070600000030005");

        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 0, "000800400008004093009003060000700000000400000060002900091000670200000100403006802");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 1, "000006040000050000600040080043000200500810000100300605209080460004500000001030000");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 2, "080000000040000000000071300050130700004006000003508014000000081012600405070000002");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 3, "030200000008000096700600050013720009006000700802009000000903082500010070000000000");
        saveToFile(GameType.Default_9x9, GameDifficulty.Hard, 4, "007500200120093700004007050000000305500800090040300018000000009000715000000400030");

        saveToFile(GameType.Default_9x9, GameDifficulty.Challenge, 0, "086000000000000070090000304968027030000100060200900000072006100000000296000000740");
        saveToFile(GameType.Default_9x9, GameDifficulty.Challenge, 1, "450900001001400000600010004006700000500000000100024060002000030000062400040005700");
        saveToFile(GameType.Default_9x9, GameDifficulty.Challenge, 2, "100000020006020091000600000030070000260040000008010000000380060700000049040100002");
        saveToFile(GameType.Default_9x9, GameDifficulty.Challenge, 3, "040100800059408061700000002500000009080700000007004000000000090801009200000000685");
        saveToFile(GameType.Default_9x9, GameDifficulty.Challenge, 4, "007000050300710000140000000000500406001000907000370005790030001060004000005620000");

        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 0, "B30050A100701600070030800002894000007008000000B550100004020300B0000090000060000A010000000032050C0407008006A000000000400001000C290000000008005000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 1, "00B4008A09C002A030C00008007850003000030C000408AB000B00052000000000000070069500030C00B00010467000008000000A100000000800000C0020700001700000095400");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 2, "90B08A00300100A000007B500054000B90000320000C05B0A00C0000090200050200000000000094C2000006000200303140000008006C0800000000003000C0010050007009AC60");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 3, "74000010000B000005400000900000000000000090000005000B075A0C240208B00007000009000C000207020B000860800300020190C000A000604800470009B00000A068003200");
        saveToFile(GameType.Default_12x12, GameDifficulty.Moderate, 4, "00B000003050000AC06000700000030001800500000040000008A50402000A00100070000000069B200009130A0000000070020195C000A409000300003600000B21B00030000800");

        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 0, "00600500000004000000002050004C00800A28049000050000A900C000000000B00000A00B0560000C900C708A00000B0002000000769000008000B002B0C6000017C00107400908");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 1, "020000B000A608070530000B9050200A03800030980010B001000B6C30900000032000CAB0000000000000067000B000000500000A000C09000081302B0100070950836000100000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 2, "0A00070050B107B0060A8000005090020C0A500040001060000000A000040B00219000000107B000602C8090C000001700400A00058B00000000000620000B400090090400800000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 3, "000000908A0002900080006B0000C000007005800B2C00006020300000043B090806500C090400C000300060A700040500A00400002800000CA0000900000060B000A0B0003970C6");
        saveToFile(GameType.Default_12x12, GameDifficulty.Hard, 4, "0070000000000020009B0100000A030000000C039002004800904005000B100B008070507040C20005A0000870B32000C5000810070980100000000000000A070C000A0500000090");

        saveToFile(GameType.Default_12x12, GameDifficulty.Challenge, 0, "2000000000000B070C00000000AC100000000020050CA00BB60002097800079001000C60400000B0070A0000A7000298005048000010004000070009A10600C00B000C00B0000020");
        saveToFile(GameType.Default_12x12, GameDifficulty.Challenge, 1, "01000002000C00640A800070000A0000082020000008100B0C081090000050A0060C079009BC000A04010500097000000000000000000000904000866070B100020000000C00B009");
        saveToFile(GameType.Default_12x12, GameDifficulty.Challenge, 2, "000C0B020600400900600A020800140000C000000013050000040600900300000C0500B49A0250000000B0C0002000300143C000B0060000800064000000000B10000000020A8B09");
        saveToFile(GameType.Default_12x12, GameDifficulty.Challenge, 3, "08060000000100000765A0000C00400000B0605001C7000019C0A4000002A000000B0040000000000100B500000080740020100005690A000000048B706000009020000020807000");
        saveToFile(GameType.Default_12x12, GameDifficulty.Challenge, 4, "B000730000000000090040000C00B00000003A07180000C00000000CB0000000400A200040B2000000A8000020006700710A06000C00000BA0200170A700603002B0043000090060");

        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 0, "000000050004013000004003006050040010");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 1, "000450000600104306630000060005010000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 2, "020003040006000000406000000050060042");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 3, "024000510042002054000020000060600000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Moderate, 4, "610030400001004000000200060000002600");

        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 0, "630010002000001000040020400006003040");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 1, "000000060130006000050603030005000041");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 2, "502360000040430050000030650000000000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 3, "000004300050006010001003000561000000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Hard, 4, "000000042000003140064030000320200000");

        saveToFile(GameType.Default_6x6, GameDifficulty.Challenge, 0, "004200200000003002600050300400046000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Challenge, 1, "003050200003502000000000640002000045");
        saveToFile(GameType.Default_6x6, GameDifficulty.Challenge, 2, "004030000000003020205004400000006500");
        saveToFile(GameType.Default_6x6, GameDifficulty.Challenge, 3, "001650000320100000000060000000065000");
        saveToFile(GameType.Default_6x6, GameDifficulty.Challenge, 4, "500004013005004020000000042500100000");
    }

    /** Don't use this if you don't know what you are doing! **/
    @Deprecated
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
            int preSaves = PRE_SAVES_MIN;
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

