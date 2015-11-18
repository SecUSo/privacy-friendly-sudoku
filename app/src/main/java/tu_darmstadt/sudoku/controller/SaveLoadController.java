package tu_darmstadt.sudoku.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;

/**
 * Created by Chris on 16.11.2015.
 */
public class SaveLoadController {

    Context context;
    private SharedPreferences settings;

    private static String FILE_EXTENSION = ".txt";
    private static String SAVE_PREFIX = "save_";
    private static String SAVES_DIR = "saves";

    private static List<GameInfoContainer> list = new LinkedList<>();

    public SaveLoadController(Context context, SharedPreferences settings) {
        this.context = context;
        this.settings = settings;
    }

    public static List<GameInfoContainer> getLoadableGameList() {
        return list;
    }

    public List<GameInfoContainer> loadGameStateInfo() {
        if(!settings.getBoolean("savesChanged", false)) {
            return list;
        }
        File dir = context.getDir(SAVES_DIR, 0);

        List<GameInfoContainer> result = new LinkedList<>();

        // go through every file
        for(File file : dir.listFiles()) {

            // filter so we only work with actual files
            if(file.isFile()) {

                // create a new GameInfoContainer
                GameInfoContainer gic = new GameInfoContainer();

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
                String[] values = gameString.split("/");

                try {
                    if(values.length < 4) {
                        throw new IllegalArgumentException("Can not load game info. File seems to be damaged or incomplete.");
                    }

                    // fill the container
                    String id = file.getName().substring(5, file.getName().lastIndexOf("."));
                    gic.setID(Integer.valueOf(id));    // save_x.txt
                    gic.parseGameType(values[0]);
                    gic.parseFixedValues(values[1]);
                    gic.parseSetValues(values[2]);
                    gic.parseNotes(values[3]);
                } catch(IllegalArgumentException e) {
                    file.delete();
                    continue;
                }

                // then add it to the list
                result.add(gic);
            }
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("savesChanged", false);
        editor.commit();

        list = result;
        return result;
    }

    public void saveGameState(GameController controller) {
        String level = GameInfoContainer.getGameInfo(controller);

        File dir = context.getDir(SAVES_DIR, 0);

        //controller.getGameID()

        File file = new File(dir, SAVE_PREFIX+String.valueOf(controller.getGameID())+FILE_EXTENSION);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(level.getBytes());
            } finally {
                stream.close();
            }
        } catch(IOException e) {
            Log.e("File Manager", "Could not save game. IOException occured.");
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("savesChanged", true);
        editor.commit();

    }
}
