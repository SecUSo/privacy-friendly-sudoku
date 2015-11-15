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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.ui.view.R;
import tu_darmstadt.sudoku.ui.view.SudokuFieldLayout;
import tu_darmstadt.sudoku.ui.view.SudokuButton;
import tu_darmstadt.sudoku.ui.view.SudokuKeyboardLayout;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GameController gameController;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    SudokuButton [] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_game_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create new GameField
        layout = (SudokuFieldLayout)findViewById(R.id.sudokuLayout);
        gameController = new GameController(sharedPref);
        /*gameController.loadLevel(GameType.Default_9x9,
                new int[]{5, 0, 1, 9, 0, 0, 0, 0, 0,
                        2, 0, 0, 0, 0, 4, 9, 5, 0,
                        3, 9, 0, 7, 0, 0, 0, 2, 6,
                        0, 3, 0, 0, 0, 1, 0, 7, 2,
                        0, 0, 6, 0, 5, 7, 0, 0, 0,
                        0, 7, 2, 0, 0, 9, 0, 4, 1,
                        0, 0, 0, 0, 7, 0, 4, 0, 9,
                        6, 4, 0, 0, 0, 0, 0, 0, 0,
                        7, 0, 0, 0, 1, 0, 3, 0, 5}
                , null, null);*/
        gameController.loadLevel(GameType.Default_12x12,
                new int[] {0, 2, 1, 0, 0, 6, 0, 0, 0, 8, 9, 0,
                          10, 0,12, 0, 0, 2, 1,11, 0, 0, 0, 6,
                           6, 0, 0, 4, 0,12, 0, 0, 0, 0, 2, 1,
                           0, 0, 0, 5, 0, 0, 0, 4,11,10, 0, 0,
                           0,10, 0, 1, 0, 0, 6, 0, 0, 0, 0, 0,
                           0, 7, 0, 0,11, 0, 0, 0, 0,12, 8, 9,
                           2, 1,11, 0, 0, 0, 0, 7, 0, 0, 6, 0,
                           0, 0, 0, 0, 0, 5, 0, 0, 4, 0,10, 0,
                           0, 0, 7, 3, 9, 0, 0, 0, 1, 0, 0, 0,
                           1, 5, 0, 0, 0, 0, 4, 0,10, 0, 0,11,
                           9, 0, 0, 0, 1,10, 2, 0, 0, 6, 0, 7,
                           0, 6,10, 0, 0, 0, 8, 0, 0, 1,12, 0}
                ,null, null);

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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_view, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent;

        if (id == R.id.nav_newgame) {
            //create new game
            intent = new Intent(this, GameActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_mainmenu) {
            //go to main menu
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            //open settings
            intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_highscore) {
            // see highscore list
            //intent = new Intent(this, HighscoreActivity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_about) {
            //open about page
            intent = new Intent(this,AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            //open about page
            //intent = new Intent(this,HelpActivity.class);
            //startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
