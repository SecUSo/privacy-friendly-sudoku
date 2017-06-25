package org.secuso.privacyfriendlysudoku.controller.database.model;

import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

public class Level {
    private int id = -1;
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
