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

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import org.secuso.privacyfriendlysudoku.ui.SettingsActivity;
import org.secuso.privacyfriendlysudoku.ui.view.R;

public class SettingsActivity extends AppCompatActivity {


    private static SettingsFragment settingsFragment;

    private static void recheckNightModeProperties(SharedPreferences sharedPreferences) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (sharedPreferences.getBoolean("pref_dark_mode_setting", false)) {

                settingsFragment.findPreference("pref_dark_mode_automatically_by_system").setEnabled(false);
                settingsFragment.findPreference("pref_dark_mode_automatically_by_battery").setEnabled(false);
            } else {
                if (sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false) && sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false) ) {
                    sharedPreferences.edit().putBoolean("pref_dark_mode_automatically_by_battery", false).commit();
                }

                settingsFragment.findPreference("pref_dark_mode_automatically_by_system").setEnabled(!sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false));
                settingsFragment.findPreference("pref_dark_mode_automatically_by_battery").setEnabled(!sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false));
            }}

        if (sharedPreferences.getBoolean("pref_dark_mode_setting", false )) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else if (sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if(sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    static SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_dark_mode_setting")|| key.equals("pref_dark_mode_automatically_by_system")||key.equals("pref_dark_mode_automatically_by_battery")) {
                recheckNightModeProperties(sharedPreferences);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, settingsFragment)
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_settings_general, rootKey);
            SharedPreferences preferenceManager = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            preferenceManager.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            recheckNightModeProperties(preferenceManager);
        }
    }
}