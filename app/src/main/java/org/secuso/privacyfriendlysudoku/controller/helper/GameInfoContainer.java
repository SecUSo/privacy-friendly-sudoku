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
package org.secuso.privacyfriendlysudoku.controller.helper;

import android.util.Log;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.game.GameCell;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.game.ICellAction;

import java.util.Date;

/**
 * Created by Chris on 17.11.2015.
 */
public class GameInfoContainer {

    GameType gameType;
    int ID;
    int timePlayed;
    Date lastTimePlayed;
    GameDifficulty difficulty;
    int[] fixedValues;
    int[] setValues;
    boolean[][] setNotes;
    int hintsUsed;
    boolean isCustom;

    public GameInfoContainer() {}
    public GameInfoContainer(int ID, GameDifficulty difficulty, GameType gameType, int[] fixedValues, int[] setValues, boolean[][] setNotes) {
        this(ID, difficulty, new Date(), 0, gameType, fixedValues, setValues, setNotes, 0);
    }
    public GameInfoContainer(int ID, GameDifficulty difficulty, Date lastTimePlayed, int timePlayed, GameType gameType, int[] fixedValues, int[] setValues, boolean[][] setNotes, int hintsUsed) {
        this.ID = ID;
        this.timePlayed = timePlayed;
        this.difficulty = difficulty;
        this.gameType = gameType;
        this.lastTimePlayed = lastTimePlayed;
        this.fixedValues = fixedValues;
        this.setValues = setValues;
        this.setNotes = setNotes;
        this.hintsUsed = hintsUsed;
        isCustom = false;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCustom (boolean isCustom) { this.isCustom = isCustom; }

    public boolean isCustom () { return  isCustom; }

    public void parseGameType(String s) {
        gameType = Enum.valueOf(GameType.class, s);
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public Date getLastTimePlayed() {
        return lastTimePlayed;
    }

    public void parseTime(String s) {
        try {
            this.timePlayed = Integer.parseInt(s);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("GameInfoContainer: Can not parse time.", e);
        }
    }

    public void parseHintsUsed(String s) {
        try {
            this.hintsUsed = Integer.parseInt(s);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("GameInfoContainer: Can not parse hints used.", e);
        }
    }

    public void parseDate(String s) {
        try {
            this.lastTimePlayed = new Date(Long.parseLong(s));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("GameInfoContainer: LastTimePlayed Date can not be extracted.", e);
        }
    }

    public void parseDifficulty(String s) {
        difficulty = Enum.valueOf(GameDifficulty.class, s);
    }

    public void parseFixedValues(String s){
        if(gameType != GameType.Unspecified && gameType != null) {
            int size = gameType.getSize();
            int sq = size*size;

            if(s.length() != sq) {
                throw new IllegalArgumentException("The string must be "+sq+" characters long.");
            }
        }
        fixedValues = new int[s.length()];
        for(int i = 0; i < s.length(); i++) {
               fixedValues[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(s.charAt(i)))+1;
               if (gameType != GameType.Unspecified && gameType != null) {
                   if (fixedValues[i] < 0 || fixedValues[i] > gameType.getSize()) {
                       throw new IllegalArgumentException("Fixed values must each be smaller than " + gameType.getSize() + ".");
                   }
               }
        }
    }

    public void parseSetValues(String s) {
        if(gameType != GameType.Unspecified && gameType != null) {
            int size = gameType.getSize();
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

        int size = gameType.getSize();
        int sq = size*size;

        if(gameType != GameType.Unspecified) {
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
                setNotes[i][k] = (strings[i].charAt(k)) == '1';
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

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public int getID() {
        return ID;
    }

    public int getHintsUsed() { return hintsUsed; }

    public static String getGameInfo(GameController controller) {
        StringBuilder sb = new StringBuilder();
        Date today = new Date();
        boolean custom = controller.gameIsCustom();

        sb.append(controller.getGameType().name());
        sb.append("/");
        sb.append(controller.getTime());
        sb.append("/");
        sb.append(today.getTime());
        sb.append("/");
        sb.append(controller.getDifficulty().name());
        sb.append("/");
        sb.append(getFixedCells(controller));
        sb.append("/");
        sb.append(getSetCells(controller));
        sb.append("/");
        sb.append(getNotes(controller));
        sb.append("/");
        sb.append(controller.getUsedHints());

        // add additional information to custom sudokus to ensure they can be distinguished from 'regular' sudokus
        if (custom) {
            sb.append("/");
            sb.append(custom);
        }

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
