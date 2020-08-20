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


    private static PreferenceScreen prefScreen;

    private static void recheckNightModeProperties(SharedPreferences sharedPreferences) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (sharedPreferences.getBoolean("pref_dark_mode_setting", false)) {
                prefScreen.findPreference("pref_dark_mode_automatically_by_system").setEnabled(false);
                prefScreen.findPreference("pref_dark_mode_automatically_by_battery").setEnabled(false);
            } else {
                if (sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false) && sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false) ) {
                    sharedPreferences.edit().putBoolean("pref_dark_mode_automatically_by_battery", false).commit();
                }

                prefScreen.findPreference("pref_dark_mode_automatically_by_system").setEnabled(!sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false));
                prefScreen.findPreference("pref_dark_mode_automatically_by_battery").setEnabled(!sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false));
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

    static SharedPreferences.OnSharedPreferenceChangeListener x = new SharedPreferences.OnSharedPreferenceChangeListener() {

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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences preferenceManager = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferenceManager.registerOnSharedPreferenceChangeListener(x);

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
            prefScreen = getPreferenceScreen();
            SharedPreferences preferenceManager = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            recheckNightModeProperties(preferenceManager);

        }
    }
}