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

package org.secuso.privacyfriendlysudoku.game;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 08.11.2015.
 */
public class CellConflict implements Parcelable {

    /*
     * A conflict is created for every cell.
     */
    private GameCell c1 = null;
    private GameCell c2 = null;

    public int getRowCell1() {
        return c1.getRow();
    }
    public int getRowCell2() {
        return c2.getRow();
    }
    public int getColCell1() {
        return c1.getCol();
    }
    public int getColCell2() {
        return c2.getCol();
    }

    public GameCell getCell1() { return c1; }
    public GameCell getCell2() { return c2; }

    public CellConflict(GameCell first, GameCell second) {
        c1 = first;
        c2 = second;
    }

    public boolean contains(GameCell c) {
        return c1.equals(c) || c2.equals(c);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof CellConflict)) {
            return false;
        }
        if(!(c1.equals(((CellConflict) other).c1) && c2.equals(((CellConflict) other).c2)
                || c1.equals(((CellConflict) other).c2) && c2.equals(((CellConflict) other).c1))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Conflict ");
        sb.append(c1.toString());
        sb.append(" ");
        sb.append(c2.toString());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(c1, 0);
        dest.writeParcelable(c2, 0);
    }

    public static final Parcelable.Creator<CellConflict> CREATOR
            = new Parcelable.Creator<CellConflict>() {
        public CellConflict createFromParcel(Parcel in) {
            return new CellConflict(in);
        }

        public CellConflict[] newArray(int size) {
            return new CellConflict[size];
        }
    };

    /** recreate object from parcel */
    private CellConflict(Parcel in) {
        c1 = in.readParcelable(GameCell.class.getClassLoader());
        c2 = in.readParcelable(GameCell.class.getClassLoader());
    }
}
