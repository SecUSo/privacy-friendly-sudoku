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

import androidx.annotation.DrawableRes;

import org.secuso.privacyfriendlysudoku.R;

import java.util.ArrayList;
import java.util.List;

public enum CreateSudokuButtonType {
    Unspecified(R.drawable.ic_accessibility_black_48dp),// placeholder
    Value(R.drawable.ic_accessibility_black_48dp), // should be non picture
    Do(R.drawable.ic_redo_black_48dp),
    Undo(R.drawable.ic_undo_black_48dp),
    Import(R.drawable.ic_import),
    Spacer(R.drawable.ic_accessibility_black_48dp),//placeholder
    Delete(R.drawable.ic_delete_black_48dp),
    Finalize(R.drawable.ic_finalize),
    Reset(R.drawable.ic_settings_backup_restore_black_48dp);


    private int resID;

    CreateSudokuButtonType(@DrawableRes int res){
        this.resID = res;
    }

    public int getResID() {
        return resID;
    }

    public static List<CreateSudokuButtonType> getSpecialButtons() {
        ArrayList<CreateSudokuButtonType> result = new ArrayList<CreateSudokuButtonType>();
        result.add(Undo);
        result.add(Do);
        result.add(Finalize);
        //result.add(Spacer);
        result.add(Delete);
        result.add(Import);
        return result;
    }
    public static String getName(CreateSudokuButtonType type) {
        switch (type) {
            case Do: return "Do";
            case Undo: return "Un";
            case Finalize: return "Fi";
            case Import: return "Im";
            case Spacer: return "";
            case Delete: return "Del";
            default:return "NotSet";
        }
    }
}


