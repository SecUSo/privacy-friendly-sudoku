package tu_darmstadt.sudoku.game.listener;

import tu_darmstadt.sudoku.game.GameCell;

/**
 * Created by Chris on 19.11.2015.
 */
public interface IModelChangedListener {
    public void onModelChange(GameCell c);
}
