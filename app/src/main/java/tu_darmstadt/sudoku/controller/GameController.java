package tu_darmstadt.sudoku.controller;

import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.CellConflictList;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.game.GameField;
import tu_darmstadt.sudoku.game.GameSettings;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.game.ICellAction;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameController {

    private int size;
    private int sectionHeight;
    private int sectionWidth;
    private GameField gameField;
    private CellConflictList errorList = new CellConflictList();
    private GameSettings settings = new GameSettings();
    private LinkedList<IModelChangeListener> listeners = new LinkedList<>();

//    private Default9x9Solver solver;
//    private SudokuGenerator generator;

    public GameController() {
        this(GameType.Default_9x9);
    }

    public GameController(GameType type) {
        setGameType(type);
        gameField = new GameField(size, sectionHeight, sectionWidth);
        setValue(0, 1, 8);        setValue(0, 4, 2);
        setValue(1, 1, 6);        setValue(1, 2, 7);
        setValue(2, 4, 8);        setValue(2, 5, 5);
        setValue(3, 4, 4);        setValue(3, 6, 6);
        setValue(4, 7, 9);        setValue(4, 8, 3);
        setValue(6, 0, 1);        setValue(6, 1, 5);
        setValue(7, 2, 8);        setValue(7, 3, 5);
        setValue(8, 2, 9);        setValue(8, 3, 4);
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
        return gameField.getCell(row,col);
    }

    public boolean isSolved() {
        return gameField.isSolved(new LinkedList<CellConflict>());
    }

    public void setValue(int row, int col, int value) {
        GameCell cell = gameField.getCell(row, col);
        if (!cell.isFixed() && isValidNumber(value)) {
            cell.deleteNotes();
            cell.setValue(value);

            if(settings.getEnableAutomaticNoteDeletion()) {
                LinkedList<GameCell> updateList = new LinkedList<GameCell>();
                updateList.addAll(gameField.getRow(cell.getRow()));
                updateList.addAll(gameField.getColumn(cell.getCol()));
                updateList.addAll(gameField.getSection(cell.getRow(), cell.getCol()));
                deleteNotes(updateList, value);
            }
        }
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
        gameField.actionOnCells(new ICellAction<Boolean>() {
            @Override
            public Boolean action(GameCell gc, Boolean existing) {
                gc.reset();
                return true;
            }
        }, true);
        notifyListeners();
    }

    public boolean deleteValue(int row, int col) {
        GameCell c = gameField.getCell(row,col);
        if(!c.isFixed()) {
            c.setValue(0);
            notifyListeners();
            return true;
        }
        return false;
    }

    public void setNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.setNote(value);
        notifyListeners();
    }

    public boolean[] getNotes(int row, int col) {
        GameCell c = gameField.getCell(row,col);
        return c.getNotes().clone();
    }

    public void deleteNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.deleteNote(value);
        notifyListeners();
    }

    public void toggleNote(int row, int col, int value) {
        GameCell c = gameField.getCell(row,col);
        c.toggleNote(value);
        notifyListeners();
    }

    /** Debug only method
     *
     * @return the Field represented as a String
     */
    public String getFieldAsString() {
        return gameField.toString();
    }

    public void registerListener(IModelChangeListener l) {
        if(!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public int getSectionHeight() {
        return sectionHeight;
    }

    public int getSectionWidth() {
        return sectionWidth;
    }

    public void notifyListeners() {
        for(IModelChangeListener l : listeners) {
            l.onModelChanged();
        }
    }

}
