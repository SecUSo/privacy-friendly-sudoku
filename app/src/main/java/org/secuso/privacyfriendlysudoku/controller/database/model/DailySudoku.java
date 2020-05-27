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

    public int getTimeNeededInSeconds() {
        if (timeNeeded.matches("/d/d:/d/d:/d/d")) {
            String[] timeInstances = timeNeeded.split(":");
            int hourIndex = 0;
            int minuteIndex = 1;
            int secondIndex = 2;

            int minutes = Integer.parseInt(timeInstances[hourIndex]) * 60 + Integer.parseInt(timeInstances[minuteIndex]);
            return minutes * 60 + Integer.parseInt(timeInstances[secondIndex]);
        }

        return 0;
    }

    public void setTimeNeeded(String timeNeeded) {
        this.timeNeeded = timeNeeded;
    }
}
