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
package org.secuso.privacyfriendlysudoku.controller;

/**
 * Created by Chris on 17.11.2015.
 */
public enum Symbol {

    SaveFormat(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P"}),
    Default(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N"}),
    Roman(new String[] {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII"}),
    Fancy(new String[] {"♪", "♫", "☼", "♥", "♦", "♣", "♠", "•", "○", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N" }),
    Chinese(new String[] {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六" }),
    Greek(new String[] {"α", "β", "γ", "δ", "ε", "ϛ", "ζ", "η", "θ", "ι", "ια", "ιβ", "ιγ", "ιδ", "ιε", "ιϛ", "ιζ", "ιη", "ιθ", "κ"}),
    Indian(new String[] {"१", "२", "३", "४", "५", "६", "७", "८", "९", "१०", "११", "१२", "१३", "१४", "१५", "१६", "१७"});

    private String[] map;

    Symbol(String[] map) {
        this.map = map;
    }

    public static String getSymbol(Symbol type, int value) {
        return (type.map[value]);
    }

    public static int getValue(Symbol type, String c) {
        for(int i = 0; i < type.map.length; i++) {
            if(type.map[i].equals(c)) return i;
        }
        return -1;
    }

}
