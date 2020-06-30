package org.secuso.privacyfriendlysudoku.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.view.R;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuFieldLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuKeyboardLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuSpecialButtonLayout;

public class CreateSudokuActivity extends BaseActivity {

    GameController gameController;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    SudokuSpecialButtonLayout specialButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("pref_dark_mode_setting", false )) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(sharedPref.getBoolean("pref_keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        gameController = new GameController(sharedPref, getApplicationContext());

        if(sharedPref.getBoolean("pref_keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        Bundle extras = getIntent().getExtras();
        GameType gameType = GameType.valueOf(extras.getString("gameType", GameType.Default_9x9.name()));
        int sectionSize = gameType.getSize();
        int boardSize = sectionSize * sectionSize;

        GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                gameType, new int [boardSize], new int [boardSize], new boolean [boardSize][sectionSize]);
        gameController.loadLevel(container);

        setContentView(R.layout.activity_game_view);
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

        specialButtonLayout = (SudokuSpecialButtonLayout) findViewById(R.id.sudokuSpecialLayout);
        specialButtonLayout.setButtons(p.x, gameController, keyboard, getFragmentManager(), orientation, CreateSudokuActivity.this);

        gameController.notifyHighlightChangedListeners();

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

    private void verify() {
        Toast.makeText(CreateSudokuActivity.this, "Verifying...", Toast.LENGTH_SHORT).show();
        GameType gameType = gameController.getGameType();
        int boardSize = gameType.getSize() * gameType.getSize();
        QQWing verifier = new QQWing(gameType, GameDifficulty.Unspecified);

        GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                gameType, new int [boardSize], new int [boardSize], new boolean [boardSize][gameType.getSize()]);
        container.parseFixedValues(gameController.getCodeOfField());

        verifier.setRecordHistory(true);
        verifier.setPuzzle(container.getFixedValues());
        boolean solvable = verifier.solve();

        if(solvable) {
            Toast.makeText(CreateSudokuActivity.this, "Done verifying!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateSudokuActivity.this, "Your sudoku cannot be solved!", Toast.LENGTH_LONG).show();
        }

    }
}
