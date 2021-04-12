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
package org.secuso.privacyfriendlysudoku.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlysudoku.ui.MainActivity;

/**
 * Created by TMZ_LToP on 30.01.2016.
 */
public class WinDialog extends DialogFragment {

    public static final String ARG_TIME = "WinDialog.ARG_TIME";
    public static final String ARG_HINT = "WinDialog.ARG_HINT";
    public static final String ARG_BEST = "WinDialog.ARG_BEST";

    private String timeString = "";
    private String hintString = "";
    private boolean isNewBestTime = false;

    public WinDialog() {
        super();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        setParam(args.getString(ARG_TIME), args.getString(ARG_HINT), args.getBoolean(ARG_BEST));
    }

    public void setParam(String timeString, String hintString, boolean isNewBestTime){
        this.timeString = timeString;
        this.hintString = hintString;
        this.isNewBestTime = isNewBestTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saved) {
        Dialog dialog = new Dialog(getActivity(), R.style.WinDialog);

        dialog.getWindow().setContentView(R.layout.win_screen_layout);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ((TextView)dialog.findViewById(R.id.win_hints)).setText(hintString);
        ((TextView)dialog.findViewById(R.id.win_time)).setText(timeString);
        if(isNewBestTime){
            ((TextView)dialog.findViewById(R.id.win_new_besttime)).setVisibility(View.VISIBLE);
        }

        ((Button)dialog.findViewById(R.id.win_continue_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if(getActivity() != null) {
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().finish();
                }
            }
        });
        ((Button)dialog.findViewById(R.id.win_showGame_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (saved != null) {
            setParam(
                    saved.getString(ARG_TIME),
                    saved.getString(ARG_HINT),
                    saved.getBoolean(ARG_BEST)
            );
        }

        return dialog;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString(ARG_TIME, timeString);
        out.putString(ARG_HINT, hintString);
        out.putBoolean(ARG_BEST, isNewBestTime);
    }
}
