package tu_darmstadt.sudoku.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import tu_darmstadt.sudoku.controller.SaveLoadStatistics;
import tu_darmstadt.sudoku.controller.helper.HighscoreInfoContainer;
import tu_darmstadt.sudoku.game.GameType;
import tu_darmstadt.sudoku.ui.view.R;

public class StatsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            SaveLoadStatistics.resetStats(this);
            mSectionsPagerAdapter.refresh(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        private FragmentManager fm;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return GameType.getValidGameTypes().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(GameType.getValidGameTypes().get(position).getStringResID());
        }
        public void refresh(Context context){
            for (Fragment f : fm.getFragments()){
                if(f instanceof PlaceholderFragment){
                    ((PlaceholderFragment) f).refresh(context);
                }
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private View rootView;
        private TextView difficultyView,averageTimeView,minTimeView;
        private RatingBar difficultyBarView;
        private String s;
        private int totalTime =0;
        private int totalGames =0;
        private int totalHints =0;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void refresh(Context context){
            resetGeneral();
            SaveLoadStatistics s = new SaveLoadStatistics(context);
            List<HighscoreInfoContainer> stats = s.loadStats(GameType.getValidGameTypes().get(getArguments().getInt(ARG_SECTION_NUMBER)));
            int j =0;
            for (HighscoreInfoContainer i : stats){
                updateGeneralIfo(i.getTime(), i.getNumberOfGames(), i.getNumberOfHintsUsed());
                setStats(i,j++);
            }
            setGeneralInfo();
        }

        private void resetGeneral(){
            totalTime=0;
            totalHints=0;
            totalGames=0;
        }

        public PlaceholderFragment() {
        }


        private String formatTime(int totalTime){
            int seconds = totalTime % 60;
            int minutes = ((totalTime -seconds)/60)%60 ;
            int hours = (totalTime - minutes - seconds)/(3600);
            String h,m,s;
            s = (seconds< 10)? "0"+String.valueOf(seconds):String.valueOf(seconds);
            m = (minutes< 10)? "0"+String.valueOf(minutes):String.valueOf(minutes);
            h = (hours< 10)? "0"+String.valueOf(hours):String.valueOf(hours);
            return (h + ":" + m + ":" + s);

        }
        private void updateGeneralIfo(int time, int games, int hints){
            totalHints +=hints;
            totalGames +=games;
            totalTime +=time;
        }
        private void setGeneralInfo(){
            TextView generalInfoView;

            generalInfoView = (TextView)rootView.findViewById(R.id.numb_of_hints);
            generalInfoView.setText(String.valueOf(totalHints));
            generalInfoView = (TextView)rootView.findViewById(R.id.numb_of_total_games);
            generalInfoView.setText(String.valueOf(totalGames));
            generalInfoView = (TextView)rootView.findViewById(R.id.numb_of_total_time);
            generalInfoView.setText(formatTime(totalTime));

        }

        private void setStats(HighscoreInfoContainer infos, int pos){

            switch (pos) {
                case 0 :
                    difficultyBarView = (RatingBar) rootView.findViewById(R.id.first_diff_bar);
                    difficultyView = (TextView) rootView.findViewById(R.id.first_diff_text);
                    averageTimeView = (TextView) rootView.findViewById(R.id.first_ava_time);
                    minTimeView = (TextView) rootView.findViewById(R.id.first_min_time);
                    break;
                case 1:
                    difficultyBarView = (RatingBar) rootView.findViewById(R.id.second_diff_bar);
                    difficultyView = (TextView) rootView.findViewById(R.id.second_diff_text);
                    averageTimeView = (TextView) rootView.findViewById(R.id.second_ava_time);
                    minTimeView = (TextView) rootView.findViewById(R.id.second_min_time);
                    break;
                case 2:
                    difficultyBarView = (RatingBar) rootView.findViewById(R.id.third_diff_bar);
                    difficultyView = (TextView) rootView.findViewById(R.id.third_diff_text);
                    averageTimeView = (TextView) rootView.findViewById(R.id.third_ava_time);
                    minTimeView = (TextView) rootView.findViewById(R.id.third_min_time);
                    break;
                default: return;
            }
            difficultyBarView.setRating(pos+1);
            difficultyView.setText(rootView.getResources().getString(infos.getDifficulty().getStringResID()));
            s= (infos.getTime() == 0)?"/":String.valueOf(infos.getTime() / infos.getNumberOfGames());
            averageTimeView.setText(s);
            s = (infos.getMinTime()==Integer.MAX_VALUE)? "/":String.valueOf(infos.getMinTime());
            minTimeView.setText(s);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
            this.rootView = rootView;
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            SaveLoadStatistics s = new SaveLoadStatistics(this.getContext());
            List<HighscoreInfoContainer> stats = s.loadStats(GameType.getValidGameTypes().get(getArguments().getInt(ARG_SECTION_NUMBER)));


            int j =0;
            for (HighscoreInfoContainer i : stats){
                updateGeneralIfo(i.getTime(), i.getNumberOfGames(), i.getNumberOfHintsUsed());
                setStats(i,j++);
            }
            setGeneralInfo();

            ImageView imageView = (ImageView) rootView.findViewById(R.id.statistic_image);
            imageView.setImageResource(GameType.getValidGameTypes().get(getArguments().getInt(ARG_SECTION_NUMBER)).getResIDImage());

            return rootView;
        }
    }
}
