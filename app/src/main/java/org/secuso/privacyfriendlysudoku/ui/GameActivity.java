package org.secuso.privacyfriendlysudoku.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.game.listener.IGameSolvedListener;
import org.secuso.privacyfriendlysudoku.game.listener.ITimerListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IHintDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IResetDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.DialogWinScreen;
import org.secuso.privacyfriendlysudoku.ui.view.R;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuFieldLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuKeyboardLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuSpecialButtonLayout;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IGameSolvedListener ,ITimerListener, IHintDialogFragmentListener, IResetDialogFragmentListener {

    GameController gameController;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    SudokuSpecialButtonLayout specialButtonLayout;
    TextView timerView;
    TextView viewName ;
    RatingBar ratingBar;
    private boolean gameSolved = false;
    SaveLoadStatistics statistics = new SaveLoadStatistics(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameType gameType = GameType.Unspecified;
        GameDifficulty gameDifficulty = GameDifficulty.Unspecified;
        int loadLevelID = 0;
        boolean loadLevel = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Object o = extras.get("gameType");
            if(o instanceof GameType) {
                gameType = (GameType)extras.get("gameType");
            }
            gameDifficulty = (GameDifficulty)(extras.get("gameDifficulty"));
            loadLevel = extras.getBoolean("loadLevel", false);
            if(loadLevel) {
                loadLevelID = extras.getInt("loadLevelID");
            }
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_game_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.addView();

        //Create new GameField
        layout = (SudokuFieldLayout)findViewById(R.id.sudokuLayout);
        gameController = new GameController(sharedPref, getApplicationContext());
        gameController.registerGameSolvedListener(this);
        gameController.registerTimerListener(this);
        statistics.setGameController(gameController);
        List<GameInfoContainer> loadableGames = GameStateManager.getLoadableGameList();

        if(loadLevel && loadableGames.size() > loadLevelID) {
            // load level from GameStateManager
            gameController.loadLevel(loadableGames.get(loadLevelID));
        } else {
            // load a new level
            gameController.loadNewLevel(gameType, gameDifficulty);
        }

        layout.setSettingsAndGame(sharedPref, gameController);

        //set KeyBoard
        keyboard = (SudokuKeyboardLayout) findViewById(R.id.sudokuKeyboardLayout);
        keyboard.removeAllViews();
        keyboard.setGameController(gameController);
        //keyboard.setColumnCount((gameController.getSize() / 2) + 1);
        //keyboard.setRowCount(2);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        //int width = p.x;
        keyboard.setKeyBoard(gameController.getSize(), p.x,layout.getHeight()-p.y);


        //set Special keys
        specialButtonLayout = (SudokuSpecialButtonLayout) findViewById(R.id.sudokuSpecialLayout);
        specialButtonLayout.setButtons(p.x, gameController, keyboard, getFragmentManager());

        //set TimerView
        timerView = (TextView)findViewById(R.id.timerView);


        //set GameName
        viewName = (TextView) findViewById(R.id.gameModeText);
        viewName.setText(getString(gameController.getGameType().getStringResID()));


        //set Rating bar
        List<GameDifficulty> difficutyList = GameDifficulty.getValidDifficultyList();
        int numberOfStarts = difficutyList.size();
        ratingBar = (RatingBar) findViewById(R.id.gameModeStar);
        ratingBar.setMax(numberOfStarts);
        ratingBar.setNumStars(numberOfStarts);
        ratingBar.setRating(difficutyList.indexOf(gameController.getDifficulty()) + 1);
        ((TextView)findViewById(R.id.difficultyText)).setText(getString(gameController.getDifficulty().getStringResID()));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // start the game
        gameController.startTimer();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!gameSolved) {
            gameController.saveGame(this);
            gameController.pauseTimer();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(!gameSolved) {
            gameController.startTimer();
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Symbol s = Symbol.valueOf(Symbol.class, sharedPref.getString("pref_symbols", "Default"));
        layout.setSymbols(s);
        keyboard.setSymbols(s);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_view, menu);
        return true;
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent;

        switch(id) {
            case R.id.menu_reset:
                ResetConfirmationDialog hintDialog = new ResetConfirmationDialog();
                hintDialog.show(getFragmentManager(), "ResetDialogFragment");
                break;

            case R.id.nav_newgame:
                //create new game
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_settings:
                //open settings
                intent = new Intent(this,SettingsActivity.class);
                intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GamePreferenceFragment.class.getName() );
                intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                break;

            case R.id.nav_highscore:
                // see highscore list
                intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_about:
                //open about page
                intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_help:
                //open about page
                //intent = new Intent(this,HelpActivity.class);
                //startActivity(intent);
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onSolved() {
        gameSolved = true;

        gameController.pauseTimer();
        gameController.deleteGame(this);

        //Show time hints new plus old best time

        statistics.saveGameStats();

        Dialog dialog = new Dialog(this);
        //dialog.setContentView(getLayoutInflater().inflate(R.layout.win_screen_layout,null));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.setContentView(R.layout.win_screen_layout);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setContentView(R.layout.win_screen_layout);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();

        layout.setEnabled(false);
        keyboard.setButtonsEnabled(false);
        specialButtonLayout.setButtonsEnabled(false);
    }

    @Override
    public void onTick(int time) {
        if(gameSolved) return;

        //do something not so awesome
        int seconds = time % 60;
        int minutes = ((time -seconds)/60)%60 ;
        int hours = (time - minutes - seconds)/(3600);
        String h,m,s;
        s = (seconds< 10)? "0"+String.valueOf(seconds):String.valueOf(seconds);
        m = (minutes< 10)? "0"+String.valueOf(minutes):String.valueOf(minutes);
        h = (hours< 10)? "0"+String.valueOf(hours):String.valueOf(hours);
        timerView.setText(h + ":" + m + ":" + s);

        // save time
        gameController.saveGame(this);
    }

    @Override
    public void onHintDialogPositiveClick() {
        gameController.hint();
    }

    @Override
    public void onResetDialogPositiveClick() {
        gameController.resetLevel();
    }

    @Override
    public void onDialogNegativeClick() {
        // do nothing
    }
    @SuppressLint("ValidFragment")
    public class ResetConfirmationDialog extends DialogFragment {

        LinkedList<IResetDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IHintDialogFragmentListener) {
                listeners.add((IResetDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.reset_confirmation)
                    .setPositiveButton(R.string.reset_confirmation_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IResetDialogFragmentListener l : listeners) {
                                l.onResetDialogPositiveClick();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }
}
