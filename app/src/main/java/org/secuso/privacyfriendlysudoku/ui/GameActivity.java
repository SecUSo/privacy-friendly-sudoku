package org.secuso.privacyfriendlysudoku.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.Symbol;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.game.listener.IGameSolvedListener;
import org.secuso.privacyfriendlysudoku.game.listener.ITimerListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IHintDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IResetDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IShareDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.R;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuFieldLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuKeyboardLayout;
import org.secuso.privacyfriendlysudoku.ui.view.SudokuSpecialButtonLayout;
import org.secuso.privacyfriendlysudoku.ui.view.WinDialog;
import org.secuso.privacyfriendlysudoku.ui.view.databinding.DialogFragmentShareBoardBinding;


import java.util.LinkedList;
import java.util.List;

public class GameActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IGameSolvedListener ,ITimerListener, IHintDialogFragmentListener, IResetDialogFragmentListener, IShareDialogFragmentListener {

    public static final String URL_SCHEME_WITHOUT_HOST = "sudoku";
    public static final String URL_SCHEME_WITH_HOST = "http";
    public static final String URL_HOST = "sudoku.secuso.org";

    GameController gameController;
    SudokuFieldLayout layout;
    SudokuKeyboardLayout keyboard;
    SudokuSpecialButtonLayout specialButtonLayout;
    TextView timerView;
    TextView viewName ;
    RatingBar ratingBar;
    private boolean gameSolved = false;
    private boolean startGame = true;
    SaveLoadStatistics statistics = new SaveLoadStatistics(this);
    WinDialog dialog = null;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(gameSolved) {
            gameController.pauseTimer();
        } else {
            // start the game
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameController.startTimer();
                }
            }, MAIN_CONTENT_FADEIN_DURATION);
        }
    }

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

        GameType gameType = GameType.Unspecified;
        GameDifficulty gameDifficulty = GameDifficulty.Unspecified;
        int loadLevelID = 0;
        boolean loadLevel = false;

        if(savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            Uri data = getIntent().getData();
            gameController = new GameController(sharedPref, getApplicationContext());

            boolean intentReceivedFromMainActivity = extras != null &&
                    (extras.containsKey("gameType") || extras.containsKey("loadLevel"));

            if (data != null && !intentReceivedFromMainActivity) {
                String input = "";
                if (data.getScheme().equals(URL_SCHEME_WITHOUT_HOST)){
                    input = data.getHost();
                } else if (data.getScheme().equals(URL_SCHEME_WITH_HOST) && data.getHost().equals(URL_HOST)){
                    input = data.getPath();
                    input =input.replace("/", "");
                }

                int sectionSize = (int)Math.sqrt(input.length());
                int boardSize = sectionSize * sectionSize;
                QQWing difficultyCheck;
                GameInfoContainer container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                        GameType.Unspecified, new int [boardSize], new int [boardSize], new boolean [boardSize][sectionSize]);
                container.setCustom(extras.getBoolean("isCustom", false));

                try {
                    container.parseGameType("Default_" + sectionSize + "x" + sectionSize);
                    container.parseFixedValues(input);
                    difficultyCheck = new QQWing(container.getGameType(), GameDifficulty.Unspecified);
                    difficultyCheck.setRecordHistory(true);
                    difficultyCheck.setPuzzle(container.getFixedValues());
                    difficultyCheck.solve();

                    container.parseDifficulty(difficultyCheck.getDifficulty().toString());
                    startGame = difficultyCheck.hasUniqueSolution();


                } catch (IllegalArgumentException e) {
                    startGame = false;
                    sectionSize = GameType.Default_9x9.getSize();
                    boardSize = sectionSize * sectionSize;
                    container = new GameInfoContainer(0, GameDifficulty.Unspecified,
                            GameType.Default_9x9, new int [boardSize], new int [boardSize], new boolean [boardSize][sectionSize]);
                }

                if (!startGame) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this, R.style.AppTheme_Dialog);
                    builder.setMessage(R.string.impossible_import_notice)
                            .setCancelable(false)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                gameController.loadLevel(container);

            } else {
                boolean isDailySudoku = false;
                if (extras != null) {
                    gameType = GameType.valueOf(extras.getString("gameType", GameType.Default_9x9.name()));
                    gameDifficulty = GameDifficulty.valueOf(extras.getString("gameDifficulty", GameDifficulty.Moderate.name()));
                    isDailySudoku = extras.getBoolean("isDailySudoku", false);
                    loadLevel = extras.getBoolean("loadLevel", false);
                    if (loadLevel) {
                        loadLevelID = extras.getInt("loadLevelID");
                    }
                }
                if (isDailySudoku) {
                    gameController.loadNewDailySudokuLevel();
                } else  {

                    List<GameInfoContainer> loadableGames = GameStateManager.getLoadableGameList();

                    if (loadLevel) {
                        if (loadableGames.size() > loadLevelID) {
                            // load level from GameStateManager
                            gameController.loadLevel(loadableGames.get(loadLevelID));
                        } else if (loadLevelID == GameController.DAILY_SUDOKU_ID) {
                            for (GameInfoContainer container : loadableGames) {
                                if (container.getID() == loadLevelID) {
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
            }
        } else {
            gameController = savedInstanceState.getParcelable("gameController");
            // in case we get the same object back
            // because parceling the Object does not always parcel it. Only if needed.
            if(gameController != null) {
                gameController.removeAllListeners();
                gameController.setContextAndSettings(getApplicationContext(), sharedPref);
            } else {
                // Error: no game could be restored. Go back to main menu.
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
            gameSolved = savedInstanceState.getInt("gameSolved") == 1;
        }


        setContentView(R.layout.activity_game_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.addView();

        if(gameSolved) {
            disableReset();
        }

        //Create new GameField
        layout = (SudokuFieldLayout)findViewById(R.id.sudokuLayout);
        gameController.registerGameSolvedListener(this);
        gameController.registerTimerListener(this);
        statistics.setGameController(gameController);

        layout.setSettingsAndGame(sharedPref, gameController);

        //set KeyBoard
        keyboard = (SudokuKeyboardLayout) findViewById(R.id.sudokuKeyboardLayout);
        keyboard.removeAllViews();
        keyboard.setGameController(gameController);
        //keyboard.setColumnCount((gameController.getSize() / 2) + 1);
        //keyboard.setRowCount(2);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        // set keyboard orientation
        int orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;

        keyboard.setKeyBoard(gameController.getSize(), p.x,layout.getHeight()-p.y, orientation);


        //set Special keys
        specialButtonLayout = (SudokuSpecialButtonLayout) findViewById(R.id.sudokuSpecialLayout);
        specialButtonLayout.setButtons(p.x, gameController, keyboard, getFragmentManager(), orientation, GameActivity.this);

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

        if(gameSolved) {
            layout.setEnabled(false);
            keyboard.setButtonsEnabled(false);
            specialButtonLayout.setButtonsEnabled(false);
        }

        gameController.notifyHighlightChangedListeners();
        gameController.notifyTimerListener(gameController.getTime());

        // run this so the error list gets build again.
        gameController.onModelChange(null);

        overridePendingTransition(0, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!gameSolved && startGame) {
            gameController.saveGame(this);
        }
        gameController.deleteTimer();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        gameController.initTimer();

        if(!gameSolved) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameController.startTimer();
                }
            }, MAIN_CONTENT_FADEIN_DURATION);
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

        Intent intent = null;

        switch(id) {
            case R.id.menu_reset:
                ResetConfirmationDialog resetDialog = new ResetConfirmationDialog();
                resetDialog.show(getFragmentManager(), "ResetDialogFragment");
                break;

            case R.id.menu_share:
                String codeForClipboard = URL_SCHEME_WITHOUT_HOST + "://" + gameController.getCodeOfField();
                String codeForClipboard1 = URL_SCHEME_WITH_HOST + "://" + URL_HOST + "/" + gameController.getCodeOfField();
                ShareBoardDialog shareDialog = new ShareBoardDialog();
                shareDialog.setDisplayCode(codeForClipboard);
                shareDialog.setCopyClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // remember to include alternate code for older android versions
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                        if (clipboard != null) {
                            ClipData clip = ClipData.newPlainText("BoardCode", codeForClipboard);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(GameActivity.this, R.string.copy_code_confirmation_toast,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GameActivity.this, R.string.copy_code_error_toast,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                shareDialog.show(getFragmentManager(), "ShareDialogFragment");

                break;

            case R.id.nav_newgame:
                //create new game
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;

            case R.id.menu_settings:
                //open settings
                intent = new Intent(this,SettingsActivity.class);
                intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GamePreferenceFragment.class.getName() );
                intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
                break;

            case R.id.nav_highscore:
                // see highscore list
                intent = new Intent(this, StatsActivity.class);
                break;

            case R.id.menu_about:
                //open about page
                intent = new Intent(this,AboutActivity.class);
                break;

            case R.id.menu_help:
                //open about page
                intent = new Intent(this,HelpActivity.class);
                break;
            default:
        }

        if(intent != null) {

            final Intent i = intent;
            // fade out the active activity
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

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
        disableReset();

        //Save solved sudoku, if it happens to be a daily sudoku, to daily sudoku database
        if(gameController.getGameID() == GameController.DAILY_SUDOKU_ID) {
            gameController.saveDailySudoku(GameActivity.this);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("finishedForToday", true);
            editor.apply();
        }

        //Don't save statistics if game is custom
        boolean isNewBestTime;

        if (!gameController.gameIsCustom()) {
            //Show time hints new plus old best time
            statistics.saveGameStats();
            isNewBestTime = gameController.getUsedHints() == 0
                    && statistics.loadStats(gameController.getGameType(),gameController.getDifficulty()).getMinTime() >= gameController.getTime();

        } else {
            isNewBestTime = false;
        }


        dialog = new WinDialog(this, R.style.WinDialog , timeToString(gameController.getTime()), String.valueOf(gameController.getUsedHints()), isNewBestTime);

        dialog.getWindow().setContentView(R.layout.win_screen_layout);
        //dialog.setContentView(getLayoutInflater().inflate(R.layout.win_screen_layout,null));
        //dialog.setContentView(R.layout.win_screen_layout);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        //((TextView)dialog.findViewById(R.id.win_hints)).setText(gameController.getUsedHints());
        //((TextView)dialog.findViewById(R.id.win_time)).setText(timeToString(gameController.getTime()));

        dialog.show();

        final Activity activity = this;
        ((Button)dialog.findViewById(R.id.win_continue_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                activity.finish();
            }
        });
        ((Button)dialog.findViewById(R.id.win_showGame_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        layout.setEnabled(false);
        keyboard.setButtonsEnabled(false);
        specialButtonLayout.setButtonsEnabled(false);
    }

    public static String timeToString(int time) {
        int seconds = time % 60;
        int minutes = ((time - seconds) / 60) % 60;
        int hours = (time - minutes - seconds) / (3600);
        String h, m, s;
        s = (seconds < 10) ? "0" + String.valueOf(seconds) : String.valueOf(seconds);
        m = (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);
        h = (hours < 10) ? "0" + String.valueOf(hours) : String.valueOf(hours);
        return h + ":" + m + ":" + s;
    }


    private void disableReset(){
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        Menu navMenu = navView.getMenu();
        navMenu.findItem(R.id.menu_reset).setEnabled(false);
    }
    @Override
    public void onTick(int time) {

        // display the time
        timerView.setText(timeToString(time));

        if(gameSolved || !startGame) return;
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
    public void onShareDialogPositiveClick(String input) {
        Intent sendBoardIntent = new Intent();
        sendBoardIntent.setAction(Intent.ACTION_SEND);
        sendBoardIntent.putExtra(Intent.EXTRA_TEXT, input);
        sendBoardIntent.setType("text/plain");

        Intent shareBoardIntent = Intent.createChooser(sendBoardIntent, null);
        startActivity(shareBoardIntent);
    }

    @Override
    public void onDialogNegativeClick() {
        // do nothing
    }

    public static class ShareBoardDialog extends DialogFragment {
        private LinkedList<IShareDialogFragmentListener> listeners = new LinkedList<>();

        /*declare empty display code and click listener in case anyone
         * tries to call the ShareBoardDialog without setting those attributes first
         */

        private String displayCode = "";
        private View.OnClickListener copyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        public void setDisplayCode(String displayCode) {
            this.displayCode = displayCode;
        }

        public void setCopyClickListener(View.OnClickListener listener) {
            copyClickListener = listener;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IShareDialogFragmentListener) {
                listeners.add((IShareDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            DialogFragmentShareBoardBinding binding = DialogFragmentShareBoardBinding.inflate(inflater);

            binding.ver3DisplaySudokuTextView.setText(displayCode);
            binding.ver3CopySudokuToClipboardButton.setOnClickListener(copyClickListener);
            builder.setView(binding.getRoot());

            builder.setPositiveButton(R.string.share_confirmation_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IShareDialogFragmentListener l : listeners) {
                                l.onShareDialogPositiveClick(binding.ver3DisplaySudokuTextView.getText().toString());
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

    public static class ResetConfirmationDialog extends DialogFragment {

        LinkedList<IResetDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IResetDialogFragmentListener) {
                listeners.add((IResetDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state

        savedInstanceState.putParcelable("gameController", gameController);
        savedInstanceState.putInt("gameSolved", gameSolved ? 1 : 0);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //super.onRestoreInstanceState(savedInstanceState);
    }

}
