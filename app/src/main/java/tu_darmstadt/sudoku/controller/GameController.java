package tu_darmstadt.sudoku.controller;

import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.CellConflictList;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.game.solver.Solver;
import tu_darmstadt.sudoku.game.solver.ISolver;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameController {

    private int size;
    private int sectionHeight;
    private int sectionWidth;
    private GameBoard gameBoard;
    private ISolver solver;
    private GameType gameType;
    private int selectedRow;
    private int selectedCol;
    private SharedPreferences settings;
    private CellConflictList errorList = new CellConflictList();
    //private LinkedList<IModelChangeListener> listeners = new LinkedList<>();

//    private Solver solver;
//    private SudokuGenerator generator;

    public GameController(SharedPreferences pref) {
        this(GameType.Default_9x9, pref);
    }

    public GameController() {
        this(null);
    }

    public GameController(GameType type, SharedPreferences pref) {
        setGameType(type);
        gameBoard = new GameBoard(size, sectionHeight, sectionWidth);
        setSettings(pref);
    }

    public void loadNewLevel(GameType type, int difficulty) {
        switch(type) {
            case Default_6x6:
                loadLevel(GameType.Default_6x6,
                        new int[]{1,0,0,0,0,6,
                                  4,0,6,1,0,0,
                                  0,0,2,3,0,5,
                                  0,4,0,0,1,0,
                                  0,6,0,2,0,0,
                                  0,3,0,5,0,1}, null,null);
                break;
            case Default_12x12:
                loadLevel(GameType.Default_12x12,
                        new int[] {0, 2, 1, 0, 0, 6, 0, 0, 0, 8, 9, 0,
                                10, 0,12, 0, 0, 2, 1,11, 0, 0, 0, 6,
                                6, 0, 0, 4, 0,12, 0, 0, 0, 0, 2, 1,
                                0, 0, 0, 5, 0, 0, 0, 4,11,10, 0, 0,
                                0,10, 0, 1, 0, 0, 6, 0, 0, 0, 0, 0,
                                0, 7, 0, 0,11, 0, 0, 0, 0,12, 8, 9,
                                2, 1,11, 0, 0, 0, 0, 7, 0, 0, 6, 0,
                                0, 0, 0, 0, 0, 5, 0, 0, 4, 0,10, 0,
                                0, 0, 7, 3, 9, 0, 0, 0, 1, 0, 0, 0,
                                1, 5, 0, 0, 0, 0, 4, 0,10, 0, 0,11,
                                9, 0, 0, 0, 1,10, 2, 0, 0, 6, 0, 7,
                                0, 6,10, 0, 0, 0, 8, 0, 0, 1,12, 0}
                        ,null, null);
                break;
            case Default_9x9:
            case Unspecified:
            default:
                loadLevel(GameType.Default_9x9,
                        new int[]{5, 0, 1, 9, 0, 0, 0, 0, 0,
                                2, 0, 0, 0, 0, 4, 9, 5, 0,
                                3, 9, 0, 7, 0, 0, 0, 2, 6,
                                0, 3, 0, 0, 0, 1, 0, 7, 2,
                                0, 0, 6, 0, 5, 7, 0, 0, 0,
                                0, 7, 2, 0, 0, 9, 0, 4, 1,
                                0, 0, 0, 0, 7, 0, 4, 0, 9,
                                6, 4, 0, 0, 0, 0, 0, 0, 0,
                                7, 0, 0, 0, 1, 0, 3, 0, 5}
                        , null, null);
        }
    }

    public void loadLevel(GameType type, int[] fixedValues, int[] setValues, boolean[][] setNotes) {
        setGameType(type);
        this.gameBoard = new GameBoard(size, sectionHeight, sectionWidth);

        if(fixedValues == null) throw new IllegalArgumentException("fixedValues may not be null.");

        gameBoard.initCells(fixedValues);

        // now set the values that are not fixed
        if (setValues != null) {
            for (int i = 0; i < size * size; i++) {
                int row = (int) Math.floor(i / size);
                int col = i % size;
                setValue(row, col, setValues[i]);
            }
        }

        // set notes.
        if(setNotes != null) {
            for(int i = 0; i < size * size; i++) {
                int row = (int) Math.floor(i / size);
                int col = i % size;
                for(int k = 0 ; k < size; k++) {
                    if(setNotes[i][k]) {
                        setNote(row, col, k);
                    }
                }
            }
        }
    }

    public void setSettings(SharedPreferences pref) {
        settings = pref;
    }

    public GameBoard solve(GameBoard gameBoard) {
        switch(gameType) {
            case Default_9x9:
            case Default_6x6:
            case Default_12x12:
                solver = new Solver(gameBoard);
                break;
            default:
                throw new UnsupportedOperationException("No Solver for this GameType defined.");
        }

        if(solver.solve()) {
            return solver.getGameBoard();
        }
        return null;
    }

    /*public boolean loadLevel(GameBoard level) {
        if(GameBoard.isValid(level)) {
            gameBoard = level;
        }
    }*/

    private void setGameType(GameType type) {
        this.gameType = type;
        switch(type) {
            case Default_9x9:
                this.size = 9;
                this.sectionHeight = 3;
                this.sectionWidth = 3;
                break;
            case Default_12x12:
                this.size = 12;
                this.sectionHeight = 3;
                this.sectionWidth = 4;
                break;
            case Default_6x6:
                this.size = 6;
                this.sectionHeight = 2;
                this.sectionWidth = 3;
                break;
            case Unspecified:
            default:
                this.size = 1;
                this.sectionHeight = 1;
                this.sectionWidth = 1;
                throw new IllegalArgumentException("GameType can not be unspecified.");
        }
    }

    /** Use with care.
     */
    public GameCell getGameCell(int row, int col) {
        return gameBoard.getCell(row, col);
    }

    public boolean isSolved() {
        errorList = new CellConflictList();
        return gameBoard.isSolved(errorList);
    }

    public void setValue(int row, int col, int value) {
        GameCell cell = gameBoard.getCell(row, col);
        if (!cell.isFixed() && isValidNumber(value)) {
            cell.deleteNotes();
            cell.setValue(value);

            if(settings != null && settings.getBoolean("pref_automatic_note_deletion",true)) {
                LinkedList<GameCell> updateList = new LinkedList<GameCell>();
                updateList.addAll(gameBoard.getRow(cell.getRow()));
                updateList.addAll(gameBoard.getColumn(cell.getCol()));
                updateList.addAll(gameBoard.getSection(cell.getRow(), cell.getCol()));
                deleteNotes(updateList, value);
            }
        }
    }

    public LinkedList<GameCell> getConnectedCells(int row, int col, boolean connectedRow, boolean connectedCol, boolean connectedSec) {
        LinkedList<GameCell> list = new LinkedList<>();

        if(connectedRow) list.addAll(gameBoard.getRow(row));
        list.remove(gameBoard.getCell(row, col));
        if(connectedCol) list.addAll(gameBoard.getColumn(col));
        list.remove(gameBoard.getCell(row, col));
        if(connectedSec) list.addAll(gameBoard.getSection(row, col));
        list.remove(gameBoard.getCell(row, col));

        return list;
    }

    public void deleteNotes(List<GameCell> updateList, int value) {
        for(GameCell c : updateList) {
            c.deleteNote(value);
        }
    }

    public int getValue(int row, int col) {
        GameCell cell = gameBoard.getCell(row, col);
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

    public List<CellConflict> getErrorList() {
        return errorList;
    }

    public void resetLevel() {
        gameBoard.reset();
        //notifyListeners();
    }

    public boolean deleteValue(int row, int col) {
        GameCell c = gameBoard.getCell(row,col);
        if(!c.isFixed()) {
            c.setValue(0);
            //notifyListeners();
            return true;
        }
        return false;
    }

    public void setNote(int row, int col, int value) {
        GameCell c = gameBoard.getCell(row,col);
        c.setNote(value);
        //notifyListeners();
    }

    public boolean[] getNotes(int row, int col) {
        GameCell c = gameBoard.getCell(row,col);
        return c.getNotes().clone();
    }

    public void deleteNote(int row, int col, int value) {
        GameCell c = gameBoard.getCell(row,col);
        c.deleteNote(value);
        //notifyListeners();
    }

    public void toggleNote(int row, int col, int value) {
        GameCell c = gameBoard.getCell(row,col);
        c.toggleNote(value);
        //notifyListeners();
    }

    /** Debug only method
     *
     * @return the Field represented as a String
     */
    public String getFieldAsString() {
        return gameBoard.toString();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void selectCell(int row, int col) {
        if(selectedRow == row && selectedCol == col) {
            // if we select the same field 2ce -> deselect it
            selectedRow = -1;
            selectedCol = -1;
        } else {
            // else we set it to the new selected field
            selectedRow = row;
            selectedCol = col;
        }
    }

    public void setSelectedValue(int value) {
        if(isCellSelected()) setValue(selectedRow, selectedCol, value);
    }

    public void deleteSelectedValue() {
        if(isCellSelected()) deleteValue(selectedRow, selectedCol);
    }

    public void toggleSelectedNote(int value) {
        if(isCellSelected()) toggleNote(selectedRow, selectedCol, value);
    }

    public boolean isCellSelected() {
        return selectedRow != -1 && selectedCol != -1;
    }

//    public void registerListener(IModelChangeListener l) {
//        if(!listeners.contains(l)) {
//            listeners.add(l);
//        }
//    }

    public int getSectionHeight() {
        return sectionHeight;
    }

    public int getSectionWidth() {
        return sectionWidth;
    }

//    public void notifyListeners() {
//        for(IModelChangeListener l : listeners) {
//            l.onModelChanged();
//        }
//    }

}
