package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;
import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.NewLevelManager;
import org.secuso.privacyfriendlysudoku.controller.database.DatabaseHelper;
import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.GameActivity;
import org.secuso.privacyfriendlysudoku.ui.SettingsActivity;
import org.secuso.privacyfriendlysudoku.ui.StatsActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DailySudokuActivity<Database> extends AppCompatActivity {

    List<DailySudoku> sudokuList;
    SharedPreferences settings;
    private final DatabaseHelper dbHelper = new DatabaseHelper(this);
    RatingBar difficultyBar;
    static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    private Handler mHandler;
    private StatsActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private SudokuListAdapter sudokuListAdapter;
    private int dailyId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_sudoku);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Calculate the current date as an int id
        Calendar currentDate = Calendar.getInstance();
        dailyId = currentDate.get(Calendar.DAY_OF_MONTH) * 1000000
                + (currentDate.get(Calendar.MONTH) + 1) * 10000 + currentDate.get(Calendar.YEAR);

        //TODO: rename tw/tx/ty
        sudokuList = dbHelper.getDailySudokus();
        TextView tw = findViewById(R.id.numb_of_total_games);
        TextView tx = findViewById(R.id.numb_of_hints);
        TextView ty = findViewById(R.id.numb_of_total_time);
        tw.setText(String.valueOf(sudokuList.size()));


        int sumHints = 0;
        int sumTime = 0;


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        for (DailySudoku sudoku : sudokuList){
            sumHints += sudoku.getHintsUsed();
            //sumTime += sudoku.getTimeNeededInSeconds();

            String[] split = sudoku.getTimeNeeded().split(":");
            sumTime += Integer.valueOf(split[0])*3600 + Integer.valueOf(split[1])*60 + Integer.valueOf(split[2]);
        }

        int hours = sumTime / 3600;
        int minutes = ( sumTime / 60 ) % 60;
        int seconds = sumTime % 60;
        String str = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        tx.setText(String.valueOf(sumHints));
        ty.setText(str);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Daily Sudoku");
        actionBar.setDisplayHomeAsUpEnabled(true);

        difficultyBar = findViewById(R.id.first_diff_bar);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();
        

        ListView listView = (ListView)findViewById(R.id.sudoku_list);
        sudokuListAdapter = new DailySudokuActivity.SudokuListAdapter(this, sudokuList);
        listView.setAdapter(sudokuListAdapter);

        GameDifficulty dailyDifficulty = GameDifficulty.Unspecified;

        //only calculate the difficulty of the daily sudoku once a day
        if (settings.getInt("lastCalculated", 0) != dailyId) {
            NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);

            int[] level = newLevelManager.loadDailySudoku();

            QQWing difficultyCheck = new QQWing(GameType.Default_9x9, GameDifficulty.Unspecified);
            difficultyCheck.setRecordHistory(true);
            difficultyCheck.setPuzzle(level);
            difficultyCheck.solve();
            dailyDifficulty = difficultyCheck.getDifficulty();

            //save the index of the daily difficulty (in the valid difficulty list) and the day it was calculated for
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastCalculated", dailyId);
            editor.putInt("dailyDifficultyIndex", GameDifficulty.getValidDifficultyList().indexOf(dailyDifficulty));
            editor.apply();

        } else {
            int index = settings.getInt("dailyDifficultyIndex", GameDifficulty.getValidDifficultyList()
                    .indexOf(GameDifficulty.Unspecified));
            dailyDifficulty = GameDifficulty.getValidDifficultyList().get(index);
        }
        RatingBar ratingbar = findViewById(R.id.first_diff_bar);
        TextView tz = findViewById(R.id.first_diff_text);

        tz.setText(dailyDifficulty.getStringResID());
        ratingbar.setNumStars(GameDifficulty.getValidDifficultyList().size());
        ratingbar.setMax(GameDifficulty.getValidDifficultyList().size());
        ratingbar.setRating(GameDifficulty.getValidDifficultyList().indexOf(dailyDifficulty)+1);


    }

    public void onClick(View view) {
        final Intent intent = new Intent(this,GameActivity.class);

        /*
         If the 'lastPlayed' key does not return the calculated id, then the player has not played
         the sudoku of the day yet, meaning it has yet to be generated
         */
        if (settings.getInt("lastPlayed", 0) != dailyId) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastPlayed", dailyId);
            editor.putBoolean("finishedForToday", false);
            editor.apply();

            //send everything to game activity
            intent.putExtra("isDailySudoku", true);
            startActivity(intent);

        } else if (!settings.getBoolean("finishedForToday", true)) {
            intent.putExtra("loadLevel", true);
            intent.putExtra("loadLevelID", GameController.DAILY_SUDOKU_ID);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.finished_daily_sudoku, Toast.LENGTH_LONG).show();
        }
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        //getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
        //return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()) {
            case R.id.action_reset:
                SaveLoadStatistics.resetStats(this);
                mSectionsPagerAdapter.refresh(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SudokuListAdapter extends BaseAdapter {

        private Context context;
        private List<DailySudoku> sudokuList;

        public SudokuListAdapter(Context context, List<DailySudoku> sudokuList) {
            this.context = context;
            this.sudokuList = sudokuList;
        }

        @Override
        public int getCount() {
            return sudokuList.size();
        }

        @Override
        public Object getItem(int position) {
            return sudokuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DailySudoku sudoku = sudokuList.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = (View) inflater.inflate(R.layout.list_entry_layout, null);
            }

            TextView gameType = (TextView)convertView.findViewById(R.id.loadgame_listentry_gametype);
            TextView difficulty =(TextView)convertView.findViewById(R.id.loadgame_listentry_difficultytext);
            RatingBar difficultyBar =(RatingBar)convertView.findViewById(R.id.loadgame_listentry_difficultybar);
            TextView playedTime = (TextView)convertView.findViewById(R.id.loadgame_listentry_timeplayed);
            TextView lastTimePlayed = (TextView)convertView.findViewById(R.id.loadgame_listentry_lasttimeplayed);
            ImageView image = (ImageView)convertView.findViewById(R.id.loadgame_listentry_gametypeimage);


            image.setImageResource(R.drawable.icon_default_9x9);

            gameType.setText(sudoku.getGameType().getStringResID());
            difficulty.setText(sudoku.getDifficulty().getStringResID());
            difficultyBar.setNumStars(GameDifficulty.getValidDifficultyList().size());
            difficultyBar.setMax(GameDifficulty.getValidDifficultyList().size());
            difficultyBar.setRating(GameDifficulty.getValidDifficultyList().indexOf(sudoku.getDifficulty())+1);


            Calendar currentDate = Calendar.getInstance();
            //int id = currentDate.get(Calendar.DAY_OF_MONTH) * 1000000
              //      + (currentDate.get(Calendar.MONTH) + 1) * 10000 + currentDate.get(Calendar.YEAR);

            int id = sudoku.getId();
            int dayOfMonth = id/1000000;
            int month = (id/10000) % 100;
            int year = id%10000;

            String str = String.format("%02d.%02d.%02d", dayOfMonth, month, year);

            lastTimePlayed.setText(str);
            playedTime.setText(sudoku.getTimeNeeded());
            return convertView;
        }
    }
}