package org.secuso.privacyfriendlysudoku.game.listener;

import org.secuso.privacyfriendlysudoku.game.CellConflict;

import java.util.List;

/**
 * Created by Chris on 02.02.2016.
 */
public interface IGameErrorListener {
    public void onGameFilledWithErrors(List<CellConflict> errorList);
}
