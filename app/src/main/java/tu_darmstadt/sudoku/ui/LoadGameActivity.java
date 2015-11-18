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
import android.widget.TextView;

import java.util.List;

import tu_darmstadt.sudoku.controller.SaveLoadController;
import tu_darmstadt.sudoku.controller.helper.GameInfoContainer;
import tu_darmstadt.sudoku.ui.view.R;

public class LoadGameActivity extends AppCompatActivity {

    List<GameInfoContainer> loadableGameList;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_game);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        SaveLoadController saveLoadController = new SaveLoadController(this, settings);
        loadableGameList = saveLoadController.loadGameStateInfo();

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

            TextView name = (TextView)convertView.findViewById(R.id.loadgame_listentry_gametype);
            TextView summary=(TextView)convertView.findViewById(R.id.loadgame_listentry_id);
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
            name.setText(gic.getGameType().name());
            summary.setText(String.valueOf(gic.getID()));

            return convertView;
        }
    }
}
