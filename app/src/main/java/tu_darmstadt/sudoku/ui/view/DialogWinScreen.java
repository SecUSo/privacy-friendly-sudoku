package tu_darmstadt.sudoku.ui.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by TMZ_LToP on 11.12.2015.
 */
public class DialogWinScreen extends android.support.v4.app.DialogFragment {


    ImageView upperView, lowerView;

    public DialogWinScreen(){

    }
    public void setProps(GameController gc){

        //no second Conjstrutor with argument allowed
        //save everything that should be sown in winscreeen and set in the text etc

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.win_screen_layout,container);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.ic_trophy_black_48dp);
        //getDialog().getWindow().setLayout(200,250);
        //Maybe creat real animation
        //Animation ani = AnimationUtils.loadAnimation(this,R.anim...);
        /*RotateAnimation anim = new RotateAnimation(0.0f,350.0f,60f,60f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        */
        return view;
    }
}
