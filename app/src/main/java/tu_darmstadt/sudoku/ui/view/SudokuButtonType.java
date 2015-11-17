package tu_darmstadt.sudoku.ui.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 15.11.2015.
 */
public enum SudokuButtonType {
    Unspecified,
    Value,
    Do,
    Undo,
    Hint,
    NoteToggle,
    NumberOrCellFirst,
    Delete,
    Reset;

    public static List<SudokuButtonType> getSpecialButtons() {
        ArrayList<SudokuButtonType> result = new ArrayList<SudokuButtonType>();
        result.add(Do);
        result.add(Undo);
        result.add(Hint);
        result.add(NoteToggle);
        result.add(NumberOrCellFirst);
        result.add(Delete);
        return result;
    }
    public static String getName(SudokuButtonType type) {
        switch (type) {
            case Do: return "Do";
            case Undo: return "Un";
            case Hint: return "Hnt";
            case NoteToggle: return "On";
            case NumberOrCellFirst: return "Cel";
            case Delete: return "Del";
            default:return "NotSet";
        }
    }
}


