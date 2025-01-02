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
package org.secuso.privacyfriendlysudoku.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.listener.IFinalizeDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IImportDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.CreateSudokuSpecialButtonLayout;
import org.secuso.privacyfriendlysudoku.R;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuFieldLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuKeyboardLayout;

/**
 * The CreateSudokuActivity is an activity which extends the BaseActivity and implements the
 * IFinalizeDialogFragementListener. It is used to create custom sudokus, which are passed to the
 * GameActivity afterwards.
 */
public class CreateSudokuActivity extends BaseActivity implements IFinalizeDialogFragmentListener, IImportDialogFragmentListener {

    GameController gameController;
    SharedPreferences sharedPref;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    CreateSudokuSpecialButtonLayout specialButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("pref_keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if(savedInstanceState == null) {

            gameController = new GameController(sharedPref, getApplicationContext());

            Bundle extras = getIntent().getExtras();
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
                gameController.setContextAndSettings(getApplicationContext(), sharedPref);
            }
        }

        setUpLayout();
    }

    private void setUpLayout() {

        setContentView(R.layout.activity_create_sudoku);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(gameController.getGameType().getStringResID()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = (SudokuFieldLayout)findViewById(R.id.sudokuLayout);
        layout.setSettingsAndGame(sharedPref, gameController);

        keyboard = (SudokuKeyboardLayout) findViewById(R.id.sudokuKeyboardLayout);
        keyboard.removeAllViews();
        keyboard.setGameController(gameController);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        int orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;

        keyboard.setKeyBoard(gameController.getSize(), p.x,layout.getHeight()-p.y, orientation);

        specialButtonLayout = (CreateSudokuSpecialButtonLayout) findViewById(R.id.createSudokuLayout);
        specialButtonLayout.setButtons(p.x, gameController, keyboard, getFragmentManager(), orientation,
                CreateSudokuActivity.this, this, this);

        gameController.notifyHighlightChangedListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Symbol s;
        try {
            s = Symbol.valueOf(sharedPref.getString("pref_symbols", Symbol.Default.name()));
        } catch(IllegalArgumentException e) {
            s = Symbol.Default;
        }
        layout.setSymbols(s);
        keyboard.setSymbols(s);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * Verifies an encoded sudoku board by testing whether or not it is uniquely solvable
     * @param gameType the type of the encoded sudoku
     * @param boardContent the encoded sudoku
     * @return whether or not the sudoku is uniquely solvable
     */
    public static boolean verify(GameType gameType, String boardContent) {
        int boardSize = gameType.getSize() * gameType.getSize();

        GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                gameType, new int [boardSize], new int [boardSize], new boolean [boardSize][gameType.getSize()]);

        try {
            container.parseFixedValues(boardContent);
        } catch (IllegalArgumentException e) {
            return false;
        }

        QQWing verifier = new QQWing(gameType, GameDifficulty.Unspecified);
        verifier.setRecordHistory(true);
        verifier.setPuzzle(container.getFixedValues());
        verifier.solve();

        return verifier.hasUniqueSolution();
    }

    /**
     * If the positive button of the FinalizeDialog is clicked, verify the sudoku. Immediately pass
     * it to the GameActivity, if the verification process is successful, and do nothing apart from
     * notifying the user if not.
     * Implements the onFinalizeDialogPositiveClick() method of the IFinalizeDialogFragmentListener
     * interface.
     */
    public void onFinalizeDialogPositiveClick() {
        Toast.makeText(CreateSudokuActivity.this, R.string.verify_custom_sudoku_process_toast, Toast.LENGTH_SHORT).show();
        String boardContent = gameController.getCodeOfField();
        boolean distinctlySolvable = verify(gameController.getGameType(), boardContent);

        if(distinctlySolvable) {
            Toast.makeText(CreateSudokuActivity.this, R.string.finished_verifying_custom_sudoku_toast, Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(this, GameActivity.class);

            /*
            Since the GameActivity expects the links of imported sudokus to start with an url scheme,
            add one to the start of the encoded board
             */
            String scheme = GameActivity.validUris.size() > 0 ? GameActivity.validUris.get(0).getScheme()
                    + "://" + GameActivity.validUris.get(0).getHost() : "";
            if (!scheme.equals("") && !scheme.endsWith("/")) scheme = scheme + "/";

            intent.setData(Uri.parse(scheme + boardContent));
            intent.putExtra("isCustom", true);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(CreateSudokuActivity.this, R.string.failed_to_verify_custom_sudoku_toast, Toast.LENGTH_LONG).show();
        }

    }
    public void onImportDialogPositiveClick(String input) {
        String inputSudoku = null;
        String prefix;
        StringBuilder errorMessage = new StringBuilder();

        /*  remove the present prefix, or, if the input contains none of the valid prefixes, notify the user
         that their input is not valid */
        for (int i = 0; i < GameActivity.validUris.size(); i++) {
            prefix = GameActivity.validUris.get(i).getHost().equals("") ?
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

        // the inputSudoku variable being null means the input did not match any of the valid prefixes
        if (inputSudoku == null) {
            Toast.makeText(CreateSudokuActivity.this,
                    this.getString(R.string.menu_import_wrong_format_custom_sudoku) + " " + errorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        boolean validSize = Math.sqrt(inputSudoku.length()) == gameController.getSize();

        if (!validSize) {
            Toast.makeText(CreateSudokuActivity.this, R.string.failed_to_verify_custom_sudoku_toast, Toast.LENGTH_LONG).show();
            return;
        }

        //check whether or not the sudoku is valid and has a unique solution
        boolean solvable = verify(gameController.getGameType(), inputSudoku);

        // if the encoded sudoku is solvable, sent the code directly to the GameActivity; if not, notify the user
        if (solvable) {
           int boardSize = gameController.getGameType().getSize() * gameController.getGameType().getSize();
            GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                    gameController.getGameType(), new int [boardSize], new int [boardSize],
                    new boolean [boardSize][gameController.getGameType().getSize()]);
            container.parseSetValues(inputSudoku);

            gameController.loadLevel(container);
            setUpLayout();

        } else {
            Toast.makeText(CreateSudokuActivity.this, R.string.failed_to_verify_custom_sudoku_toast, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Implements the onDialogNegativeClick() method of the IFinalizeDialogFragmentListener
     * interface.
     */
    public void onDialogNegativeClick() {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the user's current game state
        savedInstanceState.putParcelable("gameController", gameController);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        gameController = savedInstanceState.getParcelable("gameController");
    }
}
