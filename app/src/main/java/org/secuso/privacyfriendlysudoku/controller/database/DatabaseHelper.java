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
package org.secuso.privacyfriendlysudoku.controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.secuso.privacyfriendlysudoku.controller.database.columns.DailySudokuColumns;
import org.secuso.privacyfriendlysudoku.controller.database.columns.LevelColumns;
import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Database.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LevelColumns.SQL_CREATE_ENTRIES);
        db.execSQL(DailySudokuColumns.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LevelColumns.SQL_DELETE_ENTRIES);
        db.execSQL(DailySudokuColumns.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public synchronized List<Level> getLevels(GameDifficulty difficulty, GameType gameType) {
        if(difficulty == null || gameType == null) {
            throw new IllegalArgumentException("Arguments may not be null");
        }

        List<Level> levelList = new LinkedList<Level>();

        SQLiteDatabase database = getWritableDatabase();

        String selection = LevelColumns.DIFFICULTY + " = ? AND " + LevelColumns.GAMETYPE + " = ?";
        String[] selectionArgs = {difficulty.name(), gameType.name()};

        // How you want the results sorted in the resulting Cursor
        Cursor c = database.query(
                LevelColumns.TABLE_NAME,         // The table to query
                LevelColumns.PROJECTION,                // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                    // The sort order
        );

        if (c != null) {
            while(c.moveToNext()) {
                levelList.add(LevelColumns.getLevel(c));
            }
        }

        c.close();
        return levelList;
    }

    public synchronized Level getLevel(GameDifficulty difficulty, GameType gameType) {
        List<Level> levelList = getLevels(difficulty, gameType);
        if(levelList.size() == 0) {
            throw new IllegalArgumentException("There is no level");
        }
        return levelList.get(0);
    }

    /**
     * Returns a list of all the daily sudokus that have been solved and thus saved to the database
     * @return a list of all the daily sudokus that have been solved so far
     */
    public synchronized List<DailySudoku> getDailySudokus() {
        List<DailySudoku> dailySudokuList = new LinkedList<>();
        SQLiteDatabase database = getWritableDatabase();

        // order results from most to least recent
        String order = DailySudokuColumns._ID + " DESC";

        // How you want the results sorted in the resulting Cursor
        Cursor c = database.query(
                DailySudokuColumns.TABLE_NAME,         // The table to query
                DailySudokuColumns.PROJECTION,                // The columns to return
                null,                                // select all rows
                null,                            // select all rows
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                order                                    // The sort order
        );

        if (c != null) {
            while(c.moveToNext()) {
                dailySudokuList.add(DailySudokuColumns.getLevel(c));
            }
        }

        c.close();
        return dailySudokuList;

    }

    public synchronized void deleteLevel(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String selection = LevelColumns._ID + " = ?";
        String[] selectionArgs = {id+""};

        database.delete(LevelColumns.TABLE_NAME, selection, selectionArgs);
    }

    public synchronized long addLevel(Level level) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(LevelColumns.TABLE_NAME, null, LevelColumns.getValues(level));
    }

    /**
     * Adds a new daily sudoku to the database
     * @param ds the daily sudoku which is to be added to the database
     * @return the row id of the newly inserted sudoku (or -1 if an error occurred)
     */
    public synchronized long addDailySudoku(DailySudoku ds) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(DailySudokuColumns.TABLE_NAME, null, DailySudokuColumns.getValues(ds));
    }
}

