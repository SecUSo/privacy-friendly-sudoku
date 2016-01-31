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


