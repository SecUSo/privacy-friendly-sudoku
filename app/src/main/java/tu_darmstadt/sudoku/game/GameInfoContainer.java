package tu_darmstadt.sudoku.game;

import android.util.Log;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by Chris on 17.11.2015.
 */
public class GameInfoContainer {

    GameType gameType;
    int ID;
    int time;
    int difficulty;
    int[] fixedValues;
    int[] setValues;
    boolean[][] setNotes;

    public GameInfoContainer() {}
    public GameInfoContainer(int ID, GameType gameType, int[] fixedValues, int[] setValues, boolean[][] setNotes) {
        this.ID = ID;
        this.gameType = gameType;
        this.fixedValues = fixedValues;
        this.setValues = setValues;
        this.setNotes = setNotes;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void parseGameType(String s) {
        gameType = Enum.valueOf(GameType.class, s);
    }

    public void parseFixedValues(String s){
        fixedValues = new int[s.length()];
        for(int i = 0; i < s.length(); i++) {
               fixedValues[i] = Integer.parseInt(s.charAt(i)+"");
        }
    }

    public void parseSetValues(String s) {
        setValues = new int[s.length()];
        for(int i = 0; i < s.length(); i++) {
            setValues[i] = Integer.parseInt(s.charAt(i)+"");
        }
    }

    public void parseNotes(String s) {
        String[] strings = s.split("-");
        setNotes = new boolean[strings.length][strings[0].length()];
        for(int i = 0; i < strings.length; i++) {
            for(int k = 0; k < strings[i].length(); k++) {
                setNotes[i][k] = (strings[i].charAt(k)) == '1' ? true : false;
            }
        }
    }

    public GameType getGameType() {
        return gameType;
    }

    public int[] getFixedValues() {
        return fixedValues;
    }

    public int[] getSetValues() {
        return setValues;
    }

    public boolean[][] getSetNotes() {
        return setNotes;
    }

    public int getID() {
        return ID;
    }

    public static String getGameInfo(GameController controller) {
        StringBuilder sb = new StringBuilder();

        // TODO add some game information
        sb.append(controller.getGameType().name());
        sb.append("/");
        sb.append(getFixedCells(controller));
        sb.append("/");
        sb.append(getSetCells(controller));
        sb.append("/");
        sb.append(getNotes(controller));

        String result = sb.toString();

        Log.d("getGameInfo", result);

        return result;
    }

    private static String getFixedCells(GameController controller) {
        StringBuilder sb = new StringBuilder();
        controller.actionOnCells(new ICellAction<StringBuilder>() {
            @Override
            public StringBuilder action(GameCell gc, StringBuilder existing) {
                if (gc.isFixed()) {
                    existing.append(gc.getValue());
                } else {
                    existing.append(0);
                }
                return existing;
            }
        }, sb);
        return sb.toString();
    }

    private static String getSetCells(GameController controller) {
        StringBuilder sb = new StringBuilder();
        controller.actionOnCells(new ICellAction<StringBuilder>() {
            @Override
            public StringBuilder action(GameCell gc, StringBuilder existing) {
                if (gc.isFixed()) {
                    existing.append(0);
                } else {
                    existing.append(gc.getValue());
                }
                return existing;
            }
        }, sb);
        return sb.toString();
    }

    private static String getNotes(GameController controller) {
        StringBuilder sb = new StringBuilder();
        controller.actionOnCells(new ICellAction<StringBuilder>() {
            @Override
            public StringBuilder action(GameCell gc, StringBuilder existing) {
                for (Boolean b : gc.getNotes()) {
                    existing.append( b ? '1' : '0' );
                }
                existing.append("-");
                return existing;
            }
        }, sb);
        sb.deleteCharAt(sb.lastIndexOf("-"));
        return sb.toString();
    }





}
