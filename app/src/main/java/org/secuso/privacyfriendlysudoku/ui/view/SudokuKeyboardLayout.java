package org.secuso.privacyfriendlysudoku.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.game.listener.IHighlightChangedListener;

/**
 * Created by TMZ_LToP on 12.11.2015.
 */


public class SudokuKeyboardLayout extends LinearLayout implements IHighlightChangedListener {

    AttributeSet attrs;
    SudokuButton [] buttons;
    GameController gameController;
    Symbol symbolsToUse = Symbol.Default;
    float normalTextSize = 0;
    LinearLayout [] layouts = new LinearLayout[2];

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;

                gameController.selectValue(btn.getValue());

                gameController.saveGame(getContext());
            }
        }
    };


    public SudokuKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
    }

    public void setSymbols(Symbol s) {
        symbolsToUse = s;
        for(SudokuButton b : buttons) {
            b.setText(Symbol.getSymbol(symbolsToUse, b.getValue()-1));
        }
    }

    public void setKeyBoard(int size,int width, int height, int orientation) {
        LayoutParams p;
        int number = 0;
        int numberOfButtonsPerRow = (size % 2 == 0) ? size/2 :(size+1)/2;
        int numberOfButtons = numberOfButtonsPerRow * 2;

        buttons = new SudokuButton[numberOfButtons];

        //set layout parameters and init Layouts
        for (int i = 0; i < 2; i++) {
            if(orientation == LinearLayout.HORIZONTAL) {
                p = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
            } else {
                p = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            }
            //if (i == 0) p.bottomMargin=10; else p.topMargin=10;
            p.setMargins(0,5,0,5);
            layouts[i] = new LinearLayout(getContext(),null);
            layouts[i].setLayoutParams(p);
            layouts[i].setWeightSum(numberOfButtonsPerRow);
            layouts[i].setOrientation(orientation);
            addView(layouts[i]);
        }

        //int width2 =(width-(realSize*30))/(realSize);



        for (int layoutNumber = 0; layoutNumber <= 1 ; layoutNumber++){
            for (int i = 0; i < numberOfButtonsPerRow; i++){
                int buttonIndex = i + layoutNumber * numberOfButtonsPerRow;
                buttons[buttonIndex] = new SudokuButton(getContext(),null);
                if(orientation == LinearLayout.HORIZONTAL) {
                    p = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
                } else {
                    p = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
                }
                p.setMargins(5,5,5,5);
                buttons[buttonIndex].setLayoutParams(p);
                /* removed GridLayout because of bad scaling will use now a Linearlayout
                Spec rowSpec = spec(k,1);
                Spec colSpec = spec(i,1);

                p = (new LayoutParams(rowSpec,colSpec));

                //p = new LayoutParams(rowSpec,colSpec);
                p.setMargins((i == 0) ? 0 : 5,5,5,5);
                p.width = (width - (int)((getResources().getDimension(R.dimen.activity_horizontal_margin))*2)) / realSize;
                p.width = p.width - 10;
                //p.setGravity(Gravity.FILL_VERTICAL);
                //p.setGravity(Gravity.FILL);
               // p.setGravity(LayoutParams.WRAP_CONTENT);
                */

          //      buttons[number].setLayoutParams(p);
                //buttons[number].setGravity(Gravity.CENTER);
                buttons[buttonIndex].setType(SudokuButtonType.Value);
                buttons[buttonIndex].setTextColor(getResources().getColor(R.color.white));
                buttons[buttonIndex].setBackgroundResource(R.drawable.mnenomic_numpad_button);
                buttons[buttonIndex].setText(Symbol.getSymbol(symbolsToUse, buttonIndex));
                buttons[buttonIndex].setValue(buttonIndex + 1);
                buttons[buttonIndex].setOnClickListener(listener);

                if (buttonIndex == size) {
                    buttons[buttonIndex].setVisibility(INVISIBLE);
                }

                layouts[layoutNumber].addView(buttons[buttonIndex]);
            }
        }
    }

    public void setButtonsEnabled(boolean enabled) {
        for(SudokuButton b : buttons) {
            b.setEnabled(enabled);
        }
    }

    public void setGameController(GameController gc){
        if(gc == null) {
            throw new IllegalArgumentException("GameController may not be null.");
        }

        gameController = gc;
        gameController.registerHighlightChangedListener(this);
    }

    public void updateNotesEnabled() {
        if (normalTextSize == 0) {
            normalTextSize = buttons[0].getTextSize();
        }

        if(gameController.getNoteStatus()) {
            setTextSize(normalTextSize*0.6f);
        } else {
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

    @Override
    public void onHighlightChanged() {
        for(SudokuButton i_btn : buttons) {
            i_btn.setBackgroundResource(R.drawable.mnenomic_numpad_button);

            // Highlight Yellow if we are done with that number
            if(gameController.getValueCount(i_btn.getValue()) == gameController.getSize()) {
                i_btn.setBackgroundResource(R.drawable.numpad_highlighted_three);
            }

            if(gameController.getSelectedValue() == i_btn.getValue()) {
                // highlight button to indicate that the value is selected
                i_btn.setBackgroundResource(R.drawable.numpad_highlighted);
            }
        }
    }

    public void fixHeight (){
        int i = getHeight();
        i = buttons[0].getHeight();
        i = buttons[5].getHeight();
    }
}
