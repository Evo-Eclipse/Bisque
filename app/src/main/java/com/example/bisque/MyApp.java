package com.example.bisque;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.room.Room;

import com.example.bisque.db.AppDatabase;

public class MyApp extends Application {

    public static final String CHANNEL_ID = "timer_channel";
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        createNotificationChannel();
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Channel for timer settings");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
