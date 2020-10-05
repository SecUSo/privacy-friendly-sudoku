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
package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class CreateSudokuSpecialButton extends androidx.appcompat.widget.AppCompatImageButton {
    private int value = -1;
    private CreateSudokuButtonType type = CreateSudokuButtonType.Unspecified;

    public CreateSudokuSpecialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValue(int value)             { this.value = value; }
    public void setType(CreateSudokuButtonType type)  { this.type = type; }
    public int getValue ()                      { return value; }
    public CreateSudokuButtonType getType()           { return type; }

}
