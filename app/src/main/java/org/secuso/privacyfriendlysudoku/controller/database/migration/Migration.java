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

/**
 * Similar idea to room migration class.
 * @author Christopher Beckmann
 */
public abstract class Migration {
    int from = 0;
    int to = 0;

    public Migration(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public abstract void migrate(SQLiteDatabase db);
}
