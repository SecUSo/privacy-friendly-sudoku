package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameCell;

/**
 * Created by Timm Lippert on 11.11.2015.
 */
public class SudokuFieldLayout extends RelativeLayout {

    private GameController gameController;
    private int sectionHeight;
    private int sectionWidth;
    private int gameCellWidth;
    private int gameCellHeight;
    private SharedPreferences settings;

    public SudokuCellView [][] gamecells;
    AttributeSet attrs;

    public SudokuFieldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs=attrs;
        setBackgroundColor(Color.argb(255, 200, 200, 200));
    }

    public void setSettings(SharedPreferences sharedPref) {
        settings = sharedPref;
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

                    gameController.selectCell(row, col);
                    row = gameController.getSelectedRow();
                    col = gameController.getSelectedCol();

                    // Reset everything
                    for(int i = 0; i < gameController.getSize(); i++) {
                        for(int j = 0; j < gameController.getSize(); j++) {
                            gamecells[i][j].setHighlightType(CellHighlightTypes.Default);
                        }
                    }

                    if(row == -1 || col == -1) {
                        // we clicked on the same cell 2 times.
                        // means it got deselected and we dont highlight any cells.
                        return false;
                    }
                    // Set connected Fields
                    if(gameController.isValidCellSelected()) {
                        //String syncConnPref = sharedPref.getString(SettingsActivity., "");
                        boolean highlightConnectedRow = settings.getBoolean("pref_highlight_rows", true);
                        boolean highlightConnectedColumn = settings.getBoolean("pref_highlight_cols", true);
                        boolean highlightConnectedSection = settings.getBoolean("pref_highlight_secs", true);

                        for (GameCell c : gameController.getConnectedCells(row, col, highlightConnectedRow, highlightConnectedColumn, highlightConnectedSection)) {
                            gamecells[c.getRow()][c.getCol()].setHighlightType(CellHighlightTypes.Connected);
                        }
                        // Select touched Cell
                        scv.setHighlightType(CellHighlightTypes.Selected);
                    } else {
                        scv.setHighlightType(CellHighlightTypes.Value_Highlighted_Selected);
                    }
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

        int horizontalSections = gameController.getSize() / sectionWidth;
        for(int i = 0; i <= horizontalSections; i++) {
            for(int j = -2; j < 2; j++) {
                canvas.drawLine((i * getWidth() / horizontalSections) + j, 0, (i * getWidth() / horizontalSections) + j, getHeight(), p);
            }
        }
        int verticalSections = (gameController.getSize()/sectionHeight);
        for(int i = 0; i <= verticalSections; i++) {
            for(int j = -2; j < 2; j++) {
                canvas.drawLine(0, (i * getHeight() / verticalSections) + j, getHeight(), (i * getHeight() / verticalSections) + j, p);
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
