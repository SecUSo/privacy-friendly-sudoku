package tu_darmstadt.sudoku.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import tu_darmstadt.sudoku.ui.view.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void onClicktext(View view) {
        Intent i = new Intent(this, NewGameActivity.class);
        startActivity(i);
    }

}
