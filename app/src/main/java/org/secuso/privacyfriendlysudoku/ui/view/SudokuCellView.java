/*
 * qqwing - Sudoku solver and generator
 * Copyright (C) 2014 Stephen Ostermiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.game.GameCell;

/**
 * Created by TMZ_LToP on 10.11.2015.
 */
public class SudokuCellView extends View {

    GameCell mGameCell;
    int mWidth;
    int mHeight;
    int mSectionHeight;
    int mSectionWidth;
    int mRow;
    int mCol;
    int size;
    boolean selected;
    CellHighlightTypes highlightType = CellHighlightTypes.Default;
    Symbol symbolsToUse = Symbol.Default;
    RelativeLayout.LayoutParams params;

    int backgroundColor;
    int backgroundErrorColor;
    int backgroundSelectedColor;
    int backgroundConnectedOuterColor;
    int backgroundConnectedInnerColor;
    int backgroundValueHighlightedColor;
    int backgroundValueHighlightedSelectedColor;
    int textColor;

    public SudokuCellView(Context context, AttributeSet attrs){
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SudokuCellView);
        backgroundColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundColor, Color.argb(255, 200, 200, 200));
        backgroundErrorColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundErrorColor, Color.argb(255, 200, 200, 200));
        backgroundSelectedColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundSelectedColor, Color.argb(255, 200, 200, 200));
        backgroundConnectedOuterColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundConnectedOuterColor, Color.argb(255, 200, 200, 200));
        backgroundConnectedInnerColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundConnectedInnerColor, Color.argb(255, 200, 200, 200));
        backgroundValueHighlightedColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundValueHighlightedColor, Color.argb(255, 200, 200, 200));
        backgroundValueHighlightedSelectedColor = a.getColor(R.styleable.SudokuCellView_sudokuCellBackgroundValueHighlightedSelectedColor, Color.argb(255, 200, 200, 200));
        textColor = a.getColor(R.styleable.SudokuCellView_sudokuCellTextColor, Color.argb(255, 200, 200, 200));
        a.recycle();
    }



    public void setSelected(boolean b) {
        this.selected = b;
    }

    public void setValues (int width, int height, int sectionHeight, int sectionWidth, GameCell gameCell,int size) {
        mSectionHeight = sectionHeight;
        mSectionWidth = sectionWidth;
        mGameCell = gameCell;
        mWidth = width;
        mHeight = height;
        mRow = gameCell.getRow();
        mCol = gameCell.getCol();
        this.size = size;

        initLayoutParams();
    }

    private void initLayoutParams() {
        if(this.params == null) {
            params = new RelativeLayout.LayoutParams(mWidth, mHeight);
        }

        // Set Layout
        params.width = mWidth;
        params.height = mHeight;
        params.topMargin = mRow*mHeight;
        params.leftMargin = mCol*mWidth;
        this.setLayoutParams(params);
    }

    public void setSymbols(Symbol s) {
        symbolsToUse = s;
    }

    public void setHighlightType(CellHighlightTypes highlightType) {
        this.highlightType = highlightType;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(mGameCell == null) return false;

        if(motionEvent.getAction() == motionEvent.ACTION_DOWN) {
            highlightType = CellHighlightTypes.Selected;
        }

        return true;
    }*/

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw single Field
        drawInfo(canvas);
    }

    private void drawInfo(Canvas canvas) {
        Paint p = new Paint();
        switch(highlightType) {
            case Default:
                p.setColor(backgroundColor);
                break;
            case Error:
                p.setColor(backgroundErrorColor);
                break;
            case Selected:
                p.setColor(backgroundSelectedColor);
                break;
            case Connected:
                p.setColor(backgroundConnectedOuterColor);
                drawBackground(canvas, 3, 3, mWidth - 3, mHeight - 3, p);
                p.setColor(backgroundConnectedInnerColor);
                p.setAlpha(100);
                break;
            case Value_Highlighted:
                p.setColor(backgroundValueHighlightedColor);
                break;
            case Value_Highlighted_Selected:
                p.setColor(backgroundValueHighlightedSelectedColor);
                break;
            default:
                p.setColor(backgroundColor);
        }


        drawBackground(canvas, 3, 3, mWidth - 3, mHeight - 3, p);

        // if there is no mGameCell .. we can not retrieve the information to draw
        if(mGameCell != null) {
            drawValue(canvas);
        }
    }

    public void drawBackground(Canvas canvas, int left, int top, int right, int bottom, Paint p) {
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRect(rect, p);
    }

    public void drawValue(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(textColor);
        int root = (int) Math.sqrt(size);
        int j= root+1;
        int k = root;
        if(mGameCell.getValue() == 0) {
            for (int i = 0; i < mGameCell.getNotes().length; i++) {
                if (mGameCell.getNotes()[i]) {
                    p.setTypeface(Typeface.SANS_SERIF);
                    p.setTextSize(mWidth / 4);
                    p.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(Symbol.getSymbol(symbolsToUse, i),(mWidth*1/(size+root))*k,(mWidth*1/(size+root+1))*j,p);
                    /*canvas.drawText(String.valueOf(1), (mWidth * 1 / 12)*3, (mWidth* 1 / 12)*3, p);
                    canvas.drawText(String.valueOf(2),(mWidth*1/12)*7, (mWidth* 1 / 12)*7,p );
                    canvas.drawText(String.valueOf(3),(mWidth*1/12)*11, (mWidth* 1 / 12)*11,p );*/
                }
                k+=root+1;
                if (k > (size+root)) {
                    k = root;
                    j +=root+1;
                }
            }
            return;
        }


        if (mGameCell.isFixed()) {
            p.setTypeface(Typeface.DEFAULT_BOLD);
        }
        p.setAntiAlias(true);
        p.setTextSize(Math.min(mHeight * 3 / 4, mHeight * 3 / 4));
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(Symbol.getSymbol(symbolsToUse, mGameCell.getValue()-1), mHeight / 2, mHeight / 2 + mHeight / 4, p);
    }

    public int getRow() {
        return mRow;
    }
    public int getCol() {
        return mCol;
    }

    /*@Override
    public Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();

        return state;
    }*/
}
