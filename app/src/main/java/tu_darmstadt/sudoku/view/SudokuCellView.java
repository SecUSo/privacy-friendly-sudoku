package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.jar.Attributes;

import tu_darmstadt.sudoku.game.GameCell;

/**
 * Created by TMZ_LToP on 10.11.2015.
 */
public class SudokuCellView extends View {

    GameCell mGameCell;
    int mWidth;


    public SudokuCellView(Context context) {
        super(context);
    }

    public SudokuCellView(Context context, AttributeSet attrs){
        super(context,attrs);

    }

    public void setValues (int width, GameCell gameCell) {
        mGameCell = gameCell;
        mWidth=width;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int row = mGameCell.getRow();
        int col = mGameCell.getCol();

        this.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, mWidth));

        canvas.drawRect(col*mWidth, row*mWidth, (col+1)*mWidth+1, (row+1)*mWidth+1,new Paint());
    }

    public int getColumn() {
        return mGameCell.getCol();
    }

    public int getRow() {
        return mGameCell.getRow();
    }


}
