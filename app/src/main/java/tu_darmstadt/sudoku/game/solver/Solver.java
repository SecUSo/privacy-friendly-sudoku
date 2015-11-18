package tu_darmstadt.sudoku.game.solver;

import android.graphics.Point;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;
import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.ICellAction;

/**
 * Created by Chris on 10.11.2015.
 */
public class Solver implements ISolver {

    private GameBoard gameBoard = null;
    private LinkedList<GameBoard> solutions = new LinkedList<>();

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
                for(int k = 1; k <= gameBoard.getSize(); k++) {
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
            if(c.hasValue()) {
                checked.add(c.getValue());
            }
        }
        return false;
    }

    public LinkedList<GameBoard> getSolutions() {
        return solutions;
    }

    public boolean isDone(GameBoard gameBoard) {
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                if(!gameBoard.getCell(i,j).hasValue()) return false;
            }
        }
        return true;
    }

    public boolean solve(final GameBoard gameBoard) {

        checkSolvedCells(gameBoard);

        String string = gameBoard.toString();

        if(isDone(gameBoard)) {
            solutions.add(gameBoard);
            return true;
        }

        if(showPossibles(gameBoard))
            return solve(gameBoard);

        if(searchHiddenSingles(gameBoard))
            return solve(gameBoard);

        if(searchNakedPairsTriples(gameBoard))
            return solve(gameBoard);

        if(searchHiddenPairsTriples(gameBoard))
            return solve(gameBoard);

        if(searchNakedQuads(gameBoard))
            return solve(gameBoard);

        if(searchPointingPairs(gameBoard))
            return solve(gameBoard);

        if(searchBoxLineReduction(gameBoard))
            return solve(gameBoard);


        // if every defined strategy fails.. we have to guess
        // get the best candidate
        Point p = getBestCandidate(gameBoard);

        // then we test every possible value for that candidate, but we do it on a cloned gameBoard
        boolean result = false;
        for(int i = 0; i < gameBoard.getSize(); i++) {
            GameCell gc = gameBoard.getCell(p.x,p.y);
            try {
                if(gc.getNotes()[i]) {
                    GameBoard gameBoardCopy = gameBoard.clone();

                    GameCell copyGC = gameBoardCopy.getCell(p.x, p.y);

                    copyGC.setValue(i);

                    result = solve(gameBoardCopy);

                    //if (result) {
                        // stop after we found 1 solution
                        //return true;

                        // or keep going to find multiple solutions
                    //}

                } else {
                    continue;
                }
            } catch(CloneNotSupportedException e) {
                return false;
            }
        }
        return result;
    }

    @Override
    public boolean calculateNextPossibleStep() {

        return false;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    private boolean checkSolvedCells(final GameBoard gameBoard) {
        return gameBoard.actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                int value = -1;
                if(!gc.hasValue() && gc.getNoteCount() == 1) {
                    for(int i = 0; i < gameBoard.getSize(); i++) {
                        if(gc.getNotes()[i]) {
                            value = i+1;
                            break;
                        }
                    }
                    gc.setValue(value);
                    existing = true;
                }
                return existing;
            }}, false);
    }


    public boolean showPossibles(final GameBoard gameBoard) {
        boolean deletedSomething = false;
        LinkedList<GameCell> list = new LinkedList<GameCell>();
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                GameCell gc = gameBoard.getCell(i,j);
                if(!gc.hasValue()) {
                    list.clear();
                    list.addAll(gameBoard.getRow(i));
                    list.addAll(gameBoard.getColumn(j));
                    list.addAll(gameBoard.getSection(i,j));
                    for(GameCell c : list) {
                        for(int k = 0; k < gameBoard.getSize(); k++) {
                            if(gc.getNotes()[k] && c.hasValue() && !c.equals(gc) && k+1 == c.getValue()) {
                                gc.deleteNote(c.getValue());
                                deletedSomething = true;
                            }
                        }
                    }
                }
            }
        }
        return deletedSomething;
    }

    private boolean searchHiddenSingles(final GameBoard gameBoard) {
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


    public boolean searchNakedPairsTriples(final GameBoard gameBoard) {
        return false;
    }
    public boolean searchHiddenPairsTriples(final GameBoard gameBoard) {
        return false;
    }
    public boolean searchNakedQuads(final GameBoard gameBoard) {
        return false;
    }
    public boolean searchPointingPairs(final GameBoard gameBoard) {
        return false;
    }
    public boolean searchBoxLineReduction(final GameBoard gameBoard) {
        return false;
    }

    public Point getBestCandidate(GameBoard gameBoard) {
        Point bestCandidate = new Point();
        int minimumCount = gameBoard.getSize();
        int count = 0;
        Point candidate = new Point();
        for(int i = 0; i < gameBoard.getSize(); i++) {

            count = countUnsolved(gameBoard.getRow(i),candidate);
            if(count < minimumCount) {
                minimumCount = count;
                bestCandidate.set(candidate.x, candidate.y);
            }

            count = countUnsolved(gameBoard.getColumn(i),candidate);
            if(count < minimumCount) {
                minimumCount = count;
                bestCandidate.set(candidate.x, candidate.y);
            }

            count = countUnsolved(gameBoard.getSection(i),candidate);
            if(count < minimumCount) {
                minimumCount = count;
                bestCandidate.set(candidate.x, candidate.y);
            }

            if(minimumCount == 2) {
                return bestCandidate;
            }
        }
        return bestCandidate;
    }

    public int countUnsolved(final List<GameCell> list, Point p) {
        int count = 0;
        for(GameCell gc : list) {
            if(!gc.hasValue()) {
                count++;
                p.set(gc.getRow(), gc.getCol());
            }
        }
        return count;
    }





}
