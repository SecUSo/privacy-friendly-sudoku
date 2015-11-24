package tu_darmstadt.sudoku.ui;

import android.app.DialogFragment;

/**
 * Created by Chris on 24.11.2015.
 */
public interface IDeleteDialogFragmentListener {
    public void onDialogPositiveClick(int position);
    public void onDialogNegativeClick(int position);
}
