package org.secuso.privacyfriendlysudoku.ui.presenter;

public interface gameContract {
    interface View {
    }

    interface Presenter {
        void attachView(gameContract.View view);
        void detachView(gameContract.View view);
    }
}

