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

package org.secuso.privacyfriendlysudoku.game;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.StringRes;

import java.util.LinkedList;

import org.secuso.privacyfriendlysudoku.ui.view.R;

/**
 * Created by Chris on 18.11.2015.
 */
public enum GameDifficulty implements Parcelable {

    Unspecified(R.string.gametype_unspecified),
    Easy(R.string.difficulty_easy),
    Moderate(R.string.difficulty_moderate),
    Hard(R.string.difficulty_hard),
    Challenge(R.string.difficulty_challenge);

    private int resID;

    GameDifficulty(@StringRes int resID) {
        //getResources().getString(resID);
        this.resID = resID;
    }

    public int getStringResID() {
        return resID;
    }

    public static LinkedList<GameDifficulty> getValidDifficultyList() {
        LinkedList<GameDifficulty> validList = new LinkedList<>();
        validList.add(Easy);
        validList.add(Moderate);
        validList.add(Hard);
        validList.add(Challenge);
        return validList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
        dest.writeInt(resID);
    }

    public static final Parcelable.Creator<GameDifficulty> CREATOR
            = new Parcelable.Creator<GameDifficulty>() {
        public GameDifficulty createFromParcel(Parcel in) {
            GameDifficulty g = GameDifficulty.values()[in.readInt()];
            g.resID = in.readInt();
            return g;
        }

        public GameDifficulty[] newArray(int size) {
            return new GameDifficulty[size];
        }
    };

}
