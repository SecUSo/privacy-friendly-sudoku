package org.secuso.privacyfriendlysudoku.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.NewLevelManager;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.view.DialogWinScreen;
import org.secuso.privacyfriendlysudoku.ui.view.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RatingBar difficultyBar;
    TextView difficultyText;
    SharedPreferences settings;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);

        // Is this the very first time we start this app?
        boolean firstStart = settings.getBoolean("firstStart", true);
        if(firstStart) {
            // preload some levels so we don't have to generate as many and we can start playing right away.
            newLevelManager.loadFirstStartLevels();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstStart", false);
            editor.commit();
        }

        // check if we need to pre generate levels.
        newLevelManager.checkAndRestock();

        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

            /*
          The {@link android.support.v4.view.PagerAdapter} that will provide
          fragments for each of the sections. We use a
          {@link FragmentPagerAdapter} derivative, which will keep every
          loaded fragment in memory. If this becomes too memory intensive, it
          may be best to switch to a
          {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.scroller);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set default gametype choice to whatever was chosen the last time.
        List<GameType> validGameTypes = GameType.getValidGameTypes();
        String lastChosenGameType = settings.getString("lastChosenGameType", GameType.Default_9x9.name());
        int index = validGameTypes.indexOf(Enum.valueOf(GameType.class, lastChosenGameType));
        mViewPager.setCurrentItem(index);

        // Set the difficulty Slider to whatever was chosen the last time
        difficultyBar = (RatingBar)findViewById(R.id.difficultyBar);
        difficultyText = (TextView) findViewById(R.id.difficultyText);
        final LinkedList<GameDifficulty> difficultyList = GameDifficulty.getValidDifficultyList();
        difficultyBar.setNumStars(difficultyList.size());
        difficultyBar.setMax(difficultyList.size());
        difficultyBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1) {
                    ratingBar.setRating(1);
                }
                difficultyText.setText(getString(difficultyList.get((int) ratingBar.getRating() - 1).getStringResID()));
            }
        });
        GameDifficulty lastChosenDifficulty = GameDifficulty.valueOf(settings.getString("lastChosenDifficulty", "Easy"));
        difficultyBar.setRating(GameDifficulty.getValidDifficultyList().indexOf(lastChosenDifficulty)+1);

        // on first create always check for loadable levels!
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("savesChanged", true);
        editor.apply();
        refreshContinueButton();


        // set Nav_Bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

    }
    public void callFragment(View view){
        /*FragmentManager fm = getSupportFragmentManager();
        DialogWinScreen winScreen = new DialogWinScreen();

        winScreen.show(fm,"win_screen_layout");*/

    }

    public void onClick(View view) {

        Intent i = null;

        switch(view.getId()) {
            case R.id.arrow_left:
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.arrow_right:
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
            case R.id.continueButton:
                i = new Intent(this, LoadGameActivity.class);
                break;
            case R.id.playButton:
                GameType gameType = GameType.getValidGameTypes().get(mViewPager.getCurrentItem());
                int index = difficultyBar.getProgress()-1;
                GameDifficulty gameDifficulty = GameDifficulty.getValidDifficultyList().get(index < 0 ? 0 : index);

                NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);
                if(newLevelManager.isLevelLoadable(gameType, gameDifficulty)) {
                    // save current setting for later
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("lastChosenGameType", gameType.name());
                    editor.putString("lastChosenDifficulty", gameDifficulty.name());
                    editor.apply();

                    // send everything to game activity
                    i = new Intent(this, GameActivity.class);
                    i.putExtra("gameType", (Serializable)gameType);
                    i.putExtra("gameDifficulty", (Serializable)gameDifficulty);
                } else {
                    newLevelManager.checkAndRestock();
                    Toast t = Toast.makeText(getApplicationContext(), R.string.generating, Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                break;
            default:
        }
        if(i != null) {
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshContinueButton();
    }

    private void refreshContinueButton() {
        // enable continue button if we have saved games.
        Button continueButton = (Button)findViewById(R.id.continueButton);
        GameStateManager fm = new GameStateManager(getBaseContext(), settings);
        List<GameInfoContainer> gic = fm.loadGameStateInfo();
        if(gic.size() > 0) {
            continueButton.setEnabled(true);
            continueButton.setBackgroundResource(R.drawable.standalone_button);
        } else {
            continueButton.setEnabled(false);
            continueButton.setBackgroundResource(R.drawable.inactive_button);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent;

        switch(id) {
            case R.id.menu_settings_main:
                //open settings
                intent = new Intent(this,SettingsActivity.class);
                intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GamePreferenceFragment.class.getName() );
                intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                break;

            case R.id.nav_highscore_main:
                // see highscore list

                intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_about_main:
                //open about page
                intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_help_main:
                //open about page
                //intent = new Intent(this,HelpActivity.class);
                //startActivity(intent);
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }*/


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GameTypeFragment (defined as a static inner class below).
            return GameTypeFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return GameType.getValidGameTypes().size();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GameTypeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GameTypeFragment newInstance(int sectionNumber) {
            GameTypeFragment fragment = new GameTypeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public GameTypeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

            GameType gameType = GameType.getValidGameTypes().get(getArguments().getInt(ARG_SECTION_NUMBER));

            ImageView imageView = (ImageView) rootView.findViewById(R.id.gameTypeImage);

            imageView.setImageResource(gameType.getResIDImage());


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(gameType.getStringResID()));
            return rootView;
        }
    }
}
