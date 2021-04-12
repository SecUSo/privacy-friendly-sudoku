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
package org.secuso.privacyfriendlysudoku.controller.database.model;

import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

public class Level {
    protected int id = -1;
    private GameDifficulty difficulty = GameDifficulty.Unspecified;
    private GameType gameType = GameType.Unspecified;
    private int[] puzzle;

    public Level() {}

    public Level(int id, GameDifficulty difficulty, GameType gameType, int[] puzzle) {
        this.id = id;
        this.difficulty = difficulty;
        this.gameType = gameType;
        this.puzzle = puzzle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public int[] getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(int[] puzzle) {
        this.puzzle = puzzle;
    }
    public void setPuzzle(String puzzleString) {
        int[] puzzle = new int[gameType.getSize()*gameType.getSize()];

        if(puzzle.length != puzzleString.length()) {
            throw new IllegalArgumentException("Saved level does not have the correct size.");
        }

        for(int i = 0; i < puzzleString.length(); i++) {
            puzzle[i] = Symbol.getValue(Symbol.SaveFormat, String.valueOf(puzzleString.charAt(i)))+1;
        }
        this.puzzle = puzzle;
    }
}
