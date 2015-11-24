package tu_darmstadt.sudoku.controller;

import java.util.LinkedList;
import java.util.List;

import tu_darmstadt.sudoku.game.GameBoard;

/**
 * Created by Chris on 24.11.2015.
 */
public class UndoRedoManager {

    private int activeState;
    private LinkedList<GameBoard> states = new LinkedList<>();

    public UndoRedoManager(GameBoard initState) {
        // we get the base state and set it as active state.
        try {

            states.addLast(initState.clone());
            activeState = 0;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public boolean isUnDoAvailable() {
        return activeState > 0 && states.size() > 1;
    }

    public boolean isRedoAvailable() {
        return activeState < states.size()-1;
    }

    public GameBoard UnDo() {
        if(isUnDoAvailable()) {
            return states.get(--activeState);
        } else {
            return null;
        }
    }

    public GameBoard ReDo() {
        if(isRedoAvailable()) {
            return states.get(++activeState);
        } else {
            return null;
        }
    }

    public void addState(GameBoard gameBoard) {

        LinkedList<GameBoard> deleteList = new LinkedList<>();

        for(int i = 0; i < states.size(); i++) {
            if (i > activeState) {                  // 3 states // state 1 is active // means 0,[1],2
                // delete rest of the list          // i > activeState // i > 1 // so i = 2 will be deleted // 0 can not be deleted
                deleteList.add(states.get(i));
            }
        }
        for(GameBoard g : deleteList) {
            states.removeLastOccurrence(g);
        }

        // then append the current state
        try {

            GameBoard board = gameBoard.clone();
            states.addLast(board);
            activeState = states.size()-1;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
