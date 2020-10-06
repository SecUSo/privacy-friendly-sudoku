/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.NewLevelManager;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.game.GameType;
import org.secuso.privacyfriendlysudoku.ui.listener.IImportDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.R;
import org.secuso.privacyfriendlysudoku.ui.view.databinding.DialogFragmentImportBoardBinding;

import java.util.LinkedList;
import java.util.List;

import static org.secuso.privacyfriendlysudoku.ui.TutorialActivity.ACTION_SHOW_ANYWAYS;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IImportDialogFragmentListener{

    RatingBar difficultyBar;
    TextView difficultyText;
    SharedPreferences settings;
    ImageView arrowLeft, arrowRight;
    DrawerLayout drawer;
    NavigationView mNavigationView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("pref_dark_mode_setting", false )) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else if (settings.getBoolean("pref_dark_mode_automatically_by_system", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        } else if(settings.getBoolean("pref_dark_mode_automatically_by_battery", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        NewLevelManager newLevelManager = NewLevelManager.getInstance(getApplicationContext(), settings);

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
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.scroller);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set default gametype choice to whatever was chosen the last time.
        List<GameType> validGameTypes = GameType.getValidGameTypes();
        String lastChosenGameType = settings.getString("lastChosenGameType", GameType.Default_9x9.name());
        int index = validGameTypes.indexOf(Enum.valueOf(GameType.class, lastChosenGameType));
        mViewPager.setCurrentItem(index);
        arrowLeft = (ImageView)findViewById(R.id.arrow_left);
        arrowRight = (ImageView) findViewById(R.id.arrow_right);

        //care for initial postiton of the ViewPager
        arrowLeft.setVisibility((index==0)?View.INVISIBLE:View.VISIBLE);
        arrowRight.setVisibility((index==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

        //Update ViewPager on change
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                arrowLeft.setVisibility((position==0)?View.INVISIBLE:View.VISIBLE);
                arrowRight.setVisibility((position==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        // Set the difficulty Slider to whatever was chosen the last time
        difficultyBar = (RatingBar)findViewById(R.id.difficultyBar);
        difficultyText = (TextView) findViewById(R.id.difficultyText);
        final LinkedList<GameDifficulty> difficultyList = GameDifficulty.getValidDifficultyList();
        difficultyBar.setNumStars(difficultyList.size());
        difficultyBar.setMax(difficultyList.size());
        CheckBox createGameBar = (CheckBox) findViewById(R.id.circleButton);
        createGameBar.setButtonDrawable(R.drawable.create_game_src);
        difficultyBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                createGameBar.setChecked(false);
                ((Button) findViewById(R.id.playButton)).setText(R.string.new_game);

                if (rating >= 1) {
                    difficultyText.setText(getString(difficultyList.get((int) ratingBar.getRating() - 1).getStringResID()));
                } else {
                    difficultyText.setText(R.string.difficulty_custom);
                    createGameBar.setChecked(true);
                    ((Button)findViewById(R.id.playButton)).setText(R.string.create_game);
                }
            }
        });

        createGameBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyBar.setRating(0);
                ((Button)findViewById(R.id.playButton)).setText(R.string.create_game);
                createGameBar.setChecked(true);
            }
        });

        String retrievedDifficulty = settings.getString("lastChosenDifficulty", "Moderate");
        GameDifficulty lastChosenDifficulty = GameDifficulty.valueOf(
                retrievedDifficulty.equals("Custom")? GameDifficulty.Unspecified.toString() : retrievedDifficulty);

        if (lastChosenDifficulty == GameDifficulty.Unspecified) {
            difficultyBar.setRating(0);
            createGameBar.setChecked(true);
        } else {
            difficultyBar.setRating(GameDifficulty.getValidDifficultyList().indexOf(lastChosenDifficulty) + 1);
        }
        /*LayerDrawable stars = (LayerDrawable)difficultyBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);//Color for Stars fully selected
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.middleblue), PorterDuff.Mode.SRC_ATOP);//Color for Stars partially selected
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightblue), PorterDuff.Mode.SRC_ATOP);//color for stars not selected
        */
        // on first create always check for loadable levels!
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("savesChanged", true);
        editor.apply();
        refreshContinueButton();


        // set Nav_Bar
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view_main);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(R.id.nav_newgame_main);

        overridePendingTransition(0, 0);
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
                if (((CheckBox)findViewById(R.id.circleButton)).isChecked()) {
                    // start CreateSudokuActivity
                    i = new Intent(this, CreateSudokuActivity.class);
                    i.putExtra("gameType", gameType.name());

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("lastChosenGameType", gameType.name());
                    editor.putString("lastChosenDifficulty", "Custom");
                    editor.apply();
                    //i.putExtra("gameDifficulty", GameDifficulty.Easy);
                    break;
                }
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
                    i.putExtra("gameType", gameType.name());
                    i.putExtra("gameDifficulty", gameDifficulty.name());
                } else {
                    newLevelManager.checkAndRestock();
                    Toast t = Toast.makeText(getApplicationContext(), R.string.generating, Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                break;
            default:
        }

        final Intent intent = i;

        if(intent != null) {

            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, MAIN_CONTENT_FADEOUT_DURATION);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        selectNavigationItem(R.id.nav_newgame_main);

        refreshContinueButton();
    }

    private void refreshContinueButton() {
        // enable continue button if we have saved games.
        Button continueButton = (Button)findViewById(R.id.continueButton);
        GameStateManager fm = new GameStateManager(getBaseContext(), settings);
        List<GameInfoContainer> gic = fm.loadGameStateInfo();
        if(gic.size() > 0 && !(gic.size() == 1 && gic.get(0).getID() == GameController.DAILY_SUDOKU_ID)) {
            continueButton.setEnabled(true);
            continueButton.setBackgroundResource(R.drawable.button_standalone);
        } else {
            continueButton.setEnabled(false);
            continueButton.setBackgroundResource(R.drawable.button_inactive);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        // return if we are not going to another page
        if(id == R.id.nav_newgame_main) {
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavigationItem(id);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        // fade out the active activity (but not if the user chose to open the ImportBoardDialog)
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null && id != R.id.nav_import_sudoku) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for(int i = 0 ; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    private boolean goToNavigationItem(int id) {
        Intent intent;

        switch(id) {
            case R.id.nav_import_sudoku:
                ImportBoardDialog dialog = new ImportBoardDialog();
                dialog.show(getFragmentManager(), "ImportDialogFragment");
                break;
            case R.id.menu_settings_main:
                //open settings
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_highscore_main:
                // see highscore list

                intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.menu_about_main:
                //open about page
                intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.menu_help_main:
                //open about page
                intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.menu_tutorial_main:
                intent = new Intent(this, TutorialActivity.class);
                intent.setAction(ACTION_SHOW_ANYWAYS);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_dailySudoku_main:
                intent = new Intent(this, DailySudokuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            default:
        }
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

    public void onImportDialogPositiveClick(String input) {
        String inputSudoku = null;
        String prefix = "";
        StringBuilder errorMessage = new StringBuilder();

        // a valid input needs to contain exactly one of the valid prefixes
        for (int i = 0; i < GameActivity.validUris.size(); i++) {
            prefix = GameActivity.validUris.get(i).getHost().equals("") ?
                    GameActivity.validUris.get(i).getScheme() + "://" :
                    GameActivity.validUris.get(i).getScheme() + "://" + GameActivity.validUris.get(i).getHost() + "/";
            if (input.startsWith(prefix)) {
                inputSudoku = input.replace(prefix, "");
                break;
            }

            String endOfRecord = i == GameActivity.validUris.size() - 1 ? "" : ", ";
            errorMessage.append(prefix);
            errorMessage.append(endOfRecord);
        }

        if (inputSudoku == null) {
            Toast.makeText(MainActivity.this,
                    this.getString(R.string.menu_import_wrong_format_custom_sudoku) + " " + errorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        double size = Math.sqrt(inputSudoku.length());
        boolean validSize = false;

        // check whether or not the size of the encoded sudoku is valid; if not, notify the user
        for (GameType type : GameType.getValidGameTypes()) {
            if (type.getSize() == size) {
                validSize = true;
                break;
            }
        }

        if (!validSize) {
            Toast.makeText(MainActivity.this, R.string.failed_to_verify_custom_sudoku_toast, Toast.LENGTH_LONG).show();
            return;
        }

        GameType gameType = Enum.valueOf(GameType.class, "Default_" + (int)size + "x" + (int)size);

        //check whether or not the sudoku is valid and has a unique solution
        boolean solvable = CreateSudokuActivity.verify(gameType, inputSudoku);

        // if the encoded sudoku is solvable, sent the code directly to the GameActivity; if not, notify the user
        if (solvable) {
            Toast.makeText(MainActivity.this, R.string.finished_verifying_custom_sudoku_toast, Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(this, GameActivity.class);
            intent.setData(Uri.parse(prefix + inputSudoku));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, R.string.failed_to_verify_custom_sudoku_toast, Toast.LENGTH_LONG).show();
        }
    }

    public void onDialogNegativeClick() {
        mNavigationView.setCheckedItem(R.id.nav_newgame_main);
    }

    public static class ImportBoardDialog extends DialogFragment {
        private LinkedList<IImportDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IImportDialogFragmentListener) {
                listeners.add((IImportDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            DialogFragmentImportBoardBinding binding = DialogFragmentImportBoardBinding.inflate(inflater);
            builder.setView(binding.getRoot());
            builder.setMessage(R.string.dialog_import_custom_sudoku);
            builder.setPositiveButton(R.string.dialog_import_custom_sudoku_positive_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    for(IImportDialogFragmentListener l : listeners) {
                        l.onImportDialogPositiveClick(binding.ver3ImportSudokuEditText.getText().toString());
                    }
                }
            })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IImportDialogFragmentListener l : listeners) {
                                l.onDialogNegativeClick();
                            }
                        }
                    });
            return builder.create();
        }

    }

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
