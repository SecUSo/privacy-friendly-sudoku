package org.secuso.privacyfriendlysudoku.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.game.listener.IHighlightChangedListener;
import org.secuso.privacyfriendlysudoku.ui.GameActivity;
import org.secuso.privacyfriendlysudoku.ui.listener.IHintDialogFragmentListener;

import java.util.LinkedList;

import static org.secuso.privacyfriendlysudoku.ui.view.SudokuButtonType.Spacer;
import static org.secuso.privacyfriendlysudoku.ui.view.SudokuButtonType.getSpecialButtons;

/**
 * Created by TMZ_LToP on 17.11.2015.
 */
public class SudokuSpecialButtonLayout extends LinearLayout implements IHighlightChangedListener {


    SudokuSpecialButton[] fixedButtons;
    public int fixedButtonsCount = getSpecialButtons().size();
    GameController gameController;
    SudokuKeyboardLayout keyboard;
    Bitmap bitMap,bitResult;
    Canvas canvas;
    FragmentManager fragmentManager;
    Context context;

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuSpecialButton) {
                SudokuSpecialButton btn = (SudokuSpecialButton)v;

                //int row = gameController.getSelectedRow();
                //int col = gameController.getSelectedCol();

                switch(btn.getType()) {
                    case Delete:
                        gameController.deleteSelectedCellsValue();
                        break;
                    case NoteToggle:
                        // rotates the Drawable
                        gameController.setNoteStatus(!gameController.getNoteStatus());
                        keyboard.updateNotesEnabled();
                        onHighlightChanged();
                        break;
                    case Do:
                        gameController.ReDo();
                        break;
                    case Undo:
                        gameController.UnDo();
                        break;
                    case Hint:
                        if(gameController.isValidCellSelected()) {
                            if(gameController.getUsedHints() == 0) {
                                // are you sure you want to use a hint?
                                HintConfirmationDialog hintDialog = new HintConfirmationDialog();
                                hintDialog.show(fragmentManager, "HintDialogFragment");

                            } else {
                                gameController.hint();
                            }
                        } else {
                            // Display a Toast that explains how to use the Hint function.
                            Toast t = Toast.makeText(getContext(), R.string.hint_usage, Toast.LENGTH_SHORT);
                            t.show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };


    public SudokuSpecialButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWeightSum(fixedButtonsCount);
    }

    public void setButtonsEnabled(boolean enabled) {
        for(SudokuSpecialButton b : fixedButtons) {
            b.setEnabled(enabled);
        }
    }

    public void setButtons(int width, GameController gc, SudokuKeyboardLayout key, FragmentManager fm, int orientation, Context cxt) {
        fragmentManager = fm;
        keyboard=key;
        gameController = gc;
        context = cxt;
        if(gameController != null) {
            gameController.registerHighlightChangedListener(this);
        }
        fixedButtons = new SudokuSpecialButton[fixedButtonsCount];
        LayoutParams p;
        int i = 0;
        //ArrayList<SudokuButtonType> type = (ArrayList<SudokuButtonType>) SudokuButtonType.getSpecialButtons();
        for (SudokuButtonType t : getSpecialButtons()){
            fixedButtons[i] = new SudokuSpecialButton(getContext(),null);
            if(orientation == LinearLayout.HORIZONTAL) {
                p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            } else {
                p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                fixedButtons[i].setPadding(25, 0, 25, 0);
            }
            p.setMargins(5, 5, 5, 5);

            //int width2 =width/(fixedButtonsCount);
            //p.width= width2-15;
            if(t == Spacer) {
                fixedButtons[i].setVisibility(View.INVISIBLE);
            }
            /*if(t == SudokuButtonType.Do && !gameController.isRedoAvailable()) {
                fixedButtons[i].setEnabled(false);
            }
            if(t == SudokuButtonType.Undo && !gameController.isUndoAvailable()) {
                fixedButtons[i].setEnabled(false);
            }*/
            fixedButtons[i].setLayoutParams(p);
            fixedButtons[i].setType(t);
            fixedButtons[i].setImageDrawable(getResources().getDrawable(t.getResID()));
            // fixedButtons[i].setText(SudokuButtonType.getName(t));
            fixedButtons[i].setScaleType(ImageView.ScaleType.CENTER);
            fixedButtons[i].setAdjustViewBounds(true);
            fixedButtons[i].setOnClickListener(listener);
            fixedButtons[i].setBackgroundResource(R.drawable.numpad_highlighted_four);
            addView(fixedButtons[i]);
            i++;
        }

    }

    @Override
    public void onHighlightChanged() {
        for(int i = 0; i < fixedButtons.length; i++) {
            switch(fixedButtons[i].getType()) {
                case Undo:
                    fixedButtons[i].setBackgroundResource(gameController.isUndoAvailable() ?
                            R.drawable.numpad_highlighted_four : R.drawable.inactive_button);
                    break;
                case Do:
                    fixedButtons[i].setBackgroundResource(gameController.isRedoAvailable() ?
                            R.drawable.numpad_highlighted_four : R.drawable.inactive_button);
                    break;
                case NoteToggle:
                    Drawable drawable = ContextCompat.getDrawable(context, fixedButtons[i].getType().getResID());
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    bitMap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    bitResult = Bitmap.createBitmap(bitMap.getWidth(), bitMap.getHeight(), Bitmap.Config.ARGB_8888);

                    canvas = new Canvas(bitResult);
                    canvas.rotate(gameController.getNoteStatus() ? 45.0f : 0.0f, bitMap.getWidth()/2, bitMap.getHeight()/2);
                    canvas.drawBitmap(bitMap, 0, 0, null);
                    drawable.draw(canvas);

                    fixedButtons[i].setImageBitmap(bitResult);
                    fixedButtons[i].setBackgroundResource(gameController.getNoteStatus() ? R.drawable.numpad_highlighted_three : R.drawable.numpad_highlighted_four);

                    keyboard.updateNotesEnabled();

                    break;
                default:
                    break;
            }
        }
    }

    public static class HintConfirmationDialog extends DialogFragment {

        LinkedList<IHintDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IHintDialogFragmentListener) {
                listeners.add((IHintDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.hint_confirmation)
                    .setPositiveButton(R.string.hint_confirmation_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IHintDialogFragmentListener l : listeners) {
                                l.onHintDialogPositiveClick();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }
}
