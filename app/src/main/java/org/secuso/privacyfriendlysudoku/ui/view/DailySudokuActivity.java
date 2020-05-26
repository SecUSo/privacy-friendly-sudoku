package org.secuso.privacyfriendlysudoku.ui.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import java.util.List;

public class DailySudokuActivity extends AppCompatActivity {

    List<GameInfoContainer> loadableGameList;
    SharedPreferences settings;


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
}