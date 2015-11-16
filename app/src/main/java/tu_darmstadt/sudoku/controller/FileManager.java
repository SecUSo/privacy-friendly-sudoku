package tu_darmstadt.sudoku.controller;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;

import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 16.11.2015.
 */
public class FileManager {

    Context context;
    private static String savesFile = "saves.txt";
    private static String highscoresFile = "highscores.txt";

    FileManager(Context context) {
        this.context = context;
    }

    public String loadGameState() {
        File dir = context.getFilesDir();

        File file = new File(dir, savesFile);

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
        String saves = new String(bytes);
        String[] levels = saves.split("###");
        for(String level : levels) {
            String[] values = level.split("|");
            GameType type = Enum.valueOf(GameType.class, values[0]);

        }


        return saves;
    }

    public void saveGameState(GameController controller) {
        String level = controller.getStringRepresentation();

        File dir = context.getFilesDir();

        File file = new File(dir, savesFile);

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
    }
}
