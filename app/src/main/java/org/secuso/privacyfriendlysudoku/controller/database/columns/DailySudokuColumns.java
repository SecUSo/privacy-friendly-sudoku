/*
 * qqwing - Sudoku solver and generator
 * Copyright (C) 2014 Stephen Ostermiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.secuso.privacyfriendlysudoku.controller.database.columns;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;


/**
 * Defines a database schema for saving daily sudokus
 */
public class DailySudokuColumns extends LevelColumns {

    public static final String TABLE_NAME = "ds_levels";

    public static final String HINTS_USED = "ds_hints_used";
    public static final String TIME_NEEDED = "ds_time_needed";
    public static final String[] PROJECTION = {
            _ID,
            DIFFICULTY,
            GAMETYPE,
            PUZZLE,
            HINTS_USED,
            TIME_NEEDED
    };

    private static final String TEXT_TYPE = " TEXT ";
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String TIME_TYPE = " TIME (0) ";
    private static final String COMMA_SEP = ",";

    public static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID         + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    DIFFICULTY        + TEXT_TYPE + COMMA_SEP +
                    GAMETYPE  + TEXT_TYPE + COMMA_SEP +
                    PUZZLE     + TEXT_TYPE + COMMA_SEP +
                    HINTS_USED  + INTEGER_TYPE + COMMA_SEP +
                    TIME_NEEDED + TIME_TYPE + " )";

    public static String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    /**
     * Create a new DailySudoku object using the data stored in a specific database row
     * @param c the cursor pointing to the row whose data should be used
     * @return the DailySudoku object created using the data from the database row
     */
    public static DailySudoku getLevel(Cursor c) {
        Level level = LevelColumns.getLevel(c);
        int hintsUsed = c.getInt(c.getColumnIndexOrThrow(HINTS_USED));
        String timeNeeded = c.getString(c.getColumnIndexOrThrow(TIME_NEEDED));
        return new DailySudoku(level.getId(), level.getDifficulty(), level.getGameType(), level.getPuzzle(), hintsUsed, timeNeeded);
    }

    /**
     * Given a specific DailySudoku instance, extracts all relevant parameters and saves them to a ContentValues object
     * @param record the DailySudoku instance whose parameters should be extracted
     * @return the ContentValues instance containing the extracted parameters
     */
    public static ContentValues getValues(DailySudoku record) {
        ContentValues result = LevelColumns.getValues(record);
        result.put(HINTS_USED, record.getHintsUsed());
        result.put(TIME_NEEDED, record.getTimeNeeded());

        return result;
    }

}
