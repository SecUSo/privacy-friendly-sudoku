package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by TMZ_LToP on 19.11.2015.
 */
public class SudokuTimer extends TextView {

    int seconds=0;
    int mins=0;
    long hours=0;

    public SudokuTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getSeconds(){
        return seconds;
    }
    public int getMins(){
        return mins;
    }
    public long getHours(){
        return hours;
    }

    public void increase () {
        seconds ++;
        if(seconds+1 == 60){
            seconds=0;
            mins++;
        }if (mins == 60) {
            mins=0;
            hours++;
        }
        String h,m,s;
        s = (seconds<10)? "0"+String.valueOf(seconds) : String.valueOf(seconds);
        m = (mins<10)? "0"+String.valueOf(mins) : String.valueOf(mins);
        h = (hours<10)? "0"+String.valueOf(hours) : String.valueOf(hours);
        StringBuilder sb = new StringBuilder();
        setText(h+":"+m+":"+s);
    }

}
