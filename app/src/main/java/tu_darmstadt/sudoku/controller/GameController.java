package tu_darmstadt.sudoku.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tu_darmstadt.sudoku.game.CellConflict;
import tu_darmstadt.sudoku.game.CellConflictList;
import tu_darmstadt.sudoku.game.GameBoard;
import tu_darmstadt.sudoku.game.GameCell;
import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.game.ICellAction;
import tu_darmstadt.sudoku.game.listener.IGameSolvedListener;
import tu_darmstadt.sudoku.game.listener.IModelChangedListener;
import tu_darmstadt.sudoku.game.listener.ITimerListener;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameController implements IModelChangedListener {

    private int size;
    private int sectionHeight;
    private int sectionWidth;
    private GameBoard gameBoard;
    private int[] solution;
    private GameType gameType;
    private int selectedRow;
    private int selectedCol;
    private SharedPreferences settings;
    private int gameID = 0;
    private GameDifficulty difficulty;
    private CellConflictList errorList = new CellConflictList();
    private UndoRedoManager undoRedoManager;
    private int selectedValue;
    private LinkedList<IGameSolvedListener> solvedListeners = new LinkedList<>();
    private boolean notifiedOnSolvedListeners = false;
    private Timer timer;
    private android.os.Handler handler = new android.os.Handler();
    private TimerTask timerTask;
    private int time = 0;
    private LinkedList<ITimerListener> timerListeners = new LinkedList<>();
    private boolean timerRunning = false;
    private QQWingController qqWingController = new QQWingController();
    private Context context;

//    private Solver solver;
//    private SudokuGenerator generator;

    public GameController(SharedPreferences pref, Context context) {
        this(GameType.Default_9x9, pref, context);
    }

    public GameController() {
        this(null, null);
    }

    public GameController(GameType type, SharedPreferences pref, Context context) {
        setGameType(type);
        this.context = context;
        setSettings(pref);
        gameBoard = new GameBoard(type);
        initTimer();
    }

    public int getGameID() {
        return gameID;
    }

    public void loadNewLevel(GameType type, GameDifficulty difficulty) {
        NewLevelManager newLevelManager = NewLevelManager.getInstance();

        int[] level = newLevelManager.loadLevel(type, difficulty);

        loadLevel(new GameInfoContainer(0, difficulty, type, level, null, null));

        newLevelManager.checkAndRestock();
    }

    public int getTime() {
        return time;
    }

    public void loadLevel(GameInfoContainer gic) {
        int[] fixedValues = gic.getFixedValues();
        int[] setValues = gic.getSetValues();
        boolean[][] setNotes = gic.getSetNotes();
        this.gameID = gic.getID();
        this.difficulty = gic.getDifficulty();
        this.time = gic.getTimePlayed();

        setGameType(gic.getGameType());
        this.gameBoard = new GameBoard(gic.getGameType());

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
                        setNote(row, col, k+1);
                    }
                }
            }
        }

        gameBoard.registerOnModelChangeListener(this);

        undoRedoManager = new UndoRedoManager(gameBoard);
        // call the solve function to get the solution of this board
        //qqWingController.solve(gameBoard);
    }

    public void setSettings(SharedPreferences pref) {
        settings = pref;
    }

    public int[] solve() {

        if(solution == null) {
            solution = qqWingController.solve(gameBoard);
        }
        return solution;
    }

    private void setGameType(GameType type) {
        this.gameType = type;
        this.size = type.getSize();
        this.sectionHeight = type.getSectionHeight();
        this.sectionWidth = type.getSectionWidth();
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

    public GameType getGameType() {
        return gameType;
    }

    public <T> T actionOnCells(ICellAction<T> ca, T existing) {
        return gameBoard.actionOnCells(ca, existing);
    }

    public boolean checkIfBoardIsFilled() {
        return gameBoard.isFilled();
    }

    public void saveGame(Context context) {
        if(settings == null) {
            return;
        }

        if(gameID == 0) {
            gameID = settings.getInt("lastGameID", 0)+1;

            SharedPreferences.Editor editor = settings.edit();
            // is anyone ever gonna play so many levels? :)
            if(gameID == Integer.MAX_VALUE-1) {
                editor.putInt("lastGameID", 1);
            } else {
                editor.putInt("lastGameID", gameID);
            }
            editor.commit();
        }

        //gameID now has a value other than 0 and hopefully unique
        GameStateManager fm = new GameStateManager(context, settings);
        fm.saveGameState(this);
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
        if(isValidNumber(value)) {
            GameCell c = gameBoard.getCell(row, col);
            c.setNote(value);
        }
        //notifyListeners();
    }

    public boolean[] getNotes(int row, int col) {
        GameCell c = gameBoard.getCell(row,col);
        return c.getNotes().clone();
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void deleteNote(int row, int col, int value) {
        GameCell c = gameBoard.getCell(row,col);
        c.deleteNote(value);
        //notifyListeners();
    }

    public void toggleNote(int row, int col, int value) {
        GameCell c = gameBoard.getCell(row,col);
        if(c.hasValue()) {
            c.setValue(0);
        }
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




    /*
     * Controls for the View.
     * Select methods for Cells and Values.
     * If a value is selected while a cell is selected the value is put into the cell.
     * If no cell is selected while a value is being selected, the value gets selected,
     * and every cell selection after that will automaticly put that value into the cell.
     *
     */
    public int getSelectedRow() {
        return selectedRow;
    }
    public int getSelectedCol() {
        return selectedCol;
    }
    public int getSelectedValue() {
        return selectedValue;
    }

    public void selectCell(int row, int col) {
        // TODO if there is a value selected
        // TODO should we do this in here or rather in the view?
        // we set the value directly
        //if(selectedValue != 0) {
        //}

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

    public void selectValue(int value) {
        if(isValidCellSelected() && getSelectedValue() != value) {
            setValue(selectedRow, selectedCol, value);
            // add state to undo
            undoRedoManager.addState(gameBoard);
        }

    }

    public void deleteSelectedValue() {
        if(isValidCellSelected() && getSelectedValue() != 0) {
            deleteValue(selectedRow, selectedCol);
            // add state to undo
            undoRedoManager.addState(gameBoard);
        }

    }

    public void toggleSelectedNote(int value) {
        if(isValidCellSelected()) {
            toggleNote(selectedRow, selectedCol, value);
            // add state to undo
            undoRedoManager.addState(gameBoard);
        }
    }

    public boolean isValidCellSelected() {
        return selectedRow != -1 && selectedCol != -1 && !getGameCell(selectedRow, selectedCol).isFixed();
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

    @Override
    public void onModelChange(GameCell c) {
        if(gameBoard.isFilled()) {
            List<CellConflict> errorList = new LinkedList<>();
            if(gameBoard.isSolved(errorList)) {
                if(!notifiedOnSolvedListeners) {
                    notifiedOnSolvedListeners = true;
                    notifySolvedListeners();
                    //TODO disable controls and play animation in view. onSolved method is called.
                }
            } else {

                // TODO: errorList now holds all the errors
                // TODO: display errors .. notify some view?
            }
        } else {
            notifiedOnSolvedListeners = false;
        }
    }

    public void registerGameSolvedListener(IGameSolvedListener l) {
        if(!solvedListeners.contains(l)) {
            solvedListeners.add(l);
        }
    }

    public void removeGameSolvedListener(IGameSolvedListener l) {
        if(solvedListeners.contains(l)) {
            solvedListeners.remove(l);
        }
    }

    public void notifySolvedListeners() {
        for(IGameSolvedListener l : solvedListeners) {
            l.onSolved();
        }
    }
    public void notifyTimerListener(int time) {
        for (ITimerListener listener : timerListeners){
            listener.onTick(time);
        }
    }

    public void registerTimerListener(ITimerListener listener){
        if (!timerListeners.contains(listener)){
            timerListeners.add(listener);
        }
    }

    private void initTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(timerRunning) {
                        notifyTimerListener(time++);
                    }
                }
            });

            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    public void startTimer() {
        if(!timerRunning) {
            timerRunning = true;
        }
    }

    public void pauseTimer(){
        if(timerRunning) {
            timerRunning = false;
        }
    }

    public void ReDo() {
        updateGameBoard(undoRedoManager.ReDo());
    }

    public void UnDo() {
        updateGameBoard(undoRedoManager.UnDo());
    }

    public boolean isRedoAvailable() {
        return undoRedoManager.isRedoAvailable();
    }
    public boolean isUndoAvailable() {
        return undoRedoManager.isUnDoAvailable();
    }

    public void updateGameBoard(final GameBoard gameBoard) {
        if(gameBoard == null) {
            return;
        }
        for(int i = 0; i < gameBoard.getSize(); i++) {
            for(int j = 0; j < gameBoard.getSize(); j++) {
                GameCell other_c = gameBoard.getCell(i,j);
                GameCell this_c = this.gameBoard.getCell(i,j);
                if(other_c.isFixed()) {
                    continue;
                }
                if(other_c.hasValue()) {
                    this_c.setValue(other_c.getValue());
                } else {
                    this_c.setValue(0);
                    for(int k = 0; k < gameBoard.getSize(); k++) {
                        if(other_c.getNotes()[k]) {
                            this_c.setNote(k+1);
                        } else {
                            this_c.deleteNote(k+1);
                        }
                    }
                }
            }
        }

        return;
    }

}
