package org.secuso.privacyfriendlysudoku.game.listener;

import org.secuso.privacyfriendlysudoku.game.GameCell;

/**
 * Created by Chris on 19.11.2015.
 */
public interface IModelChangedListener {
    public void onModelChange(GameCell c);
}
