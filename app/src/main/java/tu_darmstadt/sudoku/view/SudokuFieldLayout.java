package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by Timm Lippert on 11.11.2015.
 */
public class SudokuFieldLayout extends RelativeLayout {

    private GameController gameController;
    private int sectionHeight;
    private int sectionWidth;
    private int gameCellWidth;

    public SudokuCellView [][] gamecells;
    AttributeSet attrs;

    public SudokuFieldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs=attrs;
        setBackgroundColor(Color.argb(255, 200, 200, 200));
    }

    public void setGame(GameController gc) {
        if (gc == null) throw new IllegalArgumentException("GameController may not be null.");
        gameController = gc;
        gamecells = new SudokuCellView[gc.getSize()][gc.getSize()];

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof SudokuCellView) {
                    SudokuCellView view = (SudokuCellView) v;
                }
            }
        };

        sectionHeight = gameController.getSectionHeight();
        sectionWidth = gameController.getSectionWidth();

        for (int i = 0; i < gameController.getSize(); i++) {
            for (int j = 0; j < gameController.getSize(); j++) {
                gamecells[i][j] = new SudokuCellView(getContext(), attrs);
                addView(gamecells[i][j]);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);

        // TODO: Draw Borders
        //canvas.drawLine(0, 0, 0, getHeight(), p);
        for(int i = 0; i <= (gameController.getSize()/sectionWidth); i++) {
            for(int j = -2; j < 2; j++) {
                canvas.drawLine((i * getWidth() / sectionWidth) + j, 0, (i * getWidth() / sectionWidth) + j, getHeight(), p);
            }
        }
        for(int i = 0; i <= (gameController.getSize()/sectionHeight); i++) {
            canvas.drawLine(0, i*getHeight() / sectionHeight, getHeight(), i*getHeight() / sectionHeight, p);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);

        if(changed && gameController != null) {
            gameCellWidth = (Math.min(r-l, b-t)) / gameController.getSize();

            for (int i = 0; i < gameController.getSize(); i++) {
                for (int j = 0; j < gameController.getSize(); j++) {
                    gamecells[i][j].setValues(gameCellWidth, sectionHeight, sectionWidth, gameController.getGameCell(i, j));
                }
            }
        }
    }

}
