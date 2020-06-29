package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by TMZ_LToP on 07.12.2015.
 */
public class CreateSudokuSpecialButton extends ImageButton {
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
