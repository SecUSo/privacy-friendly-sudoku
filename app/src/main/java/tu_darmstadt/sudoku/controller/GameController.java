package tu_darmstadt.sudoku.controller;

import tu_darmstadt.sudoku.game.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameController {

    private int size;
    private GameField gameField;
    private List<CheckError> errorList = new ArrayList<CheckError>();

//    private SudokuSolver solver;
//    private SudokuGenerator generator;


    public GameController(int size) {
        this.size = size;
        gameField = new GameField(size);
    }

    public void setValue(int row, int col, int val) {
        GameCell cell = gameField.getCell(row, col);
        if (!cell.isFixed() && isValidNumber(val)) {
            cell.clearNotes();
            cell.setValue(val);
        }
    }

    public int getValue(int row, int col) {
        GameCell cell = gameField.getCell(row, col);
        return cell.getValue();
    }

    public int getSize() {
        return size;
    }

    /**
     * Test if the given parameter is a number from 1 to {code size}.
     * @param val the value to be checked.
     * @return true, if {@code val} is a number from 1 to {code size}.
     * @see GameController#getSize()
     */
    public boolean isValidNumber(int val) {
        return 0 < val && val <= size;
    }

    public boolean isSolved() {
        boolean solved = true;
        /*
        for(int i = 0; i < size; i++) {

            List<Integer> set = new ArrayList<Integer>();
            for(int j = 0; j < size; j++) {
                if(set.contains(gameField.getField()[i][j].getValue())) {
                    errorList.add(new CheckError(i,j,));
                } else {
                    set.add(gameField.getField()[i][j].getValue());
                }
            }

            if(!checkList() solved = false;
            if(!checkList(gameField.getColumn(i))) solved = false;
            if(!checkList(gameField.getSection(i))) solved = false;
        }*/
        return solved;
    }

    /**
     * Checks the given list if every number occurs only once.
     * @param list the list of {@link GameCell}s that is supposed to be tested.
     * @return true if every cell has a value and every value occurs only once
     */
    private boolean checkList(GameCell[] list) {
        /*List<Integer> set = new ArrayList<Integer>();
        for(GameCell c : list) {
            if(set.contains(c.getValue())) {

                errorList.add(new CheckError());
            }
        }*/
        return false;
    }

    public List<CheckError> getErrorList() {
        return errorList;
    }
}
