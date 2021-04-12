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
package org.secuso.privacyfriendlysudoku.game;

/**
 * Created by Chris on 09.11.2015.
 */
public class GameSettings {
    private static boolean enableAutomaticNoteDeletion = true;
    private static boolean highlightConnectedRow = true;
    private static boolean highlightConnectedColumn = true;
    private static boolean highlightConnectedSection = true;


    public static boolean getEnableAutomaticNoteDeletion() {
        return enableAutomaticNoteDeletion;
    }
    public static boolean getHighlightConnectedRow() {
        return highlightConnectedRow;
    }
    public static boolean getHighlightConnectedColumn() {
        return highlightConnectedColumn;
    }
    public static boolean getHighlightConnectedSection() {
        return highlightConnectedSection;
    }
}
