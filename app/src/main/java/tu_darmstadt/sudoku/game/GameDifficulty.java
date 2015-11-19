package tu_darmstadt.sudoku.game;

import android.support.annotation.StringRes;

import java.util.LinkedList;

import tu_darmstadt.sudoku.ui.view.R;

/**
 * Created by Chris on 18.11.2015.
 */
public enum GameDifficulty {

    Easy(R.string.difficulty_easy),
    Moderate(R.string.difficulty_moderate),
    Hard(R.string.difficulty_hard);

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
        return validList;
    }

}
