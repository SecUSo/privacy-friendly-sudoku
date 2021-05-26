package org.secuso.privacyfriendlysudoku.ui.presenter;

import android.content.Intent;

public interface CreateSudokuContract {
    interface  View {
        void showToast();
        void showToast(StringBuilder message);
        void setUpLayout();
    }

    interface Presenter {
        void attachView(CreateSudokuContract.View view);
        void detachView(CreateSudokuContract.View view);
    }
}
