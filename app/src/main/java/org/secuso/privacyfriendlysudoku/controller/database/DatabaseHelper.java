package org.secuso.privacyfriendlysudoku.controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.secuso.privacyfriendlysudoku.controller.database.columns.LevelColumns;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Database.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LevelColumns.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LevelColumns.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public List<Level> getLevels(GameDifficulty difficulty, GameType gameType) {
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

    public Level getLevel(GameDifficulty difficulty, GameType gameType) {
        List<Level> levelList = getLevels(difficulty, gameType);
        if(levelList.size() == 0) {
            throw new IllegalArgumentException("There is no level");
        }
        return levelList.get(0);
    }

    public void deleteLevel(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String selection = LevelColumns._ID + " = ?";
        String[] selectionArgs = {id+""};

        database.delete(LevelColumns.TABLE_NAME, selection, selectionArgs);
    }

    public long addLevel(Level level) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(LevelColumns.TABLE_NAME, null, LevelColumns.getValues(level));
    }
}

