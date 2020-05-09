package org.secuso.privacyfriendlysudoku.ui.listener;

import org.secuso.privacyfriendlysudoku.ui.view.databinding.DialogFragmentShareBoardBinding;

/**
 * Created by Chris on 19.01.2016.
 */
public interface IShareDialogFragmentListener {
    public void onShareDialogPositiveClick(String input);
    public void onDialogNegativeClick();
}
