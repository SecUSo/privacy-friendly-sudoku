package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameType;

/**
 * Created by TMZ_LToP on 12.11.2015.
 */


public class SudokuKeyboardLayout extends GridLayout {

    AttributeSet attrs;
    SudokuButton [] buttons;
    SudokuButton [] fixedButtons;
    public int fixedButtonsCount = 6;
    GameController gameController;
    boolean notesEnabled=false;
    SudokuButtonType [] fixedTypes = {SudokuButtonType.Do,SudokuButtonType.Undo,SudokuButtonType.NoteToggle,SudokuButtonType.Delete,SudokuButtonType.NumberOrCellFirst,SudokuButtonType.Hint};
    String [] s = {"Do","Un","fal","Del","Sh**","Hi"};

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;

                switch(btn.getType()) {
                    case Value:
                        if(notesEnabled) {
                            gameController.toggleSelectedNote(btn.getValue());
                        } else {
                            gameController.setSelectedValue(btn.getValue());
                        }
                        break;
                    case Delete:
                        gameController.deleteSelectedValue();
                        break;
                    case NoteToggle:
                        notesEnabled = !notesEnabled;
                        btn.setText(String.valueOf(notesEnabled));
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




    public SudokuKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;

    }


    public void setKeyBoard(int size,int width) {
        LayoutParams p ;
        buttons = new SudokuButton[size];
        fixedButtons = new SudokuButton[fixedButtonsCount];
        int row = 0;
        int number = 0;
        int torun = ((size)%2==0) ? (size)/2 :(size+1)/2 ;

        for (int k = 0; k<2;k++){
            for (int i = 0; i< torun; i++){
                buttons[i] = new SudokuButton(getContext(),null);
                p = new LayoutParams(GridLayout.spec(k,1),GridLayout.spec(i, 1));
                p.setMargins(0,0,0,0);
                int width2 =width/(torun);
                p.width= width2-15;
                buttons[i].setLayoutParams(p);
                buttons[i].setGravity(Gravity.CENTER);
                buttons[i].setType(SudokuButtonType.Value);
                buttons[i].setText(String.valueOf(number + 1));
                buttons[i].setValue(number + 1);
                buttons[i].setOnClickListener(listener);

                number++;
                addView(buttons[i]);
            }
        }
        for (int i = 0; i < fixedButtonsCount; i++){
            fixedButtons[i] = new SudokuButton(getContext(),null);
            p = new LayoutParams(GridLayout.spec(2,1),GridLayout.spec(i, 1));
            p.setMargins(0,0,0,0);
            int width2 =width/(fixedButtonsCount);
            p.width= width2-15;
            fixedButtons[i].setLayoutParams(p);
            fixedButtons[i].setGravity(Gravity.CENTER);
            fixedButtons[i].setType(fixedTypes[i]);
            fixedButtons[i].setText(s[i]);
            fixedButtons[i].setOnClickListener(listener);
            addView(fixedButtons[i]);


        }


    }

    public void setGameController(GameController gc){
        gameController=gc;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
