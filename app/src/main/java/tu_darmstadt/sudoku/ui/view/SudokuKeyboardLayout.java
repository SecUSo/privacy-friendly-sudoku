package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tu_darmstadt.sudoku.controller.GameController;

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
            SudokuButton btn = (SudokuButton)v;
            int i = btn.getValue();
            if(notesEnabled) {
                //TODO: set notes funktion erst noch im GameController Schreiben
            }else {
                gameController.setSelectedValue(btn.getValue());
            }
        }
    };




    public SudokuKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;

    }


    public void setKeyBoard(int size,int width) {
        LayoutParams p ;
        int fixedButtons = 0;
        buttons = new SudokuButton[size+1];
        int row = 0;
        int number = 0;
        int torun = ((size+fixedButtons)%2==0) ? (size+fixedButtons)/2 :(size+fixedButtons+1)/2 ;

        for (int k = 0; k<2;k++){
            for (int i = 0; i< torun; i++){
                buttons[i] = new SudokuButton(getContext(),null);
                p = new LayoutParams(GridLayout.spec(k,1),GridLayout.spec(i, 1));
                p.setMargins(0,0,0,0);
                int width2 =width/(torun);
                p.width= width2-15;
                buttons[i].setLayoutParams(p);
                buttons[i].setGravity(Gravity.CENTER);
                if (number<size) {
                    buttons[i].setText(String.valueOf(number + 1));
                    buttons[i].setVal(number + 1);
                    buttons[i].setOnClickListener(listener);
                } else {
                    //TODO: Set Enum for fixed Buttons maybe also pictures
                }
                number++;
                addView(buttons[i]);
            }
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
