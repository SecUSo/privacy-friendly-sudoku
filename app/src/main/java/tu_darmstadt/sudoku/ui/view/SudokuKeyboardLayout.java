package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.controller.Symbol;

/**
 * Created by TMZ_LToP on 12.11.2015.
 */


public class SudokuKeyboardLayout extends GridLayout {

    AttributeSet attrs;
    SudokuButton [] buttons;
    GameController gameController;
    boolean notesEnabled=false;
    Symbol symbolsToUse = Symbol.Default;
    float normalTextSize = 0;

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;
                if(notesEnabled) {
                    gameController.toggleSelectedNote(btn.getValue());
                } else {
                    gameController.selectValue(btn.getValue());
                }
                gameController.saveGame(getContext());
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
                // TODO settings: get SymbolEnum from settings
                buttons[number].setText(Symbol.getSymbol(symbolsToUse, number));
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

    public void toggleNotesEnabled() {
        notesEnabled = !notesEnabled;
        if (normalTextSize == 0) {normalTextSize = buttons[0].getTextSize();}
        if(notesEnabled) {
            setTextSize(normalTextSize*0.6f);
        }else {
            setTextSize(normalTextSize);
        }
    }

    private void setTextSize(float size){
        for (SudokuButton b : buttons){
            //b.setTextSize(size);
            b.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        }
    }



    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
