package tu_darmstadt.sudoku.game.solver;

import tu_darmstadt.sudoku.game.GameBoard;

/**
 * Created by Chris on 11.11.2015.
 */
public interface ISolver {

    public boolean solve(GameBoard gameBoard);

    public boolean calculateNextPossibleStep();

    public GameBoard getGameBoard();

}
