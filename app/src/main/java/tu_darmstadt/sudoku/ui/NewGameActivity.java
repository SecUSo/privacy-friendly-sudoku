package tu_darmstadt.sudoku.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import tu_darmstadt.sudoku.controller.GameController;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.ui.view.R;

public class NewGameActivity extends AppCompatActivity {

    GameType gameType = GameType.Default_9x9;
    int gameDifficulty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
    }

    public void onPlayClick(View view) {

        // TODO get settings from GUI

        Intent i = new Intent(this, GameActivity.class);

        i.putExtra("gameType", gameType);
        i.putExtra("gameDifficulty", gameDifficulty);

        startActivity(i);
    }
}
