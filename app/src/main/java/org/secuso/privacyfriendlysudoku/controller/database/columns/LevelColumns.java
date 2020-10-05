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

package org.secuso.privacyfriendlysudoku.controller.database.columns;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.controller.database.model.*;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

public class LevelColumns implements BaseColumns {

    public static final String TABLE_NAME = "levels";

    public static final String DIFFICULTY = "level_difficulty";
    public static final String GAMETYPE = "level_gametype";
    public static final String PUZZLE = "level_puzzle";

    private static final String TEXT_TYPE = " TEXT ";
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String COMMA_SEP = ",";

    public static final String[] PROJECTION = {
            _ID,
            DIFFICULTY,
            GAMETYPE,
            PUZZLE
    };

    public static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID         + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    DIFFICULTY        + TEXT_TYPE + COMMA_SEP +
                    GAMETYPE  + TEXT_TYPE + COMMA_SEP +
                    PUZZLE     + TEXT_TYPE + " )";
    public static String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static Level getLevel(Cursor c) {
        Level level = new Level();

        // *** ID ***
        level.setId(c.getInt(c.getColumnIndexOrThrow(LevelColumns._ID)));

        // *** GAME TYPE ***
        String gameTypeString = c.getString(c.getColumnIndexOrThrow(LevelColumns.GAMETYPE));
        GameType gameType = GameType.valueOf(gameTypeString);
        level.setGameType(gameType);

        // *** DIFFICULTY ***
        String difficultyString = c.getString(c.getColumnIndexOrThrow(LevelColumns.DIFFICULTY));
        level.setDifficulty(GameDifficulty.valueOf(difficultyString));

        // *** PUZZLE ***
        String puzzleString = c.getString(c.getColumnIndexOrThrow(LevelColumns.PUZZLE));
        int[] puzzle = new int[gameType.getSize()*gameType.getSize()];

        if(puzzle.length != puzzleString.length()) {
            throw new IllegalArgumentException("Saved level does not have the correct size.");
        }

        for(int i = 0; i < puzzleString.length(); i++) {
            puzzle[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(puzzleString.charAt(i)))+1;
        }
        level.setPuzzle(puzzle);

        return level;
    }

    public static ContentValues getValues(Level record) {
        ContentValues values = new ContentValues();
        if(record.getId() != -1) {
            values.put(LevelColumns._ID, record.getId());
        }
        values.put(LevelColumns.GAMETYPE, record.getGameType().name());
        values.put(LevelColumns.DIFFICULTY, record.getDifficulty().name());

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < record.getPuzzle().length; i++) {
            if (record.getPuzzle()[i] == 0) {
                sb.append(0);
            } else {
                sb.append(Symbol.getSymbol(Symbol.SaveFormat, record.getPuzzle()[i]-1));
            }
        }
        values.put(LevelColumns.PUZZLE, sb.toString());
        return values;
    }
}
