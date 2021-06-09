package org.secuso.privacyfriendlysudoku.ui.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlysudoku.controller.GameController;
import org.secuso.privacyfriendlysudoku.game.listener.IHighlightChangedListener;

import static org.secuso.privacyfriendlysudoku.ui.view.SudokuButtonType.Spacer;
import static org.secuso.privacyfriendlysudoku.ui.view.SudokuButtonType.getSpecialButtons;

public abstract class SpecialButtonLayout extends LinearLayout implements IHighlightChangedListener {

    public int fixedButtonsCount = getSpecialButtons().size();

    FragmentManager fragmentManager;
    GameController gameController;
    SudokuKeyboardLayout keyboard;
    Bitmap bitMap,bitResult;
    Canvas canvas;
    Context context;
    float buttonMargin;
    
    protected SpecialButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, getSudokuSpecialButtonLayout());
        buttonMargin = a.getDimension(getSudokuSpecialKeyboardMargin(), 5f);
        a.recycle();

        setWeightSum(fixedButtonsCount);
        this.context = context;
    }

    public void setButtonsEnabled(boolean enabled) {
        setFixedButtonsEnabled(enabled);
    }

    /*
     * Set up the vector drawables so that they can be properly displayed
     * despite using theme attributes for their fill color
     */
    protected void setUpVectorDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        bitMap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitResult = Bitmap.createBitmap(bitMap.getWidth(), bitMap.getHeight(), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitResult);
    }

    protected abstract int[] getSudokuSpecialButtonLayout();
    protected abstract int getSudokuSpecialKeyboardMargin();
    protected abstract void setFixedButtonsEnabled(boolean enabled);

}
