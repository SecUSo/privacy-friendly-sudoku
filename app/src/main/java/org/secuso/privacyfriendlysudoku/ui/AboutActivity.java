package org.secuso.privacyfriendlysudoku.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import org.secuso.privacyfriendlysudoku.ui.view.BuildConfig;
import org.secuso.privacyfriendlysudoku.ui.view.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView)findViewById(R.id.secusoWebsite)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.githubURL)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.textFieldVersionName)).setText(BuildConfig.VERSION_NAME);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_about);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#024265")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
