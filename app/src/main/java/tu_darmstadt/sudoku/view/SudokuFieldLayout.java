package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameSettings;
import tu_darmstadt.sudoku.view.highlighting.CellHighlightTypes;

/**
 * Created by Timm Lippert on 11.11.2015.
 */
public class SudokuFieldLayout extends RelativeLayout {

    private GameController gameController;
    private int sectionHeight;
    private int sectionWidth;
    private int gameCellWidth;
    private int gameCellHeight;

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

        OnTouchListener listener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v instanceof SudokuCellView) {

                    SudokuCellView scv = ((SudokuCellView) v);

                    int row = scv.getRow();
                    int col = scv.getCol();

                    // Reset everything
                    for(int i = 0; i < gameController.getSize(); i++) {
                        for(int j = 0; j < gameController.getSize(); j++) {
                            gamecells[i][j].setHighlightType(CellHighlightTypes.Default);
                        }
                    }
                    // Set connected Fields
                    for(GameCell c : gameController.getConnectedCells(row,col, GameSettings.getHighlightConnectedRow(), GameSettings.getHighlightConnectedColumn(), GameSettings.getHighlightConnectedSection())) {
                        gamecells[c.getRow()][c.getCol()].setHighlightType(CellHighlightTypes.Connected);
                    }
                    // Select touched Cell
                    gameController.selectCell(row, col);
                    scv.setHighlightType(CellHighlightTypes.Selected);


                }
                return false;
            }
        };

        sectionHeight = gameController.getSectionHeight();
        sectionWidth = gameController.getSectionWidth();

        for (int i = 0; i < gameController.getSize(); i++) {
            for (int j = 0; j < gameController.getSize(); j++) {
                gamecells[i][j] = new SudokuCellView(getContext(), attrs);
                gamecells[i][j].setOnTouchListener(listener);
                addView(gamecells[i][j]);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(gameController == null) return;

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);

        // TODO: Draw Borders
        for(int i = 0; i <= (gameController.getSize()/sectionWidth); i++) {
            for(int j = -2; j < 2; j++) {
                canvas.drawLine((i * getWidth() / sectionWidth) + j, 0, (i * getWidth() / sectionWidth) + j, getHeight(), p);
            }
        }
        for(int i = 0; i <= (gameController.getSize()/sectionHeight); i++) {
            for(int j = -2; j < 2; j++) {
                canvas.drawLine(0, (i * getHeight() / sectionHeight) + j, getHeight(), (i * getHeight() / sectionHeight) + j, p);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);

        if(changed && gameController != null) {
            gameCellWidth = (Math.min(r-l, b-t)) / gameController.getSize();
            gameCellHeight = (Math.min(r-l, b-t)) / gameController.getSize();

            for (int i = 0; i < gameController.getSize(); i++) {
                for (int j = 0; j < gameController.getSize(); j++) {
                    gamecells[i][j].setValues(gameCellWidth, gameCellHeight, sectionHeight, sectionWidth, gameController.getGameCell(i, j));
                }
            }
        }
    }

}
