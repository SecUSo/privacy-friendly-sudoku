package org.secuso.privacyfriendlysudoku.controller;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.secuso.privacyfriendlysudoku.controller.helper.HighscoreInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.game.listener.ITimerListener;

/**
 * Created by TMZ_LToP on 19.11.2015.
 */
public class SaveLoadStatistics implements ITimerListener {


    private static String FILE_EXTENSION = ".txt";
    private static String SAVE_PREFIX = "stat";
    private static String SAVES_DIR = "stats";
    private GameController gc;
    Context context;
    private int numberOfArguents = 2;

    //GameDifficulty, time, gamemode, #hints, AvTime, amountOf Games per GameDifficulty,
    public SaveLoadStatistics(Context context){
        this.context = context;
    }
    public void setGameController(GameController gc) {
        this.gc = gc;
        gc.registerTimerListener(this);
    }

    public HighscoreInfoContainer loadStats(GameType t, GameDifficulty gd){
        File dir = context.getDir(SAVES_DIR, 0);
        HighscoreInfoContainer infos;
        byte[] bytes;
        FileInputStream inputStream;
        File file;
        file = new File(dir,SAVE_PREFIX+t.name()+"_"+gd.name()+FILE_EXTENSION);
        bytes = new byte[(int)file.length()];

        try {
            inputStream = new FileInputStream(file);
            try {
                inputStream.read(bytes);
            }finally {
                inputStream.close();
            }
        }  catch (IOException e) {
            Log.e("Failed to read file","File could not be read");
        }
        infos = new HighscoreInfoContainer(t,gd);
        try {
            infos.setInfosFromFile(new String(bytes));
        } catch (IllegalArgumentException e){
            file.delete();
        }
        return infos;

    }

    public List<HighscoreInfoContainer> loadStats(GameType t) {
        File dir = context.getDir(SAVES_DIR, 0);
        List<GameDifficulty> difficulties = GameDifficulty.getValidDifficultyList();
        List<HighscoreInfoContainer> result = new ArrayList<>();
        HighscoreInfoContainer infos;
        byte[] bytes;
        FileInputStream inputStream;
        File file;
        for (GameDifficulty dif : difficulties){
            file = new File(dir,SAVE_PREFIX+t.name()+"_"+dif.name()+FILE_EXTENSION);
            bytes = new byte[(int)file.length()];
            try {
                inputStream = new FileInputStream(file);
                try {
                    inputStream.read(bytes);
                }finally {
                    inputStream.close();
                }
            }  catch (IOException e) {
                Log.e("Failed to read file","File could not be read");
            }
            infos = new HighscoreInfoContainer(t,dif);
            try {
            infos.setInfosFromFile(new String(bytes));
            } catch (IllegalArgumentException e){
                file.delete();
            }
                result.add(infos);
        }



        return result;
    }

    public static void resetStats(Context context) {


        File dir = context.getDir(SAVES_DIR, 0);
        File file;
        for(GameType t: GameType.getValidGameTypes()){
            for(GameDifficulty d : GameDifficulty.getValidDifficultyList()){
                file = new File(dir,SAVE_PREFIX+t.name()+"_"+d.name()+FILE_EXTENSION);
                file.delete();
            }
        }

    }

    public void saveTime(GameDifficulty gd, GameType gameType) {
        //TODO: Increse time every second
        HighscoreInfoContainer infos = loadStats(gameType, gd);
        infos.incTime();
        saveContainer(infos,gd,gameType);


    }
    public void saveHints(GameDifficulty gd, GameType gameType){
        HighscoreInfoContainer infos = loadStats(gameType,gd);
        infos.incHints();
        saveContainer(infos,gd,gameType);
    }

    public void saveContainer (HighscoreInfoContainer infos, GameDifficulty gd, GameType t){

        File dir = context.getDir(SAVES_DIR, 0);
        File file = new File(dir, SAVE_PREFIX+t.name()+"_"+gd.name()+FILE_EXTENSION);


        String stats = infos.getActualStats();
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(stats.getBytes());
            } finally {
                stream.close();
            }
        } catch(IOException e) {
            Log.e("File Manager", "Could not save game. IOException occured.");
        }
    }

    public void saveGameStats() {

        if (gc == null) return;

        HighscoreInfoContainer infoContainer = new HighscoreInfoContainer();

        // Read existing stats
        File dir = context.getDir(SAVES_DIR, 0);

        File file = new File(dir, SAVE_PREFIX+gc.getGameType().name()+"_"+gc.getDifficulty().name()+FILE_EXTENSION);


        if (file.isFile()){
            byte[] bytes = new byte[(int)file.length()];
            try {
                FileInputStream stream = new FileInputStream(file);
                try {
                    stream.read(bytes);
                } finally {
                    stream.close();
                }
            }catch (IOException e) {
            Log.e("Stats load to save game","error while load old game Stats");
        }

        String fileStats = new String(bytes);
        if (!fileStats.isEmpty()) {
            try {
                infoContainer.setInfosFromFile(fileStats);

            } catch (IllegalArgumentException e) {
                Log.e("Parse Error","Illegal Atgumanet");
            }
        }

        }

        //add stats of current game stats or create init stats
        infoContainer.add(gc);

        String stats = infoContainer.getActualStats();
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(stats.getBytes());
            } finally {
                stream.close();
            }
        } catch(IOException e) {
            Log.e("File Manager", "Could not save game. IOException occured.");
        }
    }

    @Override
    public void onTick(int time) {
        saveTime(gc.getDifficulty(), gc.getGameType());
        //gc.getUsedHints();
    }
}
