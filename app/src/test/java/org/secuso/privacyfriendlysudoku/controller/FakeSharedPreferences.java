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
package org.secuso.privacyfriendlysudoku.controller;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * In-memory {@link SharedPreferences} for local unit tests, so that settings
 * dependent behaviour of the {@link GameController} can be tested without an
 * Android runtime. Only the getters are supported; everything that would
 * require a real preference file throws.
 */
public class FakeSharedPreferences implements SharedPreferences {

    private final Map<String, Object> values = new HashMap<>();

    public FakeSharedPreferences put(String key, Object value) {
        values.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String key, T defValue) {
        return values.containsKey(key) ? (T) values.get(key) : defValue;
    }

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>(values);
    }

    @Override
    public String getString(String key, String defValue) {
        return get(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return get(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return get(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return get(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return get(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return get(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public Editor edit() {
        throw new UnsupportedOperationException("FakeSharedPreferences is read only.");
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // no listeners in tests
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // no listeners in tests
    }
}
