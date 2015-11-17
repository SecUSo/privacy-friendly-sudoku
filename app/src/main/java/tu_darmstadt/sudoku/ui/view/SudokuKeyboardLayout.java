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
    GameController gameController;
    boolean notesEnabled=false;


    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;
                    if(notesEnabled) {
                        gameController.toggleSelectedNote(btn.getValue());
                    } else {
                        gameController.setSelectedValue(btn.getValue());
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
        int number = 0;
        int torun = ((size)%2==0) ? (size)/2 :(size+1)/2 ;
        int realSize = torun;

        for (int k = 0; k<2;k++){
            for (int i = 0; i< torun; i++){
                if (number == size) {
                    break;
                }
                buttons[number] = new SudokuButton(getContext(),null);
                p = new LayoutParams(GridLayout.spec(k,1),GridLayout.spec(i, 1));
                p.setMargins(0,0,0,0);
                int width2 =width/(realSize);
                p.width= width2-15;
                buttons[number].setLayoutParams(p);
                buttons[number].setGravity(Gravity.CENTER);
                buttons[number].setType(SudokuButtonType.Value);
                buttons[number].setText(String.valueOf(number + 1));
                buttons[number].setValue(number + 1);
                buttons[number].setOnClickListener(listener);
                addView(buttons[number]);
                number++;



            }
        }
    }

    public void setGameController(GameController gc){
        gameController=gc;
    }

    public void setNotesEnabled(boolean b) {
        notesEnabled = b;
    }

    public void setValueSize (float size){
        for (int i = 0; i< buttons.length;i++){
            buttons[i].setTextSize(size);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
