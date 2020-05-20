package org.secuso.privacyfriendlysudoku.controller.database.model;

import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

public class DailySudoku extends Level {
    private int hintsUsed;
    private String timeNeeded;

    public DailySudoku(int id, GameDifficulty gameDifficulty, GameType gameType, int[] puzzle, int hintsUsed, String timeNeeded) {
        super(id, gameDifficulty, gameType, puzzle);
        this.hintsUsed = hintsUsed;
        this.timeNeeded = timeNeeded;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public String getTimeNeeded() {
        return timeNeeded;
    }

    public void setTimeNeeded(String timeNeeded) {
        this.timeNeeded = timeNeeded;
    }
}
