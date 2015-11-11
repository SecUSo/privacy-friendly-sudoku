package tu_darmstadt.sudoku.game.solver;

import tu_darmstadt.sudoku.game.GameField;

/**
 * Created by Chris on 11.11.2015.
 */
public interface ISolver {

    public boolean solve();

    public boolean calculateNextPossibleStep();

    public GameField getGameField();

}
