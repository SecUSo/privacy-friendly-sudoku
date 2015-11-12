package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by Timm Lippert on 11.11.2015.
 */
public class SudokuFieldLayout extends RelativeLayout {

    private GameController gameController;

    public SudokuCellView [][] gamecells;
    AttributeSet attrs;

    public SudokuFieldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs=attrs;
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
                    Toast t = Toast.makeText(getContext(), "(" + view.getRow() + " " + view.getColumn() + ")", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        };

        for (int i = 0; i < gameController.getSize(); i++) {
            for (int j = 0; j < gameController.getSize(); j++) {
                gamecells[i][j] = new SudokuCellView(getContext(), attrs);
                addView(gamecells[i][j]);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);

        if(changed && gameController != null) {
            int width = (Math.min(r-l, b-t)) / gameController.getSize();

            for (int i = 0; i < gameController.getSize(); i++) {
                for (int j = 0; j < gameController.getSize(); j++) {
                    gamecells[i][j].setValues(width, gameController.getSectionHeight(), gameController.getSectionWidth(), gameController.getGameCell(i, j));
                }
            }
        }
    }

}
