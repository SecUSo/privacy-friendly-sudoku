package tu_darmstadt.sudoku.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import tu_darmstadt.sudoku.controller.SaveLoadController;
import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameInfoContainer;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.ui.view.R;
import tu_darmstadt.sudoku.ui.view.SudokuFieldLayout;
import tu_darmstadt.sudoku.ui.view.SudokuKeyboardLayout;
import tu_darmstadt.sudoku.ui.view.SudokuSpecialButtonLayout;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GameController gameController;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    SudokuSpecialButtonLayout specialButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameType gameType = GameType.Unspecified;
        int gameDifficulty = 0;
        int loadLevelID = 0;
        boolean loadLevel = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Object o = extras.get("gameType");
            if(o instanceof GameType) {
                gameType = (GameType)extras.get("gameType");
            }
            gameDifficulty = extras.getInt("gameDifficulty");
            loadLevel = extras.getBoolean("loadLevel");
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
        gameController = new GameController(sharedPref);

        List<GameInfoContainer> loadableGames = SaveLoadController.getLoadableGameList();

        if(loadLevel && loadableGames.size() > loadLevelID) {
            // load level from SaveLoadController
            gameController.loadLevel(loadableGames.get(loadLevelID));
        } else {
            // load a new level
            gameController.loadNewLevel(gameType, gameDifficulty);
        }

        layout.setGame(gameController);
        layout.setSettings(sharedPref);

        //set KeyBoard
        keyboard = (SudokuKeyboardLayout) findViewById(R.id.sudokuKeyboardLayout);
        keyboard.removeAllViews();
        keyboard.setGameController(gameController);
        keyboard.setColumnCount((gameController.getSize() / 2) + 1);
        keyboard.setRowCount(2);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        //int width = p.x;
        keyboard.setKeyBoard(gameController.getSize(), p.x);


        //set Special keys
        specialButtonLayout = (SudokuSpecialButtonLayout) findViewById(R.id.sudokuSpecialLayout);
        specialButtonLayout.setButtons(p.x,gameController,keyboard);
        /*
        // DEBUG
        String debug = gameController.getFieldAsString();
        Log.d("Sudoku", debug);
         */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            gameController.saveGame(getBaseContext());
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
            case R.id.nav_newgame:
                //create new game
                intent = new Intent(this, MainActivity.class);
                gameController.saveGame(getBaseContext());
                finish();
                startActivity(intent);
                break;

            case R.id.nav_continue:
                //create new game
                intent = new Intent(this, LoadGameActivity.class);
                gameController.saveGame(getBaseContext());
                finish();
                startActivity(intent);
                break;

            case R.id.menu_settings:
                //open settings
                intent = new Intent(this,SettingsActivity.class);
                gameController.saveGame(getBaseContext());
                finish();
                startActivity(intent);
                break;

            case R.id.nav_highscore:
                // see highscore list
                //intent = new Intent(this, HighscoreActivity.class);
                //startActivity(intent);
                break;

            case R.id.menu_about:
                //open about page
                intent = new Intent(this,AboutActivity.class);
                gameController.saveGame(getBaseContext());
                finish();
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
}
