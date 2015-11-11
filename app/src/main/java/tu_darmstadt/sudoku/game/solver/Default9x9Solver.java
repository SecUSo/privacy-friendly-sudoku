package tu_darmstadt.sudoku.game.solver;

import java.util.LinkedList;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameField;
import tu_darmstadt.sudoku.game.ICellAction;

/**
 * Created by Chris on 10.11.2015.
 */
public class Default9x9Solver implements ISolver {

    private GameField gameField = null;

    Default9x9Solver(GameField gameField) {
        try {
            if(gameField == null) {
                throw new IllegalArgumentException("GameField may not be null.");
            }

            this.gameField = gameField.clone();
        } catch(CloneNotSupportedException e) {
            throw new IllegalArgumentException("This GameField is not cloneable.", e);
        }
    }

    public boolean solve() {

        if(gameField.isSolved(new LinkedList<CellConflict>())) {
            return true;
        }

        checkSolvedCells();

        if(showPossibles()) return solve();

        if(searchHiddenSingles()) return solve();

        if(searchNakedPairsTriples()) return solve();

        if(searchHiddenPairsTriples()) return solve();

        if(searchNakedQuads()) return solve();

        if(searchPointingPairs()) return solve();

        if(searchBoxLineReduction()) return solve();

        return false;
    }

    @Override
    public boolean calculateNextPossibleStep() {
        return false;
    }

    public GameField getGameField() {
        return gameField;
    }

    public boolean showPossibles() {
        LinkedList<GameCell> list = new LinkedList<GameCell>();
        for(int i = 0; i < gameField.getSize(); i++) {
            for(int j = 0; j < gameField.getSize(); j++) {
                GameCell gc = gameField.getCell(i,j);
                if(!gc.hasValue()) {
                    list.clear();
                    list.addAll(gameField.getRow(i));
                    list.addAll(gameField.getColumn(j));
                    list.addAll(gameField.getSection(i,j));
                    for(int k = 0; k < gameField.getSize(); k++) {
                        for(GameCell c : list) {
                            gc.deleteNote(c.getValue());
                        }
                    }
                }

            }
        }
        return false;
    }

    public boolean searchNakedPairsTriples() {
        return false;
    }
    public boolean searchHiddenPairsTriples() {
        return false;
    }
    public boolean searchNakedQuads() {
        return false;
    }
    public boolean searchPointingPairs() {
        return false;
    }
    public boolean searchBoxLineReduction() {
        return false;
    }

    private boolean checkSolvedCells() {
        return gameField.actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                boolean oneNote = false;
                int value = -1;
                if(gc.getNoteCount() == 1) {
                    for(int i = 0; i < gameField.getSize(); i++) {
                        if(gc.getNotes()[i]) {
                            value = i;
                            break;
                        }
                    }
                    gc.setValue(value);
                    existing = true;
                }
                return existing;
            }}, false);
    }

    private boolean searchHiddenSingles() {
        return false;
    }


}
