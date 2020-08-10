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


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.secuso.privacyfriendlysudoku.ui.view.R;

import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * The SettingsActivity is extended by AppCompatPreferenceActivity
 * The activity is responsible for the different app settings.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        overridePendingTransition(0, 0);

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(BaseActivity.MAIN_CONTENT_FADEIN_DURATION);
        }
        /**
         * Set up the {@link android.app.ActionBar}, if the API is available.
         */

        SharedPreferences preferenceManager = PreferenceManager
                .getDefaultSharedPreferences(this);

       SharedPreferences.OnSharedPreferenceChangeListener x = new SharedPreferences.OnSharedPreferenceChangeListener() {

           /**
            * Case differentiation for the different Night Mode options
            * @param sharedPreferences
            */

           public void recheckNightModeProperties(SharedPreferences sharedPreferences){
               if (sharedPreferences.getBoolean("pref_dark_mode_setting", false )) {
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                   restartActivity();

               } else if (sharedPreferences.getBoolean("pref_dark_mode_automatically_by_system", false)) {
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                   restartActivity();
               } else if(sharedPreferences.getBoolean("pref_dark_mode_automatically_by_battery", false)){
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                   restartActivity();
               } else {
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                   restartActivity();
               }

           }
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("pref_dark_mode_setting")|| key.equals("pref_dark_mode_automatically_by_system")||key.equals("pref_dark_mode_automatically_by_battery")) {
                    recheckNightModeProperties(sharedPreferences);
                }
            }
        };
        preferenceManager.registerOnSharedPreferenceChangeListener(x);

    }

    /**
     * is responsible for closing and opening the activity
     */
    public void restartActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        recreate();
        finish();
        startActivity(i);
    }

    private void setupActionBar () {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                finish();
                //NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_settings_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GamePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GamePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings_general);
            setHasOptionsMenu(true);


            PreferenceManager preferenceManager = getPreferenceManager();
            SharedPreferences sp = preferenceManager.getSharedPreferences();

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (sp.getBoolean("pref_dark_mode_setting", false)) {
                    //TwoStatePreference x = (TwoStatePreference) findPreference("pref_dark_mode_automatically_by_system");
                    //x.setEnabled(false);
                    findPreference("pref_dark_mode_automatically_by_system").setEnabled(false);
                    findPreference("pref_dark_mode_automatically_by_battery").setEnabled(false);
                } else {
                    if (sp.getBoolean("pref_dark_mode_automatically_by_battery", false) && sp.getBoolean("pref_dark_mode_automatically_by_system", false) ) {
                        sp.edit().putBoolean("pref_dark_mode_automatically_by_battery", false).commit();
                    }

                    findPreference("pref_dark_mode_automatically_by_system").setEnabled(!sp.getBoolean("pref_dark_mode_automatically_by_battery", false));
                    findPreference("pref_dark_mode_automatically_by_battery").setEnabled(!sp.getBoolean("pref_dark_mode_automatically_by_system", false));
                }

                // Bind the summaries of EditText/List/Dialog/Ringtone preferences
                // to their values. When their values change, their summaries are
                // updated to reflect the new value, per the Android Design
                // guidelines.

                //bindPreferenceSummaryToValue(findPreference("example_text"));
                //bindPreferenceSummaryToValue(findPreference("example_list"));

            } 

            }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                //startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
