package org.secuso.privacyfriendlysudoku.ui.view;

import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 15.11.2015.
 */
public enum SudokuButtonType {
    Unspecified(R.drawable.ic_accessibility_black_48dp),// placeholder
    Value(R.drawable.ic_accessibility_black_48dp), // should be non picture
    Do(R.drawable.ic_redo_black_48dp),
    Undo(R.drawable.ic_undo_black_48dp),
    Hint(R.drawable.ic_lightbulb_outline_black_48dp),
    NoteToggle(R.drawable.ic_create_black_48dp),
    Spacer(R.drawable.ic_accessibility_black_48dp),//placeholder
    Delete(R.drawable.ic_delete_black_48dp),
    Reset(R.drawable.ic_settings_backup_restore_black_48dp);

    private int resID;

    SudokuButtonType(@DrawableRes int res){
        this.resID = res;
    }

    public int getResID() {
        return resID;
    }

    public static List<SudokuButtonType> getSpecialButtons() {
        ArrayList<SudokuButtonType> result = new ArrayList<SudokuButtonType>();
        result.add(Undo);
        result.add(Do);
        result.add(Hint);
        //result.add(Spacer);
        result.add(Delete);
        result.add(NoteToggle);
        return result;
    }
    public static String getName(SudokuButtonType type) {
        switch (type) {
            case Do: return "Do";
            case Undo: return "Un";
            case Hint: return "Hnt";
            case NoteToggle: return "On";
            case Spacer: return "";
            case Delete: return "Del";
            default:return "NotSet";
        }
    }
}


