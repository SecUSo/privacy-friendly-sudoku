package org.secuso.privacyfriendlysudoku.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.core.util.Pair;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.CreateSudokuActivity;
import org.secuso.privacyfriendlysudoku.ui.GameActivity;
import org.secuso.privacyfriendlysudoku.ui.listener.IFinalizeDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IImportDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.R;

public class CreateSudokuPresenter implements CreateSudokuContract.Presenter, IImportDialogFragmentListener {

    private CreateSudokuContract.View view;

    GameController gameController;

    public void setGameController(Parcelable parcel) {
        this.gameController = (GameController)parcel;
    }

    public GameController getGameController() {
        return gameController;
    }

    @Override
    public void attachView(CreateSudokuContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView(CreateSudokuContract.View view) {
        view = null;
    }

    public void createGame(Bundle savedInstanceState, SharedPreferences sharedPref, Intent intent, Context context) {
        if(savedInstanceState == null) {

            gameController = GameController.getInstance(sharedPref, context);

            Bundle extras = intent.getExtras();
            GameType gameType = GameType.valueOf(extras.getString("gameType", GameType.Default_9x9.name()));
            int sectionSize = gameType.getSize();
            int boardSize = sectionSize * sectionSize;

            GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Moderate,
                    gameType, new int[boardSize], new int[boardSize], new boolean[boardSize][sectionSize]);
            gameController.loadLevel(container);
        } else {
            gameController = savedInstanceState.getParcelable("gameController");
            if(gameController != null) {
                gameController.removeAllListeners();
                gameController.setContextAndSettings(context, sharedPref);
            }
        }
    }

    private Pair<String, StringBuilder> checkUri(String input) {
        String inputSudoku = null;
        StringBuilder errorMessage = new StringBuilder();

        /*  remove the present prefix, or, if the input contains none of the valid prefixes, notify the user
         that their input is not valid */
        for (int i = 0; i < GameActivity.validUris.size(); i++) {
            String prefix = GameActivity.validUris.get(i).getHost().equals("") ?
                    GameActivity.validUris.get(i).getScheme() + "://" :
                    GameActivity.validUris.get(i).getScheme() + "://" + GameActivity.validUris.get(i).getHost() + "/";
            if (input.startsWith(prefix)) {
                inputSudoku = input.replace(prefix, "");
                break;
            }

            String endOfRecord = (i == GameActivity.validUris.size() - 1) ? "" : ", ";
            errorMessage.append(prefix);
            errorMessage.append(endOfRecord);
        }

        return new Pair(inputSudoku, errorMessage);
    }

    private boolean isValidInputSudoku(String inputSudoku) {
        boolean validSize = Math.sqrt(inputSudoku.length()) == gameController.getSize();
        if (!validSize)
            return false;

        //check whether or not the sudoku is valid and has a unique solution
        boolean solvable = CreateSudokuActivity.verify(gameController.getGameType(), inputSudoku);

        if (!solvable) {
            return false;
        }

        return true;
    }
    public void onImportDialogPositiveClick(String input) {
        Pair<String, StringBuilder> importReult = checkUri(input);
        String inputSudoku = importReult.first;
        StringBuilder errorMessage = importReult.second;

        // the inputSudoku variable being null means the input did not match any of the valid prefixes
        if (inputSudoku == null) {
            view.showToast(errorMessage);
        }

        boolean isValid = isValidInputSudoku(inputSudoku);
        if(isValid) {
            // if the encoded sudoku is solvable, sent the code directly to the GameActivity; if not, notify the user
            int boardSize = gameController.getGameType().getSize() * gameController.getGameType().getSize();
            GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                    gameController.getGameType(), new int [boardSize], new int [boardSize],
                    new boolean [boardSize][gameController.getGameType().getSize()]);
            container.parseSetValues(inputSudoku);

            gameController.loadLevel(container);
            view.setUpLayout();
        } else {
            view.showToast();
        }
    }

    /**
     * Implements the onDialogNegativeClick() method of the IFinalizeDialogFragmentListener
     * interface.
     */
    public void onDialogNegativeClick() {

    }
}
