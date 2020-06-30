package org.secuso.privacyfriendlysudoku.ui.view;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 15.11.2015.
 */
public enum CreateSudokuButtonType {
    Unspecified(R.drawable.ic_accessibility_black_48dp),// placeholder
    Value(R.drawable.ic_accessibility_black_48dp), // should be non picture
    Do(R.drawable.ic_redo_black_48dp),
    Undo(R.drawable.ic_undo_black_48dp),
    NoteToggle(R.drawable.ic_create_black_48dp),
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
        result.add(NoteToggle);
        return result;
    }
    public static String getName(CreateSudokuButtonType type) {
        switch (type) {
            case Do: return "Do";
            case Undo: return "Un";
            case Finalize: return "Fi";
            case NoteToggle: return "On";
            case Spacer: return "";
            case Delete: return "Del";
            default:return "NotSet";
        }
    }
}


