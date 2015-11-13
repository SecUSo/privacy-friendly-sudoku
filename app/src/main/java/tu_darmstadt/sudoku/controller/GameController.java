package tu_darmstadt.sudoku.controller;

import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.CellConflictList;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameField;
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
    private GameField gameField;
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
        this.gameType = type;
        setGameType(type);
        gameField = new GameField(size, sectionHeight, sectionWidth);
        setSettings(pref);
        setValue(0, 1, 8);        setValue(0, 4, 2);
        setValue(0, 5, 6);        setValue(0, 6, 7);
        setValue(0, 7, 3);        setValue(0, 8, 4);
        setNote(6, 3, 1); setNote(6, 3, 2); setNote(6, 3, 3); setNote(6, 3, 4);
        setNote(6, 3, 5); setNote(6, 3, 6); setNote(6, 3, 7); setNote(6, 3, 8);
        setNote(6, 3, 9);

        setNote(7, 3, 2); setNote(7, 3, 3); setNote(7, 3, 4);
        setNote(7, 3, 5);  setNote(7, 3, 7); setNote(7, 3, 8);
        setNote(7, 3, 9);
    }

    public void loadLevel(int size, int sectionHeight, int sectionWidth, int[] fixedValues, int[] setValues, int[][] setNotes) {
        this.size = size;
        this.sectionHeight = sectionHeight;
        this.sectionWidth = sectionWidth;
        this.gameField = new GameField(size, sectionHeight, sectionWidth);

        if(fixedValues == null) throw new IllegalArgumentException("fixedValues may not be null.");

        gameField.initCells(fixedValues);

        // now set the values that are not fixed
        if (setValues != null) {
            for (int i = 0; i < size * size; i++) {
                int row = (int) Math.floor(i / size);
                int col = i % size;
                setValue(row, col, setValues[i]);
            }
        }

        if(setNotes != null) {
            // set notes.
        }

    }

    public void setSettings(SharedPreferences pref) {
        settings = pref;
    }

    private GameField solve(GameField gameField) {
        switch(gameType) {
            case Default_9x9:
            case Default_6x6:
            case Default_12x12:
                solver = new Solver(gameField);
                break;
            default:
                throw new UnsupportedOperationException("No Solver for this GameType defined.");
        }

        if(solver.solve()) {
            return solver.getGameField();
        }
        return null;
    }

    /*public boolean loadLevel(GameField level) {
        if(GameField.isValid(level)) {
            gameField = level;
        }
    }*/

    private void setGameType(GameType type) {
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
        return gameField.getCell(row, col);
    }

    public boolean isSolved() {
        return gameField.isSolved(new LinkedList<CellConflict>());
    }

    public void setValue(int row, int col, int value) {
        GameCell cell = gameField.getCell(row, col);
        if (!cell.isFixed() && isValidNumber(value)) {
            cell.deleteNotes();
            cell.setValue(value);

            if(settings != null && settings.getBoolean("pref_automatic_note_deletion",true)) {
                LinkedList<GameCell> updateList = new LinkedList<GameCell>();
                updateList.addAll(gameField.getRow(cell.getRow()));
                updateList.addAll(gameField.getColumn(cell.getCol()));
                updateList.addAll(gameField.getSection(cell.getRow(), cell.getCol()));
                deleteNotes(updateList, value);
            }
        }
    }

    public LinkedList<GameCell> getConnectedCells(int row, int col, boolean connectedRow, boolean connectedCol, boolean connectedSec) {
        LinkedList<GameCell> list = new LinkedList<>();

        if(connectedRow) list.addAll(gameField.getRow(row));
        list.remove(gameField.getCell(row, col));
        if(connectedCol) list.addAll(gameField.getColumn(col));
        list.remove(gameField.getCell(row, col));
        if(connectedSec) list.addAll(gameField.getSection(row, col));
        list.remove(gameField.getCell(row, col));

        return list;
    }

    public void deleteNotes(List<GameCell> updateList, int value) {
        for(GameCell c : updateList) {
            c.deleteNote(value);
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

    public List<CellConflict> getErrorList() {
        return errorList;
    }

    public void resetLevel() {
        gameField.reset();
        //notifyListeners();
    }

    public boolean deleteValue(int row, int col) {
        GameCell c = gameField.getCell(row,col);
        if(!c.isFixed()) {
            c.setValue(0);
            //notifyListeners();
            return true;
        }
        return false;
    }

    public void setNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.setNote(value);
        //notifyListeners();
    }

    public boolean[] getNotes(int row, int col) {
        GameCell c = gameField.getCell(row,col);
        return c.getNotes().clone();
    }

    public void deleteNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.deleteNote(value);
        //notifyListeners();
    }

    public void toggleNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.toggleNote(value);
        //notifyListeners();
    }

    /** Debug only method
     *
     * @return the Field represented as a String
     */
    public String getFieldAsString() {
        return gameField.toString();
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
        if(selectedRow != -1 && selectedCol != -1) setValue(selectedRow, selectedCol, value);
    }

    public void deleteSelectedValue() {
        if(selectedRow != -1 && selectedCol != -1) setValue(selectedRow, selectedCol, 0);
    }

    public void toggleSelectedNote(int value) {
        if(selectedRow != -1 && selectedCol != -1) toggleNote(selectedRow, selectedCol, value);
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
