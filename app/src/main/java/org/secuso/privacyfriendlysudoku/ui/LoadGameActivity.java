/*
 * qqwing - Sudoku solver and generator
 * Copyright (C) 2014 Stephen Ostermiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.secuso.privacyfriendlysudoku.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.controller.GameStateManager;
import org.secuso.privacyfriendlysudoku.controller.NewLevelManager;
import org.secuso.privacyfriendlysudoku.controller.helper.GameInfoContainer;
import org.secuso.privacyfriendlysudoku.game.GameDifficulty;
import org.secuso.privacyfriendlysudoku.ui.listener.IDeleteDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.view.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class LoadGameActivity extends BaseActivity implements IDeleteDialogFragmentListener {

    List<GameInfoContainer> loadableGameList;
    SharedPreferences settings;
    LoadGameAdapter loadGameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_game);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.menu_continue_game);
        actionBar.setDisplayHomeAsUpEnabled(true);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        init();
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

    public void init() {

        GameStateManager gameStateManager = new GameStateManager(this, settings);
        loadableGameList = gameStateManager.loadGameStateInfo();

        for (GameInfoContainer container : loadableGameList) {
            if (container.getID() == GameController.DAILY_SUDOKU_ID) {
                loadableGameList.remove(container);
                break;
            }
        }

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
                DeleteDialogFragment deleteDialog = new DeleteDialogFragment();
                deleteDialog.setPosition(position);
                deleteDialog.show(getFragmentManager(), "DeleteDialogFragment");

                return true;
            }
        };

        ListView listView = (ListView)findViewById(R.id.main_content);
        loadGameAdapter = new LoadGameAdapter(this, loadableGameList);
        listView.setAdapter(loadGameAdapter);
        listView.setOnItemClickListener(clickListener);
        listView.setOnItemLongClickListener(longClickListener);

    }

    @Override
    public void onDialogPositiveClick(int position) {
        GameStateManager gameStateManager = new GameStateManager(getApplicationContext(), settings);
        gameStateManager.deleteGameStateFile(loadableGameList.get(position));
        loadGameAdapter.delete(position);
    }

    @Override
    public void onDialogNegativeClick(int position) {
        // do nothing
    }

    public static class DeleteDialogFragment extends DialogFragment {

        private int position = 0;

        public void setPosition(int position) {
            this.position = position;
        }
        public int getPosition() {
            return position;
        }

        LinkedList<IDeleteDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IDeleteDialogFragmentListener) {
                listeners.add((IDeleteDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            builder.setMessage(R.string.loadgame_delete_confirmation)
                    .setPositiveButton(R.string.loadgame_delete_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IDeleteDialogFragmentListener l : listeners) {
                                l.onDialogPositiveClick(getPosition());
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }


    private class LoadGameAdapter extends BaseAdapter {

        private Context context;
        private List<GameInfoContainer> loadableGameList;

        public LoadGameAdapter(Context context, List<GameInfoContainer> loadableGameList) {
            this.context = context;
            this.loadableGameList = loadableGameList;
        }

        public void delete(int position) {
            loadableGameList.remove(position);
            notifyDataSetChanged();
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
            difficultyBar.setNumStars(GameDifficulty.getValidDifficultyList().size());
            difficultyBar.setMax(GameDifficulty.getValidDifficultyList().size());
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
