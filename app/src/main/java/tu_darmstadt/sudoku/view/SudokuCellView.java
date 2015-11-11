package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Created by TMZ_LToP on 10.11.2015.
 */
public class SudokuCellView extends View {

    public SudokuCellView(Context context) {
        super(context);
    }

    public SudokuCellView(Context context, AttributeSet attrs){
        super(context,attrs);

    }

    int pubx =0 ;
    int puby =0 ;

    public void setPos(int x,int y) {
        //dpi = (width * 160)/density getRecourses().getDisplayMetrics().density

        pubx = x;
        puby = y;

    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        canvas.drawRect(pubx,puby,pubx+100,puby+100,new Paint());


    }
}
