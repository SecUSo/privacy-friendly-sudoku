package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.jar.Attributes;

import tu_darmstadt.sudoku.game.GameCell;

/**
 * Created by TMZ_LToP on 10.11.2015.
 */
public class SudokuCellView extends View {

    GameCell mGameCell;
    int mWidth;
    int mSectionHeight;
    int mSectionWidth;
    boolean touched;


    public SudokuCellView(Context context) {
        super(context);
    }

    public SudokuCellView(Context context, AttributeSet attrs){
        super(context, attrs);

    }

    public void setValues (int width, int sectionHeight, int sectionWidth, GameCell gameCell) {
        mSectionHeight = sectionHeight;
        mSectionWidth = sectionWidth;
        mGameCell = gameCell;
        mWidth=width;
        setWillNotDraw(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(mGameCell == null) return false;

        //this.setBackgroundColor(Color.CYAN);
        touched = true;
        Toast t = Toast.makeText(getContext(), mGameCell.toString(), Toast.LENGTH_SHORT);
        t.show();

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mGameCell == null) {
            return;
        }
        int row = mGameCell.getRow();
        int col = mGameCell.getCol();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidth, mWidth);
        params.topMargin = row*mWidth;
        params.leftMargin = col*mWidth;
        this.setLayoutParams(params);

        Paint p = new Paint();
        p.setColor(Color.GRAY);
        RectF rect = new RectF(0, 0, mWidth, mWidth);
        canvas.drawRect(rect, p);
        p.setColor(Color.WHITE);
        RectF rectInner = new RectF(2, 2, mWidth-2, mWidth-2);
        canvas.drawRect(rectInner, p);

        if(touched) {
            p.setColor(Color.GREEN);
            RectF rectTouched = new RectF(2, 2, mWidth-2, mWidth-2);
            canvas.drawRect(rectTouched, p);
        }

        drawValue(canvas);
    }

    public void drawValue(Canvas canvas) {
        if(mGameCell.getValue() == 0) return;

        Paint p = new Paint();
        if (mGameCell.isFixed()) {
            p.setTypeface(Typeface.DEFAULT_BOLD);
        }
        p.setAntiAlias(true);
        p.setTextSize(Math.min(mWidth * 3 / 4, mWidth * 3 / 4));
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(mGameCell.getValue()), mWidth/2, mWidth/2 + mWidth/4, p);
    }

    public int getColumn() {
        return mGameCell.getCol();
    }

    public int getRow() {
        return mGameCell.getRow();
    }


}
