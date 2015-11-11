package tu_darmstadt.sudoku.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by Timm Lippert on 11.11.2015.
 */
public class SudokuFieldLayout extends RelativeLayout {


    private GameController gamecont;

    public SudokuCellView [][] gamecells;



    public SudokuFieldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGame(GameController gc) {
        if (gc == null) throw new IllegalArgumentException("GameController may not be null.");
        gamecont = gc;
        gamecells = new SudokuCellView[gc.getSize()][gc.getSize()];
        int width = (Math.min(getWidth(),getHeight()))/gc.getSize();

        for (int i = 0; i < gc.getSize(); i++) {
            for (int j = 0; j < gc.getSize(); j++){

                gamecells[i][j] = new SudokuCellView(getContext(), null);
                //gamecells[i][j].setValues(i,j,width,field);
            }
        }


    }

}
