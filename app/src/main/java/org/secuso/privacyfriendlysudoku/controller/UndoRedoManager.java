/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku.controller;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

import org.secuso.privacyfriendlysudoku.game.GameBoard;

/**
 * Created by Chris on 24.11.2015.
 */
public class UndoRedoManager implements Parcelable {

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

    public GameBoard unDo() {
        if(isUnDoAvailable()) {
            return states.get(--activeState);
        } else {
            return null;
        }
    }

    public GameBoard reDo() {
        if(isRedoAvailable()) {
            return states.get(++activeState);
        } else {
            return null;
        }
    }

    public void addState(GameBoard gameBoard) {

        // don't add duplicates right after each other..
        if(gameBoard.equals(states.get(activeState))) {
            return;
        }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(activeState);
        out.writeTypedList(states);
    }

    public static final Parcelable.Creator<UndoRedoManager> CREATOR
            = new Parcelable.Creator<UndoRedoManager>() {
        public UndoRedoManager createFromParcel(Parcel in) {
            return new UndoRedoManager(in);
        }

        public UndoRedoManager[] newArray(int size) {
            return new UndoRedoManager[size];
        }
    };

    /** recreate object from parcel */
    private UndoRedoManager(Parcel in) {
        activeState = in.readInt();
        in.readTypedList(states, GameBoard.CREATOR);
    }
}
