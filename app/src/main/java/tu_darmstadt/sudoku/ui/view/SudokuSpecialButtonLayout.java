package tu_darmstadt.sudoku.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import tu_darmstadt.sudoku.controller.GameController;

/**
 * Created by TMZ_LToP on 17.11.2015.
 */
public class SudokuSpecialButtonLayout extends LinearLayout {

    SudokuSpecialButton[] fixedButtons;
    public int fixedButtonsCount = SudokuButtonType.getSpecialButtons().size();
    GameController gameController;
    SudokuKeyboardLayout keyboard;
    Bitmap bitMap,bitResult;
    Canvas canvas;


    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof SudokuSpecialButton) {
                SudokuSpecialButton btn = (SudokuSpecialButton)v;

                int row = gameController.getSelectedRow();
                int col = gameController.getSelectedCol();

                switch(btn.getType()) {
                    case Delete:
                        gameController.deleteSelectedValue();
                        break;
                    case NoteToggle:
                        //btn.setText(keyboard.notesEnabled ? "ON" : "OFF");
                        //Animation rotates whole button
                        /*AnimationSet aniset = new AnimationSet(true);
                        aniset.setInterpolator(new DecelerateInterpolator());
                        aniset.setFillAfter(true);
                        aniset.setFillEnabled(true);

                        RotateAnimation rotate = new RotateAnimation(0.0f,(keyboard.notesEnabled ? 90.0f:0.0f),
                                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                                Animation.RELATIVE_TO_SELF,0.5f);
                        rotate.setDuration(1500);
                        rotate.setFillAfter(true);
                        aniset.addAnimation(rotate);

                        btn.startAnimation(aniset);*/

                        // rotates now only the Drawable
                        bitMap = BitmapFactory.decodeResource(getResources(), btn.getType().getResID());
                        bitResult = Bitmap.createBitmap(bitMap.getWidth(), bitMap.getHeight(), Bitmap.Config.ARGB_8888);

                        canvas = new Canvas(bitResult);
                        canvas.rotate(keyboard.notesEnabled?0.0f:90.0f,bitMap.getWidth()/2,bitMap.getHeight()/2);
                        canvas.drawBitmap(bitMap,0,0,null);
                        btn.setImageBitmap(bitResult);
                        keyboard.toggleNotesEnabled();
                        break;
                    case Do:
                        gameController.ReDo();
                        gameController.saveGame(getContext());
                        break;
                    case Undo:
                        gameController.UnDo();
                        gameController.saveGame(getContext());
                        break;
                    case Hint:
                        if(gameController.isValidCellSelected()) {
                            gameController.hint();
                        }
                        break;
                    case NumberOrCellFirst:
                        // TODO: not implemented
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


    public void setButtons(int width, GameController gc,SudokuKeyboardLayout key) {
        keyboard=key;
        gameController = gc;
        fixedButtons = new SudokuSpecialButton[fixedButtonsCount];
        LayoutParams p;
        int i = 0;
        //ArrayList<SudokuButtonType> type = (ArrayList<SudokuButtonType>) SudokuButtonType.getSpecialButtons();
        for (SudokuButtonType t : SudokuButtonType.getSpecialButtons()){
            fixedButtons[i] = new SudokuSpecialButton(getContext(),null);
            p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
            p.setMargins(5, 5, 5, 5);

            //int width2 =width/(fixedButtonsCount);
            //p.width= width2-15;
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
}
