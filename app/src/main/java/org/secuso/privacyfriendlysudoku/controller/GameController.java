package org.secuso.privacyfriendlysudoku.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import org.secuso.privacyfriendlysudoku.controller.database.DatabaseHelper;
import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.CellConflict;
import org.secuso.privacyfriendlysudoku.game.CellConflictList;
import org.secuso.privacyfriendlysudoku.game.GameBoard;
import org.secuso.privacyfriendlysudoku.game.GameCell;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.game.ICellAction;
import org.secuso.privacyfriendlysudoku.game.listener.IGameSolvedListener;
import org.secuso.privacyfriendlysudoku.game.listener.IHighlightChangedListener;
import org.secuso.privacyfriendlysudoku.game.listener.IHintListener;
import org.secuso.privacyfriendlysudoku.game.listener.IModelChangedListener;
import org.secuso.privacyfriendlysudoku.game.listener.ITimerListener;
import org.secuso.privacyfriendlysudoku.ui.GameActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Chris on 06.11.2015.
 */
public class GameController implements IModelChangedListener, Parcelable {

    // General
    public static final int DAILY_SUDOKU_ID = Integer.MAX_VALUE - 1;
    private SharedPreferences settings;

    // View
    private Context context;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int selectedValue = 0;
    private int highlightValue = 0;

    private LinkedList<IHighlightChangedListener> highlightListeners = new LinkedList<>();
    private LinkedList<IGameSolvedListener> solvedListeners = new LinkedList<>();
    private LinkedList<IHintListener> hintListener = new LinkedList<>();
    private boolean notifiedOnSolvedListeners = false;

    // Game
    private int gameID = 0;         // 0 = empty id => will be assigned a free ID when saving
    private int size;
    private int sectionHeight;
    private int sectionWidth;
    private int usedHints = 0;
    private GameBoard gameBoard;
    private int[] solution = new int[0];
    private GameType gameType;
    private GameDifficulty difficulty;
    private CellConflictList errorList = new CellConflictList();

    // Undo Redo
    private UndoRedoManager undoRedoManager;

    // Solver / Generator
    private QQWingController qqWingController = new QQWingController();

    // Timer
    private int time = 0;
    private AtomicBoolean timerRunning = new AtomicBoolean(false);
    private LinkedList<ITimerListener> timerListeners = new LinkedList<>();
    private Handler timerHandler = new Handler();
    private Timer timer = new Timer();
    private TimerTask timerTask;

    private boolean noteStatus = false;

    // Constructors
    public GameController() {
        this(null, null);
    }
    public GameController(SharedPreferences pref, Context context) {
        this(GameType.Default_9x9, pref, context);
    }
    public GameController(GameType type, SharedPreferences pref, Context context) {
        this.context = context;
        this.gameBoard = new GameBoard(type);

        setGameType(type);
        setSettings(pref);

        initTimer();
    }

    public int getGameID() {
        return gameID;
    }

    public void loadNewLevel(GameType type, GameDifficulty difficulty) {
        NewLevelManager newLevelManager = NewLevelManager.getInstance(context, settings);

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
        this.usedHints = gic.getHintsUsed();

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

        if(solution == null || solution.length == 0) {
            solution = qqWingController.solve(gameBoard);
        }
        return solution;
    }

    /*public boolean loadLevel(GameBoard level) {
        if(GameBoard.isValid(level)) {
            gameBoard = level;
        }
    }*/

