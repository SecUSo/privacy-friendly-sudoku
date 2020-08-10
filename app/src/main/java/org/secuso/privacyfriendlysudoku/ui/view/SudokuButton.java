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

package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.Button;

/**
 * Created by TMZ_LToP on 12.11.2015.
 */



public class SudokuButton extends Button {

    private int value = -1;
    private SudokuButtonType type = SudokuButtonType.Unspecified;

    public SudokuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValue(int value)             { this.value = value; }
    public void setType(SudokuButtonType type)  { this.type = type; }
    public int getValue ()                      { return value; }
    public SudokuButtonType getType()           { return type; }


}


