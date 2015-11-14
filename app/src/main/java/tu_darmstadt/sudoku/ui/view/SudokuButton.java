package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by TMZ_LToP on 12.11.2015.
 */



public class SudokuButton extends Button {

    private int i = 100;

    public SudokuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVal(int i){
        this.i = i;
    }
    public int getValue () {
        return i;
    }
}


