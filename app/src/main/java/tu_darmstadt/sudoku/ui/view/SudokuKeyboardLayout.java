package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;

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


    public void setKeyBoard(int size,int width, int height) {
        LayoutParams p ;
        buttons = new SudokuButton[size];
        int number = 0;
        int torun = ((size)%2==0) ? (size)/2 :(size+1)/2 ;
        int realSize = torun;

        int width2 =(width-(realSize*30))/(realSize);
        for (int k = 0; k<2;k++){
            for (int i = 0; i< torun; i++){
                if (number == size) {
                    break;
                }
                buttons[number] = new SudokuButton(getContext(),null);
                Spec rowSpec = spec(k,1);
                Spec colSpec = spec(i,1);

                p = (new LayoutParams(rowSpec,colSpec));



                //p = new LayoutParams(rowSpec,colSpec);
                p.setMargins((i == 0)?0:5,5,5,5);
                p.width= (width-(int)((getResources().getDimension(R.dimen.activity_horizontal_margin))*2))/realSize;
                p.width= p.width-10;
                p.setGravity(LayoutParams.WRAP_CONTENT);


                buttons[number].setLayoutParams(p);
                //buttons[number].setGravity(Gravity.CENTER);
                buttons[number].setType(SudokuButtonType.Value);
                buttons[number].setBackgroundResource(R.drawable.mnenomic_numpad_button);
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
