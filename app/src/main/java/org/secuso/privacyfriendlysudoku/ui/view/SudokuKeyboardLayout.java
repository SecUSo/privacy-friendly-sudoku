/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.secuso.privacyfriendlysudoku.R;
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
    float normalTextSize = 20; // in sp
    LinearLayout [] layouts = new LinearLayout[2];
    float buttonMargin;

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuButton) {
                SudokuButton btn = (SudokuButton)v;

                gameController.selectValue(btn.getValue());
            }
        }
    };


    public SudokuKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SudokuKeyboardLayout);
        buttonMargin = a.getDimension(R.styleable.SudokuKeyboardLayout_sudokuKeyboardMargin, 5f);
        normalTextSize = a.getDimension(R.styleable.SudokuKeyboardLayout_sudokuKeyboardTextSize, 20f);
        a.recycle();
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

        normalTextSize = (int) getResources().getDimension(R.dimen.text_size) / getResources().getDisplayMetrics().scaledDensity;

        buttons = new SudokuButton[numberOfButtons];

        //set layout parameters and init Layouts
        for (int i = 0; i < 2; i++) {
            if(orientation == LinearLayout.HORIZONTAL) {
                p = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
            } else {
                p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1);
            }
            //if (i == 0) p.bottomMargin=10; else p.topMargin=10;
            p.setMargins(0, (int) buttonMargin, 0, (int) buttonMargin);
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
                p.setMargins((int) buttonMargin, (int) buttonMargin, (int) buttonMargin, (int) buttonMargin);
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
                buttons[buttonIndex].setType(SudokuButtonType.Value);
                buttons[buttonIndex].setTextColor(getResources().getColor(R.color.white));
                buttons[buttonIndex].setBackgroundResource(R.drawable.mnenomic_numpad_button);
                buttons[buttonIndex].setPadding(0, 0, 0, 0);
                buttons[buttonIndex].setGravity(Gravity.CENTER);
                buttons[buttonIndex].setMinHeight((int) (normalTextSize * 2));
                buttons[buttonIndex].setText(Symbol.getSymbol(symbolsToUse, buttonIndex));
                buttons[buttonIndex].setTextSize(TypedValue.COMPLEX_UNIT_SP, normalTextSize);
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

        if(gameController.getNoteStatus()) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP,normalTextSize*0.55f);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_SP,normalTextSize);
        }
    }

    private void setTextSize(int unit,float size){
        for (SudokuButton b : buttons){
            //b.setTextSize(size);
            b.setTextSize(unit,size);
        }
    }



    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onHighlightChanged() {
        for(SudokuButton i_btn : buttons) {
            int backgroundResId = R.drawable.mnenomic_numpad_button;

            boolean numCompleted = (gameController.getValueCount(i_btn.getValue()) == gameController.getSize());
            boolean numSelected = (gameController.getSelectedValue() == i_btn.getValue());

            if(numCompleted) {
                // Fill color           : darkyellow
                // Border (if selected) : yellow
                if(numSelected) {
                    backgroundResId = R.drawable.numpad_selected_complete;
                } else {
                    backgroundResId = R.drawable.numpad_unselected_complete;
                }
            } else {
                // Fill color           : lightblue
                // Border (if selected) : colorPrimaryDark
                if(numSelected) {
                    backgroundResId = R.drawable.numpad_highlighted;
                } // The else scenario is taken care of by the default initialized value
            }

            i_btn.setBackgroundResource(backgroundResId);

        }
    }


}
