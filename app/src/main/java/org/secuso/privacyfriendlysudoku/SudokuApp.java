package org.secuso.privacyfriendlysudoku;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public class SudokuApp extends Application {

    public static final String CHANNEL_ID = "sudoku.0";

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channels
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default", NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
