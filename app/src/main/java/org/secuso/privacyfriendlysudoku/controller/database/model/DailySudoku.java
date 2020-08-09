package org.secuso.privacyfriendlysudoku.controller.database.model;

import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

/**
 * Models the content of a single row of the daily sudoku database
 */
public class DailySudoku extends Level {
    private int hintsUsed;
    private String timeNeeded;

    public DailySudoku(int id, GameDifficulty gameDifficulty, GameType gameType, int[] puzzle, int hintsUsed, String timeNeeded) {
        super(id, gameDifficulty, gameType, puzzle);
        this.hintsUsed = hintsUsed;
        this.timeNeeded = timeNeeded;
    }

    /**
     * Return the amount of hints the user needed to solve this sudoku
     * @return the amount of hints the user needed to solve this sudoku
     */
    public int getHintsUsed() {
        return hintsUsed;
    }

    /**
     * Set a new value for the hintsUsed attribute of this daily sudoku
     * @param hintsUsed the new value for the hintsUsed attribute
     */
    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    /**
     * Return the time the user needed to solve this sudoku as a string
     * @return the time the user needed to solve this sudoku as a string
     */
    public String getTimeNeeded() {
        return timeNeeded;
    }

    /**
     * Return the time the user needed to solve this sudoku in seconds
     * @return the time the user needed to solve this sudoku in seconds (or 0 if the timeNeeded parameter
     * does not have the right format)
     */
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

    /**
     * Set a new value for the timeNeeded attribute of this daily sudoku
     * @param timeNeeded the new value for the timeNeeded attribute
     */
    public void setTimeNeeded(String timeNeeded) {
        this.timeNeeded = timeNeeded;
    }
}
