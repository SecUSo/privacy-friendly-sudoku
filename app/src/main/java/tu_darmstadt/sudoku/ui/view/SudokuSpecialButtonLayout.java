package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by TMZ_LToP on 17.11.2015.
 */
public class SudokuSpecialButtonLayout extends LinearLayout {

    SudokuButton [] fixedButtons;
    public int fixedButtonsCount = SudokuButtonType.getSpecialButtons().size();
    GameController gameController;
    boolean notesEnabled=false;
    SudokuKeyboardLayout keyboard;


    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;

                switch(btn.getType()) {
                    case Delete:
                        gameController.deleteSelectedValue();
                        break;
                    case NoteToggle:
                        notesEnabled = !notesEnabled;
                        btn.setText(notesEnabled ? "ON" : "OFF");
                        keyboard.setNotesEnabled(notesEnabled);
                        break;
                    case Do:
                        // TODO: not implemented
                        break;
                    case Undo:
                        // TODO: not implemented
                        break;
                    case Hint:
                        // TODO: not implemented
                        break;
                    case NumberOrCellFirst:
                        // TODO: not implemented
                        break;
                    default:
                        break;
                }
            }
        }
    };


    public SudokuSpecialButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWeightSum(fixedButtonsCount);
    }


    public void setButtons(int width, GameController gc,SudokuKeyboardLayout key) {
        keyboard=key;
        gameController = gc;
        fixedButtons = new SudokuButton[fixedButtonsCount];
        LayoutParams p;
        int i = 0;
        ArrayList<SudokuButtonType> type = (ArrayList<SudokuButtonType>) SudokuButtonType.getSpecialButtons();
        for (SudokuButtonType t : SudokuButtonType.getSpecialButtons()){
            fixedButtons[i] = new SudokuButton(getContext(),null);
            p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
            p.setMargins(0,0,0,0);
            int width2 =width/(fixedButtonsCount);
            p.width= width2-15;
            fixedButtons[i].setLayoutParams(p);
            fixedButtons[i].setGravity(Gravity.CENTER);
            fixedButtons[i].setType(t);
            fixedButtons[i].setText(SudokuButtonType.getName(t));
            fixedButtons[i].setOnClickListener(listener);
            addView(fixedButtons[i]);
            i++;


        }

    }
}
