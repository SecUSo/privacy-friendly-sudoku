/*
 * qqwing - Sudoku solver and generator
 * Copyright (C) 2014 Stephen Ostermiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.secuso.privacyfriendlysudoku.ui;

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
import org.secuso.privacyfriendlysudoku.controller.NewLevelManager;
import org.secuso.privacyfriendlysudoku.controller.database.DatabaseHelper;
import org.secuso.privacyfriendlysudoku.controller.database.model.DailySudoku;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.qqwing.QQWing;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.view.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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

        sudokuList = dbHelper.getDailySudokus();
        TextView totalGamesTextView = findViewById(R.id.numb_of_total_games);
        TextView hintsTextView = findViewById(R.id.numb_of_hints);
        TextView totalTimeTextView = findViewById(R.id.numb_of_total_time);
        totalGamesTextView.setText(String.valueOf(sudokuList.size()));


        int sumHints = 0;
        int sumTime = 0;

        for (DailySudoku sudoku : sudokuList){
            sumHints += sudoku.getHintsUsed();
            sumTime += sudoku.getTimeNeededInSeconds();
        }

        int hours = sumTime / 3600;
        int minutes = ( sumTime / 60 ) % 60;
        int seconds = sumTime % 60;
        String str = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        hintsTextView.setText(String.valueOf(sumHints));
        totalTimeTextView.setText(str);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_daily_sudoku);
        actionBar.setDisplayHomeAsUpEnabled(true);

        difficultyBar = findViewById(R.id.first_diff_bar);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();


        ListView listView = (ListView)findViewById(R.id.sudoku_list);
        sudokuListAdapter = new DailySudokuActivity.SudokuListAdapter(this, sudokuList);
        listView.setAdapter(sudokuListAdapter);

        // Calculate the current date as an int id
        Calendar currentDate = Calendar.getInstance();
        dailyId = currentDate.get(Calendar.DAY_OF_MONTH) * 1000000
                + (currentDate.get(Calendar.MONTH) + 1) * 10000 + currentDate.get(Calendar.YEAR);

        GameDifficulty dailyDifficulty;

        //only calculate the difficulty of the daily sudoku once a day
        if (settings.getInt("lastCalculated", 0) != dailyId) {
            // generate the daily sudoku
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
            // if the daily sudoku has been calculated already, the difficulty can be read from the settings attribute
            int index = settings.getInt("dailyDifficultyIndex", GameDifficulty.getValidDifficultyList()
                    .indexOf(GameDifficulty.Unspecified));
            dailyDifficulty = GameDifficulty.getValidDifficultyList().get(index);
        }

        TextView diffTextView = findViewById(R.id.first_diff_text);

        diffTextView.setText(dailyDifficulty.getStringResID());
        difficultyBar.setNumStars(GameDifficulty.getValidDifficultyList().size());
        difficultyBar.setMax(GameDifficulty.getValidDifficultyList().size());
        difficultyBar.setRating(GameDifficulty.getValidDifficultyList().indexOf(dailyDifficulty)+1);


    }

    public void onClick(View view) {
        final Intent intent = new Intent(this,GameActivity.class);

        /*
         If the 'lastPlayed' key does not return the calculated id, then the player has not played
         the sudoku of the day yet, meaning it has yet to be saved on their phone and needs to be generated again
         */
        if (settings.getInt("lastPlayed", 0) != dailyId) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastPlayed", dailyId);

            //as the player just started a new daily sudoku, set the 'finishedForToday' setting to 'false'
            editor.putBoolean("finishedForToday", false);
            editor.apply();

            //send everything to game activity, which calculates the daily sudoku
            intent.putExtra("isDailySudoku", true);
            startActivity(intent);

        } else if (!settings.getBoolean("finishedForToday", true)) {
            /*
            if the 'finished for today' setting is 'false', the player has already started the sudoku
            but has yet to finish it -> send the designated daily sudoku ID to the GameActivity
             */
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