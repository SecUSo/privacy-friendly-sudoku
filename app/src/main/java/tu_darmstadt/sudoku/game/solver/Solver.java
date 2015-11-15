package tu_darmstadt.sudoku.game.solver;

import java.util.LinkedList;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.ICellAction;

/**
 * Created by Chris on 10.11.2015.
 */
public class Solver implements ISolver {

    private GameBoard gameBoard = null;

    public Solver(GameBoard gf) {
        try {
            if(gf == null) {
                throw new IllegalArgumentException("GameBoard may not be null.");
            }

            gameBoard = gf.clone();
        } catch(CloneNotSupportedException e) {
            throw new IllegalArgumentException("This GameBoard is not cloneable.", e);
        }

        gameBoard.reset();
        if(!isSolvable(gameBoard)) {
            throw new IllegalArgumentException("This GameBoard is not solveable.");
        }

        setNotes(gameBoard);
    }

    public void setNotes(GameBoard gameBoard) {
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                for(int k = 0; k < gameBoard.getSize(); k++) {
                    gameBoard.getCell(i,j).setNote(k);
                }
            }
        }
    }

    public boolean isSolvable(GameBoard gameBoard) {
        for(int i = 0; i < gameBoard.getSize(); i++) {
            if(hasErrors(gameBoard.getRow(i))) return false;
            if(hasErrors(gameBoard.getColumn(i))) return false;
            if(hasErrors(gameBoard.getSection(i))) return false;
        }
        return true;
    }

    public boolean hasErrors(LinkedList<GameCell> list) {
        LinkedList<Integer> checked = new LinkedList<Integer>();
        for(GameCell c : list) {
            if(checked.contains(c.getValue())) {
                return true;
            }
            checked.add(c.getValue());
        }
        return false;
    }

    public boolean solve() {

        if(gameBoard.isSolved(new LinkedList<CellConflict>())) {
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

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public boolean showPossibles() {
        LinkedList<GameCell> list = new LinkedList<GameCell>();
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                GameCell gc = gameBoard.getCell(i,j);
                if(!gc.hasValue()) {
                    list.clear();
                    list.addAll(gameBoard.getRow(i));
                    list.addAll(gameBoard.getColumn(j));
                    list.addAll(gameBoard.getSection(i,j));
                    for(int k = 0; k < gameBoard.getSize(); k++) {
                        for(GameCell c : list) {
                            gc.deleteNote(c.getValue());
                        }
                    }
                }

            }
        }
        return false;
    }

    private boolean searchHiddenSingles() {
        boolean foundHiddenSingles = false;

        LinkedList<GameCell> list = new LinkedList<>();

        for(int i = 0; i < gameBoard.getSize()*3; i++) {

            int index = i % gameBoard.getSize();
            int listSelector = (int)Math.floor(i / gameBoard.getSize());

            if(listSelector == 0) list = gameBoard.getRow(index);
            if(listSelector == 1) list = gameBoard.getColumn(index);
            if(listSelector == 2) list = gameBoard.getSection(index);

            LinkedList<Integer[]> possibles = new LinkedList<>();
            LinkedList<Integer> notPossibles = new LinkedList<Integer>();

            for(GameCell c : list) { // check all gameCells in Row

                if(c.hasValue()) {
                    notPossibles.add(c.getValue());
                    continue;
                }

                for(int k = 0; k < gameBoard.getSize(); k++) {  // check all nodes
                    if(c.getNotes()[k]) {   // if k note active
                        for(int p = 0; p < possibles.size(); p++) { // check possible list
                            if(possibles.get(p)[2] == k+1) {    // is value in possible list?
                                // value already exists in possible list
                                // add to impossibles
                                if(!notPossibles.contains(k+1)) {
                                    notPossibles.add(k + 1);
                                }
                            }
                        }
                        if(!notPossibles.contains(k+1)){    // if k node is possible
                            possibles.add(new Integer[] {c.getRow(), c.getCol(), k+1});
                        }
                    }
                }
            }
            // we checked a section/row/column
            // now set the remaining possibles that are not impossible
            boolean possible = true;
            for(int p = 0; p < possibles.size(); p++) {
                possible = true;
                for(int np = 0; np < notPossibles.size(); np++) {
                    if(possibles.get(p)[2] == notPossibles.get(np)) {
                        // found that current possible is impossible
                        possible = false;
                    }
                }
                // if this is still possible then SET IT :D YAY
                if(possible) {
                    gameBoard.getCell(possibles.get(p)[0],possibles.get(p)[1]).setValue(possibles.get(p)[2]);
                    foundHiddenSingles = true;
                }
            }
        }

        return foundHiddenSingles;
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
        return gameBoard.actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                int value = -1;
                if(!gc.hasValue() && gc.getNoteCount() == 1) {
                    for(int i = 0; i < gameBoard.getSize(); i++) {
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


}
