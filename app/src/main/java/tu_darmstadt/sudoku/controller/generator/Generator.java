package tu_darmstadt.sudoku.controller.generator;

import android.graphics.Point;
import android.support.v7.util.SortedList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tu_darmstadt.sudoku.controller.solver.Solver;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.game.ICellAction;

/**
 * Created by Chris on 19.11.2015.
 */
public class Generator {

    private Random random;
    private int size;
    private int sectionHeight;
    private int sectionWidth;
    private GameBoard gameBoard;

    public Generator(GameType type, GameDifficulty difficulty) {
        this.size = type.getSize();
        this.sectionHeight = type.getSectionHeight();
        this.sectionWidth = type.getSectionWidth();
        this.gameBoard = new GameBoard(type);
        gameBoard.initCells(new int[size*size]);
        setNotes(gameBoard);

        // generate a random valid board.
        gameBoard = generate();

        // produce a level out of it.
        gameBoard = createLevel(difficulty);
    }

    public GameBoard createLevel(GameDifficulty difficulty) {
        return gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }


    public GameBoard generate() {
        random = new Random();

        GameBoard workingBoard = gameBoard;
        GameBoard board = gameBoard;

        /*GameCell chosen = gameBoard.getCell(random.nextInt(9), random.nextInt(9));
        while(chosen.getNoteCount() <= 2) {
            chosen = gameBoard.getCell(random.nextInt(9), random.nextInt(9));
        }*/

        while(workingBoard != null && !workingBoard.isFilled()) {

            // clone board
            try {
                workingBoard = board.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }

            // get all candidates
            List<GameCell> candidates = getListOfCandidates(workingBoard);
            // choose one of them
            GameCell chosen = candidates.get(random.nextInt(candidates.size()));
            // get all possible values
            List<Integer> possibleValues = getPossibleValues(chosen);
            // set a random value of that pool
            chosen.setValue(possibleValues.get(random.nextInt(possibleValues.size())));

            deleteConnectedValues(workingBoard, chosen);

            Solver solver = new Solver(workingBoard);
            if (solver.solve(solver.getGameBoard())) {
                List<GameBoard> solutions = solver.getSolutions();
                switch(solutions.size()) {
                    case 0:     // if we get no solution .. revert change
                        continue;
                    case 1:     // if we get 1 solution we are done and return the solution
                        return solutions.get(0);
                    default:    // if we get more than 1 solution .. keep setting numbers
                        board = workingBoard;
                        continue;
                }
            } else {

            }
        }




        return null;
    }

    private boolean deleteConnectedValues(final GameBoard gameBoard, final GameCell cell) {
        return gameBoard.actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                if (!gc.hasValue() && !gc.equals(cell)) {
                    if (gc.getRow() == cell.getRow()
                            || gc.getCol() == cell.getCol()
                            || (int) (Math.floor(gc.getRow() / sectionHeight) * sectionHeight + Math.floor(gc.getCol() / sectionWidth)) ==
                            (int) (Math.floor(cell.getRow() / sectionHeight) * sectionHeight + Math.floor(cell.getCol() / sectionWidth))) {
                        gc.deleteNote(cell.getValue());
                        if(gc.getNoteCount() == 0) {
                            existing = true;
                        }
                    }
                }
                return existing;
            }
        }, false);
    }

    private List<Integer> getPossibleValues(GameCell c) {
        List<Integer> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            if(c.getNotes()[i]) {
                result.add(i+1);
            }
        }
        return result;
    }

    private List<GameCell> getListOfCandidates(GameBoard gameBoard) {
        final ArrayList<GameCell> candidates = new ArrayList<>();

        gameBoard.actionOnCells(new ICellAction<ArrayList<GameCell>>() {
            @Override
            public ArrayList<GameCell> action(GameCell gc, ArrayList<GameCell> existing) {
                if(gc.getNoteCount() > 1) {
                    existing.add(gc);
                }
                return existing;
            }
        }, candidates);

        return candidates;
    }

    private void setNotes(GameBoard gameBoard) {
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                for(int k = 1; k <= gameBoard.getSize(); k++) {
                    gameBoard.getCell(i,j).setNote(k);
                }
            }
        }
    }

}
