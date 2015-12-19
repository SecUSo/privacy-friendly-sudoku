package tu_darmstadt.sudoku.controller.helper;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by Chris on 18.11.2015.
 */

public class HighscoreInfoContainer {

    private GameType type = null;
    private GameDifficulty difficulty = null;
    private int minTime = Integer.MAX_VALUE;
    private int time=0;
    private int numberOfHintsUsed =0;
    private int numberOfGames=0;
    private int numberOfGamesNoHints =0;
    private int timeNoHints = 0;

    private int amountOfSavedArguments = 8;

    public HighscoreInfoContainer(){

    }
    public HighscoreInfoContainer(GameType t, GameDifficulty diff){
        type =(type == null)?t:type;
        difficulty = (difficulty == null) ? diff : difficulty;
    }

    public void add(GameController gc){
        //add all wanted Game Stats
        difficulty = (difficulty== null) ? gc.getDifficulty() : difficulty;
        type = (type == null) ? gc.getGameType() : type;
        time += gc.getTime();
        numberOfHintsUsed += gc.getUsedHints();
        numberOfGames++;
        // min time is only minTime of games without hints used
        minTime = (gc.getUsedHints() == 0 && gc.getTime()< minTime) ? gc.getTime() : minTime;
        numberOfGamesNoHints = (gc.getUsedHints() == 0) ? numberOfGamesNoHints + 1 : numberOfGamesNoHints;
        timeNoHints = (gc.getUsedHints() == 0) ? timeNoHints + gc.getTime() : timeNoHints;

    }

    public void setInfosFromFile(String s){
        if(s.isEmpty()) return;
        String [] strings = s.split("/");
        if (strings.length != amountOfSavedArguments) {
            throw new IllegalArgumentException("Argument Exception");
        }
        try {
            time = parseTime(strings[0]);
            numberOfHintsUsed = parseHints(strings[1]);
            numberOfGames = parseNumberOfGames(strings[2]);
            minTime = parseTime(strings[3]);
            type = parseGameType(strings[4]);
            difficulty = parsDifficulty(strings[5]);
            numberOfGamesNoHints=parseNumberOfGames(strings[6]);
            timeNoHints = parseTime(strings[7]);

        } catch (IllegalArgumentException e){
            throw  new IllegalArgumentException("Could not set Infoprmation illegal Arguments");
        }
    }

    public GameDifficulty getDifficulty(){
        return difficulty;
    }
    public GameType getGameType(){return type;}
    public int getTime(){
        return time;
    }
    public int getMinTime(){return minTime; }
    public int getNumberOfHintsUsed(){return numberOfHintsUsed; }
    public int getNumberOfGames(){  return numberOfGames;   }
    public int getNumberOfGamesNoHints(){   return numberOfGamesNoHints; }
    public int getTimeNoHints(){    return timeNoHints; }


    private GameType parseGameType(String s){
        return GameType.valueOf(s);
    }
    private GameDifficulty parsDifficulty(String s) {
        return GameDifficulty.valueOf(s);
    }

    private int parseTime(String s){
        int ret = Integer.valueOf(s);
        if (ret<0){
            throw new IllegalArgumentException("Parser Exception wrong Integer");
        }
        return ret;
    }
    private int parseHints(String s){
        int ret = Integer.valueOf(s);
        if (ret<0){
            throw new IllegalArgumentException("Parser Exception wrong Integer");
        }
        return ret;

    }
    private int parseNumberOfGames(String s) {
        int ret = Integer.valueOf(s);
        if (ret<0){
            throw new IllegalArgumentException("Parser Exception wrong Integer");
        }
        return ret;
    }

    public String getActualStats(){
        StringBuilder sb = new StringBuilder();
        sb.append(time);
        sb.append("/");
        sb.append(numberOfHintsUsed);
        sb.append("/");
        sb.append(numberOfGames);
        sb.append("/");
        sb.append(minTime);
        sb.append("/");
        sb.append(type.name());
        sb.append("/");
        sb.append(difficulty.name());
        sb.append("/");
        sb.append(numberOfGamesNoHints);
        sb.append("/");
        sb.append(timeNoHints);


        return sb.toString();
    }
}
