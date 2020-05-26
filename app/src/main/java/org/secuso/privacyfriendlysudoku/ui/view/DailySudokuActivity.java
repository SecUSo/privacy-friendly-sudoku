package org.secuso.privacyfriendlysudoku.ui.view;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.secuso.privacyfriendlysudoku.controller.SaveLoadStatistics;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.ui.StatsActivity;
import java.util.List;

public class DailySudokuActivity extends AppCompatActivity {

    List<GameInfoContainer> loadableGameList;
    SharedPreferences settings;
    private StatsActivity.SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_sudoku);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Daily Sudoku");
        actionBar.setDisplayHomeAsUpEnabled(true);



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

}