    public void hint(){
        if(!isValidCellSelected()) {
            return;
        }

        int[] solved = solve();
        // and reveal the selected value.
        int value = solved[selectedRow * getSize() + selectedCol];
        setValue(selectedRow, selectedCol, value);
        // add state to undo
        undoRedoManager.addState(gameBoard);
        highlightValue = value;

        usedHints++;

        notifyHintListener();
        notifyHighlightChangedListeners();
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
            cell.setValue(value);

            if(settings != null && settings.getBoolean("pref_automatic_note_deletion",true)) {
                LinkedList<GameCell> updateList = new LinkedList<GameCell>();
                updateList.addAll(gameBoard.getRow(cell.getRow()));
                updateList.addAll(gameBoard.getColumn(cell.getCol()));
                updateList.addAll(gameBoard.getSection(cell.getRow(), cell.getCol()));
                deleteNotes(updateList, value);
            }

            if(settings != null && settings.getBoolean("pref_highlightInputError",true)) {
                checkInputError(row, col);
            }
        }
    }

    private void checkInputError(int row, int col) {
        if(isValidNumber(row+1) && isValidNumber(col+1)) {

            if (errorList == null) {
                errorList = new CellConflictList();
            }

            errorList.addAll(checkInputErrorList(gameBoard.getCell(row,col), gameBoard.getRow(row)));
            errorList.addAll(checkInputErrorList(gameBoard.getCell(row, col), gameBoard.getColumn(col)));
            errorList.addAll(checkInputErrorList(gameBoard.getCell(row, col), gameBoard.getSection(row, col)));
        }
    }


    private CellConflictList checkInputErrorList(GameCell cell, List<GameCell> list) {
        CellConflictList errorList = new CellConflictList();
        for (int i = 0; i < list.size(); i++) {
            GameCell c2 = list.get(i);

            if (!cell.equals(c2) && cell.getValue() != 0 && c2.getValue() != 0) {

                // Same value in one set should not exist
                if (cell.getValue() == c2.getValue()) {
                    // we found an error..
                    errorList.add(new CellConflict(cell, c2));
                }
            }
        }
        return errorList;
    }

    public LinkedList<GameCell> getConnectedCells(int row, int col) {
        LinkedList<GameCell> list = new LinkedList<>();

        list.addAll(gameBoard.getRow(row));
        list.remove(gameBoard.getCell(row, col));
        list.addAll(gameBoard.getColumn(col));
        list.remove(gameBoard.getCell(row, col));
        list.addAll(gameBoard.getSection(row, col));
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
            if(gameID == DAILY_SUDOKU_ID - 1) {
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

    public void saveDailySudoku(Context context) {
        int amountOfCells = size * size;
        int[] encodedBoard = new int[amountOfCells];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                encodedBoard[i * size + j] = gameBoard.getCell(i, j).getValue();
            }
        }

        Calendar currentDate = Calendar.getInstance();
        int id = currentDate.get(Calendar.DAY_OF_MONTH) * 1000000
                + (currentDate.get(Calendar.MONTH) + 1) * 10000 + currentDate.get(Calendar.YEAR);

        DatabaseHelper db = new DatabaseHelper(context);
        DailySudoku dailySudoku = new DailySudoku(id, difficulty, gameType, encodedBoard, usedHints, GameActivity.timeToString(time));
        db.addDailySudoku(dailySudoku);
    }

    public void deleteGame(Context context) {
        if(gameID == 0) {
            throw new IllegalArgumentException("GameID may not be 0.");
        }
        GameStateManager fm = new GameStateManager(context, settings);
        fm.deleteGameStateFile(getInfoContainer());
    }

    public GameInfoContainer getInfoContainer() {
        // this functionality is not needed as of yet. this is not correctly implemented
        // but its sufficient to our needs
        return new GameInfoContainer(gameID, difficulty, gameType, null, null, null);
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

    public CellConflictList getErrorList() {
        return errorList;
    }

    public void resetLevel() {
        gameBoard.reset();

        if(settings.getBoolean("pref_timer_reset", true)) {
            time = 0;
            notifyTimerListener(0);
            undoRedoManager = new UndoRedoManager(gameBoard);
        } else {
            undoRedoManager.addState(gameBoard);
        }

        notifyHighlightChangedListeners();
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

    public String getCodeOfField() { return gameBoard.transformToCode(); }



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
    public int getSelectedCellsValue() {
        return isValidCellSelected() ? getGameCell(selectedRow, selectedCol).getValue() : 0;
    }

    public int getSelectedValue() {
        return selectedValue;
    }

    public void selectCell(int row, int col) {
        if(selectedValue != 0) {
            // we have a value selected.
            // we need to set the value directly now / toggle notes.
            if(noteStatus) {
                toggleNote(row, col, selectedValue);
            } else {
                setValue(row, col, selectedValue);
            }
            undoRedoManager.addState(gameBoard);

        } else if(selectedRow == row && selectedCol == col) {
            // if we select the same field 2ce -> deselect it
            selectedRow = -1;
            selectedCol = -1;
            highlightValue = 0;
        } else {
            // else we set it to the new selected field
            selectedRow = row;
            selectedCol = col;
            // highlight the selected value only if its not 0.
            int v = getGameCell(row, col).getValue();
            if(v != 0) {
                highlightValue = v;
            }
        }
        notifyHighlightChangedListeners();
    }

    public int getHighlightedValue() {
        return highlightValue;
    }

    public boolean isValueHighlighted() {
        return highlightValue > 0 && highlightValue <= size;
    }

    public void selectValue(int value) {
        if(isValidCellSelected()) {
            if(noteStatus) {
                toggleNote(selectedRow, selectedCol, value);
                undoRedoManager.addState(gameBoard);

                highlightValue = value;
            } else {
                if(getSelectedCellsValue() != value) {
                    setValue(selectedRow, selectedCol, value);
                    // add state to undo
                    undoRedoManager.addState(gameBoard);

                    highlightValue = value;
                }
            }
        } else if(selectedRow == -1 && selectedCol == -1){
            if(value == selectedValue) {
                // if the value we are selecting is the one we already have selected... deselect it
                selectedValue = 0;
                highlightValue = 0;
            } else {
                selectedValue = value;
                highlightValue = value;
            }
        }

        notifyHighlightChangedListeners();
    }
    
    public void setNoteStatus(boolean enabled) {
        noteStatus = enabled;
    }

    public boolean getNoteStatus() {
        return noteStatus;
    }

    public void deleteSelectedCellsValue() {
        if(isValidCellSelected()) {
            deleteValue(selectedRow, selectedCol);
            // add state to undo
            undoRedoManager.addState(gameBoard);
            notifyHighlightChangedListeners();
        }
    }

    public void toggleSelectedCellsNote(int value) {
        if(isValidCellSelected()) {
            toggleNote(selectedRow, selectedCol, value);
            // add state to undo
            undoRedoManager.addState(gameBoard);
        }
    }

    public boolean isValidCellSelected() {
        return selectedRow != -1 && selectedCol != -1 && !getGameCell(selectedRow, selectedCol).isFixed();
    }

    public int getSectionHeight() {
        return sectionHeight;
    }

    public int getSectionWidth() {
        return sectionWidth;
    }

    @Override
    public void onModelChange(GameCell c) {

        checkErrorList();

        if(gameBoard.isFilled()) {
            errorList = new CellConflictList();
            if(gameBoard.isSolved(errorList)) {
                if(!notifiedOnSolvedListeners) {
                    notifiedOnSolvedListeners = true;
                    notifySolvedListeners();
                    resetSelects();
                }
            }// else {
                // errorList now holds all the errors => display errors
                //notifyErrorListener(errorList);
                //resetSelects();
            //}
        } else {
            notifiedOnSolvedListeners = false;
        }
    }

    public void checkErrorList() {
        LinkedList<CellConflict> toRemove = new LinkedList<>();
        for(CellConflict cc : errorList) {
            if(cc.getCell1().getValue() != cc.getCell2().getValue()) {
                toRemove.add(cc);
            }
        }
        errorList.removeAll(toRemove);
    }

    public void resetSelects() {
        selectedCol = -1;
        selectedRow = -1;
        selectedValue = 0;
        highlightValue = 0;
        notifyHighlightChangedListeners();
    }

    public void registerGameSolvedListener(IGameSolvedListener l) {
        if(!solvedListeners.contains(l)) {
            solvedListeners.add(l);
        }
    }

    public void registerHighlightChangedListener(IHighlightChangedListener l) {
        if(!highlightListeners.contains(l)) {
            highlightListeners.add(l);
        }
    }

    public void removeGameSolvedListener(IGameSolvedListener l) {
        if(solvedListeners.contains(l)) {
            solvedListeners.remove(l);
        }
    }

    public void notifyHighlightChangedListeners() {
        for(IHighlightChangedListener l : highlightListeners) {
            l.onHighlightChanged();
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

    public void notifyHintListener() {
        for (IHintListener listener : hintListener){
            listener.onHintUsed();
        }
    }

    /*public void notifyErrorListener(List<CellConflict> errorList) {
        for (IGameErrorListener listener : errorListeners){
            listener.onGameFilledWithErrors(errorList);
        }
    }*/

    public void registerHintListener(IHintListener listener){
        if (!hintListener.contains(listener)){
            hintListener.add(listener);
        }
    }

    public int getUsedHints(){
        return usedHints;
    }

    public void resetTime() {
        time = 0;
        notifyTimerListener(0);
    }

    public void initTimer() {
        deleteTimer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(timerRunning.get()) {
                            notifyTimerListener(++time);
                            //Log.d("Timer", "calling notifyTimerListener(" + time + ");");
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void deleteTimer() {
        pauseTimer();
        timer.cancel();
        timer.purge();
    }

    public void startTimer() {
        timerRunning.set(true);
        notifyHighlightChangedListeners();
    }

    public void pauseTimer(){
        timerRunning.set(false);
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

        notifyHighlightChangedListeners();
        return;
    }

    public int getValueCount(final int value) {
        return actionOnCells(new ICellAction<Integer>() {
            @Override
            public Integer action(GameCell gc, Integer existing) {
                return (gc.getValue() == value) ? existing + 1 : existing;
            }
        }, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        // get the gamecontroller a new context
        out.writeInt(selectedRow);
        out.writeInt(selectedCol);
        out.writeInt(selectedValue);
        out.writeInt(highlightValue);
        out.writeInt(gameID);
        out.writeInt(size);
        out.writeInt(sectionHeight);
        out.writeInt(sectionWidth);
        out.writeInt(usedHints);
        out.writeInt(time);

        out.writeInt(solution.length);
        out.writeIntArray(solution);

        out.writeInt(noteStatus ? 1 : 0);
        out.writeInt(notifiedOnSolvedListeners ? 1 : 0);

        out.writeParcelable(gameType, 0);
        out.writeParcelable(difficulty, 0);
        out.writeParcelable(gameBoard, 0);
        //out.writeParcelable(undoRedoManager, 0);

        out.writeTypedList(errorList);
    }

    public static final Parcelable.Creator<GameController> CREATOR = new Parcelable.Creator<GameController>() {
        public GameController createFromParcel(Parcel in) {
            return new GameController(in);
        }

        public GameController[] newArray(int size) {
            return new GameController[size];
        }
    };

    /** recreate object from parcel */
    private GameController(Parcel in) {

        selectedRow = in.readInt();
        selectedCol = in.readInt();
        selectedValue = in.readInt();
        highlightValue = in.readInt();
        gameID = in.readInt();
        size = in.readInt();
        sectionHeight = in.readInt();
        sectionWidth = in.readInt();
        usedHints = in.readInt();
        time = in.readInt();

        solution = new int[in.readInt()];
        in.readIntArray(solution);

        noteStatus = in.readInt() == 1;
        notifiedOnSolvedListeners = in.readInt() == 1;

        gameType = in.readParcelable(GameType.class.getClassLoader());
        difficulty = in.readParcelable(GameDifficulty.class.getClassLoader());
        gameBoard = in.readParcelable(GameBoard.class.getClassLoader());
        undoRedoManager = new UndoRedoManager(gameBoard);//*/in.readParcelable(UndoRedoManager.class.getClassLoader());

        in.readTypedList(errorList, CellConflict.CREATOR);

        removeAllListeners();

        gameBoard.removeAllListeners();
        gameBoard.registerOnModelChangeListener(this);
    }

    public void removeAllListeners() {
        highlightListeners = new LinkedList<>();
        solvedListeners = new LinkedList<>();
        hintListener = new LinkedList<>();
        timerListeners = new LinkedList<>();
    }

    public void setContextAndSettings(Context applicationContext, SharedPreferences sharedPref) {
        context = applicationContext;
        setSettings(sharedPref);
    }
}
