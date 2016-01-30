package org.secuso.privacyfriendlysudoku.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by TMZ_LToP on 30.01.2016.
 */
public class DialogActivity extends Dialog {

    private String time="";
    private String hints="";
    private boolean newBest=false;

    public DialogActivity(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogActivity(Context context,int themeResId,String t,String h, boolean newB) {
        super(context,themeResId);
        setParam(t,h,newB);
    }


    public void setParam(String t,String h, boolean newB){
        time = t;
        hints=h;
        newBest=newB;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ((TextView)findViewById(R.id.win_hints)).setText(hints);
        ((TextView)findViewById(R.id.win_time)).setText(time);
        if(newBest){
            ((TextView)findViewById(R.id.win_new_besttime)).setVisibility(View.VISIBLE);
        }

    }


}
