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
import org.secuso.privacyfriendlysudoku.ui.listener.IFinalizeDialogFragmentListener;
import org.secuso.privacyfriendlysudoku.ui.listener.IHintDialogFragmentListener;

import java.util.LinkedList;

import static org.secuso.privacyfriendlysudoku.ui.view.CreateSudokuButtonType.Spacer;
import static org.secuso.privacyfriendlysudoku.ui.view.CreateSudokuButtonType.getSpecialButtons;

public class CreateSudokuSpecialButtonLayout extends LinearLayout implements IHighlightChangedListener {

    IFinalizeDialogFragmentListener finalizeDialogFragmentListener;
    CreateSudokuSpecialButton[] fixedButtons;
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
            if(v instanceof CreateSudokuSpecialButton) {
                CreateSudokuSpecialButton btn = (CreateSudokuSpecialButton)v;

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
                    case Finalize:
                        FinalizeConfirmationDialog dialog = new FinalizeConfirmationDialog();
                        dialog.show(fragmentManager, "FinalizeDialogFragment");
                    default:
                        break;
                }
            }
        }
    };


    public CreateSudokuSpecialButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWeightSum(fixedButtonsCount);
        this.context = context;
    }

    public void setButtonsEnabled(boolean enabled) {
        for(CreateSudokuSpecialButton b : fixedButtons) {
            b.setEnabled(enabled);
        }
    }

    public void setButtons(int width, GameController gc, SudokuKeyboardLayout key, FragmentManager fm,
                           int orientation, Context cxt, IFinalizeDialogFragmentListener finalizeListener) {
        fragmentManager = fm;
        keyboard=key;
        gameController = gc;
        context = cxt;
        finalizeDialogFragmentListener = finalizeListener;
        if(gameController != null) {
            gameController.registerHighlightChangedListener(this);
        }
         fixedButtons = new CreateSudokuSpecialButton[fixedButtonsCount];
        LayoutParams p;
        int i = 0;
        //ArrayList<SudokuButtonType> type = (ArrayList<SudokuButtonType>) SudokuButtonType.getSpecialButtons();
        for (CreateSudokuButtonType t : getSpecialButtons()){
            fixedButtons[i] = new CreateSudokuSpecialButton(getContext(),null);
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

            if(fixedButtons[i].getVisibility() == View.VISIBLE) {
                Drawable drawable = ContextCompat.getDrawable(context, fixedButtons[i].getType().getResID());
                setUpVectorDrawable(drawable);
                drawable.draw(canvas);
                fixedButtons[i].setImageBitmap(bitResult);
            }
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
                    setUpVectorDrawable(drawable);

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

    private void setUpVectorDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        bitMap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitResult = Bitmap.createBitmap(bitMap.getWidth(), bitMap.getHeight(), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitResult);
    }

    public static class FinalizeConfirmationDialog extends DialogFragment {

        LinkedList<IFinalizeDialogFragmentListener> listeners = new LinkedList<>();

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            if(activity instanceof IFinalizeDialogFragmentListener) {
                listeners.add((IFinalizeDialogFragmentListener) activity);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            builder.setMessage(R.string.finalize_custom_sudoku_dialog)
                    .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(IFinalizeDialogFragmentListener l : listeners) {
                                l.onFinalizeDialogPositiveClick();
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
