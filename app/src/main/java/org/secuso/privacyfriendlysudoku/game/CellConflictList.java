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

import java.util.ArrayList;

/**
 * Created by Chris on 10.11.2015.
 */
public class CellConflictList extends ArrayList<CellConflict> {

    /**
     * Adds the CellConflict to the list.
     * We don't allow double entries.
     * @param object the object to be added
     * @return true if it could be added, false otherwise
     */
    @Override
    public boolean add(CellConflict object) {
        if(!contains(object)) {
            return super.add(object);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[List ");
        for(int i = 0; i < size(); i++) {
            sb.append(get(i).toString());
            if(i+1 < size()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
