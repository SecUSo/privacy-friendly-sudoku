package tu_darmstadt.sudoku.controller.helper;

import android.util.Log;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.controller.Symbol;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.game.ICellAction;

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
        if(gameType != GameType.Unspecified && gameType != null) {
            int size = GameType.getSize(gameType);
            int sq = size*size;

            if(s.length() != sq) {
                throw new IllegalArgumentException("The string must be "+sq+" characters long.");
            }
        }
        fixedValues = new int[s.length()];
        for(int i = 0; i < s.length(); i++) {
               fixedValues[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(s.charAt(i)))+1;
        }
    }

    public void parseSetValues(String s) {
        if(gameType != GameType.Unspecified && gameType != null) {
            int size = GameType.getSize(gameType);
            int sq = size*size;

            if(s.length() != sq) {
                throw new IllegalArgumentException("The string must be "+sq+" characters long.");
            }
        }
        setValues = new int[s.length()];
        for(int i = 0; i < s.length(); i++) {
            setValues[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(s.charAt(i)))+1;
        }
    }

    public void parseNotes(String s) {
        String[] strings = s.split("-");

        int size = GameType.getSize(gameType);
        int sq = size*size;

        if(gameType != GameType.Unspecified && gameType != null) {
            if(strings.length != sq) {
                throw new IllegalArgumentException("The string array must have "+sq+" entries.");
            }
        }

        setNotes = new boolean[strings.length][strings[0].length()];
        for(int i = 0; i < strings.length; i++) {
            if(strings[i].length() != size) {
                throw new IllegalArgumentException("The string must be "+size+" characters long.");
            }
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
                    existing.append(Symbol.getSymbol(Symbol.SaveFormat, gc.getValue() - 1));
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
                if (gc.isFixed() || gc.getValue() == 0) {
                    existing.append(0);
                } else {
                    existing.append(Symbol.getSymbol(Symbol.SaveFormat, gc.getValue() - 1));
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
