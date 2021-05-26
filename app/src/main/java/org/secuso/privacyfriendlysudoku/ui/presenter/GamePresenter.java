package org.secuso.privacyfriendlysudoku.ui.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;

import java.util.List;

public class GamePresenter implements gameContract.Presenter{
    private gameContract.View view;

    GameController gameController;
    GameInfoContainer container;

    GameType gameType = GameType.Unspecified;
    GameDifficulty gameDifficulty = GameDifficulty.Unspecified;
    int loadLevelID = 0;
    boolean loadLevel = false;

    public void setGameController(Parcelable parcel) {
        this.gameController = (GameController) parcel;
    }
    public GameController getGameController() {
        return gameController;
    }

    public void attachView(gameContract.View view) {
        this.view = view;
    }
    public void detachView(gameContract.View view) {
        view = null;
    }

    public void init(SharedPreferences sharedPref, Context context) {
        gameController = new GameController(sharedPref, context);
    }

    public boolean setSudoku(boolean _startGame, List<Uri> validUris, Uri data) {
        boolean startGame = _startGame;
        String input = "";

        for (int i = 0; i < validUris.size(); i++) {
            if (data.getScheme().equals(validUris.get(i).getScheme())) {
                if (validUris.get(i).getHost().equals("")) {
                    input = data.getHost();
                    break;
                }
                else if (data.getHost().equals(validUris.get(i).getHost())) {
                    input = data.getPath();
                    input = input.replace("/", "");
                    break;
                }
            }
        }

        // Save all of the information that can be extracted from the encoded board in a GameInfoContainer object
        int sectionSize = (int)Math.sqrt(input.length());
        int boardSize = sectionSize * sectionSize;
        QQWing difficultyCheck;
        container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                GameType.Unspecified, new int [boardSize], new int [boardSize], new boolean [boardSize][sectionSize]);
        // always set custom sudokus as custom
        // TODO: maybe introduce a setting in the settings page to let the user decide
        container.setCustom(true);

        try {
            container.parseGameType("Default_" + sectionSize + "x" + sectionSize);
            container.parseFixedValues(input);
            difficultyCheck = new QQWing(container.getGameType(), GameDifficulty.Unspecified);

            // calculate difficulty of the imported sudoku
            difficultyCheck.setRecordHistory(true);
            difficultyCheck.setPuzzle(container.getFixedValues());
            difficultyCheck.solve();

            container.parseDifficulty(difficultyCheck.getDifficulty().toString());

            // A sudoku is that does not have a unique solution is deemed 'unplayable' and may not be started
            startGame = difficultyCheck.hasUniqueSolution();


        } catch (IllegalArgumentException e) {
            // If the imported code does not actually encode a valid sudoku, it needs to be rejected
            startGame = false;

                    /*
                     set up a blank sudoku field that can be displayed in the activity while the player is notified that
                     the link they imported does not encode a valid sudoku
                     */
            sectionSize = GameType.Default_9x9.getSize();
            boardSize = sectionSize * sectionSize;
            container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                    GameType.Default_9x9, new int [boardSize], new int [boardSize], new boolean [boardSize][sectionSize]);
        }
        return startGame;
    }
    public void loadLevel(List<GameInfoContainer> loadableGames) {
        if (loadLevel) {
            if (loadableGames.size() > loadLevelID) {
                // load level from GameStateManager
                gameController.loadLevel(loadableGames.get(loadLevelID));
            } else if (loadLevelID == GameController.DAILY_SUDOKU_ID) {
                for (GameInfoContainer container : loadableGames) {
                    if (container.getId() == loadLevelID) {
                        gameController.loadLevel(container);
                        break;
                    }
                }
            }
        } else {
            // load a new level
            gameController.loadNewLevel(gameType, gameDifficulty);
        }
    }

    public void loadLevel(){
        gameController.loadLevel(container);
    }

    public boolean setFromExtras(boolean _isDailySudoku, Bundle extras) {
        boolean isDailySudoku = _isDailySudoku;
        gameType = GameType.valueOf(extras.getString("gameType", GameType.Default_9x9.name()));
        gameDifficulty = GameDifficulty.valueOf(extras.getString("gameDifficulty", GameDifficulty.Moderate.name()));
        isDailySudoku = extras.getBoolean("isDailySudoku", false);
        loadLevel = extras.getBoolean("loadLevel", false);
        if (loadLevel) {
            loadLevelID = extras.getInt("loadLevelID");
        }
        return isDailySudoku;
    }

    public boolean checkNewBestTime(SaveLoadStatistics statistics) {
        boolean isNewBestTime;
        if (!gameController.gameIsCustom()) {
            //Show time hints new plus old best time
            statistics.saveGameStats();
            isNewBestTime = gameController.getUsedHints() == 0
                    && statistics.loadStats(gameController.getGameType(),gameController.getDifficulty()).getMinTime() >= gameController.getTime();

        } else {
            // cannot be best time if sudoku is custom
            isNewBestTime = false;
        }
        return isNewBestTime;
    }
}
