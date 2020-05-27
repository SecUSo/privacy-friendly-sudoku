package org.secuso.privacyfriendlysudoku.controller.database.columns;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import org.secuso.privacyfriendlysudoku.controller.database.model.Level;



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


    public static DailySudoku getLevel(Cursor c) {
        Level level = LevelColumns.getLevel(c);
        int hintsUsed = c.getInt(c.getColumnIndexOrThrow(HINTS_USED));
        String timeNeeded = c.getString(c.getColumnIndexOrThrow(TIME_NEEDED));
        return new DailySudoku(level.getId(), level.getDifficulty(), level.getGameType(), level.getPuzzle(), hintsUsed, timeNeeded);
    }

    public static ContentValues getValues(DailySudoku record) {
        ContentValues result = LevelColumns.getValues(record);
        result.put(HINTS_USED, record.getHintsUsed());
        result.put(TIME_NEEDED, record.getTimeNeeded());

        return result;
    }

}
