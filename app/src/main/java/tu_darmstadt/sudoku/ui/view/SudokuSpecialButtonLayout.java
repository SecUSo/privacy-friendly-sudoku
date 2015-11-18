package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameBoard;

/**
 * Created by TMZ_LToP on 17.11.2015.
 */
public class SudokuSpecialButtonLayout extends LinearLayout {

    SudokuButton [] fixedButtons;
    public int fixedButtonsCount = SudokuButtonType.getSpecialButtons().size();
    GameController gameController;
    SudokuKeyboardLayout keyboard;


    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;

                int row = gameController.getSelectedRow();
                int col = gameController.getSelectedCol();

                switch(btn.getType()) {
                    case Delete:
                        gameController.deleteSelectedValue();
                        break;
                    case NoteToggle:
                        btn.setText(keyboard.notesEnabled ? "ON" : "OFF");
                        keyboard.toggleNotesEnabled();
                        break;
                    case Do:
                        // TODO: not implemented
                        break;
                    case Undo:
                        // TODO: not implemented
                        break;
                    case Hint:
                        if(gameController.isValidCellSelected()) {
                            LinkedList<GameBoard> solved = gameController.solve();
                            if(solved.size() >= 1) {
                                GameBoard solvedBoard = solved.get(0);
                                // test every placed value so far

                                // and reveal the selected value.
                                gameController.selectValue(solvedBoard.getCell(row, col).getValue());
                            }
                        }
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
