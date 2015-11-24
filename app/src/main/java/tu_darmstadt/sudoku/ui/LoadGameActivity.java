package tu_darmstadt.sudoku.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import tu_darmstadt.sudoku.controller.SaveLoadGameStateController;
import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;
import tu_darmstadt.sudoku.game.GameDifficulty;
import tu_darmstadt.sudoku.ui.view.R;

public class LoadGameActivity extends AppCompatActivity {

    List<GameInfoContainer> loadableGameList;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_game);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        SaveLoadGameStateController saveLoadGameStateController = new SaveLoadGameStateController(this, settings);
        loadableGameList = saveLoadGameStateController.loadGameStateInfo();

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(parent.getContext(), GameActivity.class);
                i.putExtra("loadLevel", true);
                i.putExtra("loadLevelID", position);
                finish();
                startActivity(i);
            }
        };

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        };

        ListView listView = (ListView)findViewById(R.id.load_game_list);
        listView.setAdapter(new LoadGameAdapter(this, loadableGameList));
        listView.setOnItemClickListener(clickListener);
        listView.setOnItemLongClickListener(longClickListener);

    }



    private class LoadGameAdapter extends BaseAdapter {

        private Context context;
        private List<GameInfoContainer> loadableGameList;

        public LoadGameAdapter(Context context, List<GameInfoContainer> loadableGameList) {
            this.context = context;
            this.loadableGameList = loadableGameList;
        }

        @Override
        public int getCount() {
            return loadableGameList.size();
        }

        @Override
        public Object getItem(int position) {
            return loadableGameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return loadableGameList.get(position).getID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = (View) inflater.inflate(R.layout.list_entry_layout, null);
            }

            GameInfoContainer gic = loadableGameList.get(position);

            TextView gameType = (TextView)convertView.findViewById(R.id.loadgame_listentry_gametype);
            TextView difficulty =(TextView)convertView.findViewById(R.id.loadgame_listentry_difficultytext);
            RatingBar difficultyBar =(RatingBar)convertView.findViewById(R.id.loadgame_listentry_difficultybar);
            TextView playedTime = (TextView)convertView.findViewById(R.id.loadgame_listentry_timeplayed);
            TextView lastTimePlayed = (TextView)convertView.findViewById(R.id.loadgame_listentry_lasttimeplayed);
            ImageView image = (ImageView)convertView.findViewById(R.id.loadgame_listentry_gametypeimage);

            switch(gic.getGameType()) {
                case Default_6x6:
                    image.setImageResource(R.drawable.icon_default_6x6);
                    break;
                case Default_12x12:
                    image.setImageResource(R.drawable.icon_default_12x12);
                    break;
                case Default_9x9:
                    image.setImageResource(R.drawable.icon_default_9x9);
                    break;
                default:
                    image.setImageResource(R.drawable.icon_default_9x9);
            }
            gameType.setText(gic.getGameType().getStringResID());
            difficulty.setText(gic.getDifficulty().getStringResID());
            difficultyBar.setRating(GameDifficulty.getValidDifficultyList().indexOf(gic.getDifficulty())+1);

            int time = gic.getTimePlayed();
            int seconds = time % 60;
            int minutes = ((time -seconds)/60)%60 ;
            int hours = (time - minutes - seconds)/(3600);
            String h,m,s;
            s = (seconds< 10)? "0"+String.valueOf(seconds):String.valueOf(seconds);
            m = (minutes< 10)? "0"+String.valueOf(minutes):String.valueOf(minutes);
            h = (hours< 10)? "0"+String.valueOf(hours):String.valueOf(hours);
            playedTime.setText(h + ":" + m + ":" + s);

            Date lastTimePlayedDate = gic.getLastTimePlayed();

            DateFormat format = DateFormat.getDateTimeInstance();
            format.setTimeZone(TimeZone.getDefault());

            lastTimePlayed.setText(format.format(lastTimePlayedDate));

            return convertView;
        }
    }
}
