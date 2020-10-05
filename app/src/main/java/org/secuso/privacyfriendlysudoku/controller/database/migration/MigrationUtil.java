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
package org.secuso.privacyfriendlysudoku.controller.database.migration;

import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import static org.secuso.privacyfriendlysudoku.controller.database.columns.DailySudokuColumns.SQL_CREATE_ENTRIES;

/**
 * @author Christopher Beckmann
 */
public class MigrationUtil {

    public static List<Migration> migrations = Arrays.asList(
            new Migration(1,2) {
                @Override
                public void migrate(SQLiteDatabase db) {
                    db.execSQL(SQL_CREATE_ENTRIES);
                }
            }
    );

    //TODO: for now just try to find the desired migration from the list.
    // -> When more migrations are added, a chain could be found, e.g. 1->2->3
    public static boolean executeMigration(SQLiteDatabase db, int from, int to) {
        for(Migration m : migrations) {
            if(m.from == from && m.to == to) {
                m.migrate(db);
                return true;
            }
        }
        return false;
    }

}
