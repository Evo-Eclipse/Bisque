package com.example.bisque.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.example.bisque.MyApp;
import com.example.bisque.R;

import java.util.HashMap;
import java.util.Map;

public class TimerService extends Service {
    public static final String ACTION_TIMER_TICK = "com.example.bisque.ACTION_TIMER_TICK";
    public static final String EXTRA_TIMER_ID = "com.example.bisque.EXTRA_TIMER_ID";
    public static final String EXTRA_REMAINING_SECONDS = "com.example.bisque.EXTRA_REMAINING_SECONDS";

    private final IBinder binder = new TimerBinder();
    private final Map<String, TimerData> timers = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private static final String CHANNEL_ID = MyApp.CHANNEL_ID; // Use the same channel ID

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    private static class TimerData {
        int remainingSeconds;
        boolean isRunning;
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadTimerData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveTimerData();
    }

    public void startTimer(String timerId, int initialSeconds) {
        TimerData timerData = getOrCreateTimerData(timerId);
        timerData.remainingSeconds = initialSeconds;
        timerData.isRunning = true;

        timerData.runnable = new Runnable() {
            @Override
            public void run() {
                if (timerData.remainingSeconds > 0) {
                    timerData.remainingSeconds--;
                    updateTimerUI(timerId, timerData.remainingSeconds);
                    timerData.handler.postDelayed(this, 1000);
                } else {
                    stopTimer(timerId);
                    sendNotification();
                }
            }
        };
        timerData.handler.post(timerData.runnable);
    }

    public void pauseTimer(String timerId) {
        TimerData timerData = timers.get(timerId);
        if (timerData != null && timerData.isRunning) {
            timerData.handler.removeCallbacks(timerData.runnable);
            timerData.isRunning = false;
        }
    }

    public void stopTimer(String timerId) {
        TimerData timerData = timers.get(timerId);
        if (timerData != null) {
            timerData.handler.removeCallbacks(timerData.runnable);
            timerData.isRunning = false;
            timerData.remainingSeconds = 0;
            updateTimerUI(timerId, 0);
        }
    }

    public int getRemainingSeconds(String timerId) {
        TimerData timerData = timers.get(timerId);
        return timerData != null ? timerData.remainingSeconds : 0;
    }

    private TimerData getOrCreateTimerData(String timerId) {
        TimerData timerData = timers.get(timerId);
        if (timerData == null) {
            timerData = new TimerData();
            timers.put(timerId, timerData);
        }
        return timerData;
    }

    private void sendNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer Finished")
                .setContentText("A timer has finished running.")
                .setSmallIcon(R.drawable.ic_notifications_timer_24dp)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void updateTimerUI(String timerId, int remainingSeconds) {
        Intent intent = new Intent(ACTION_TIMER_TICK);
        intent.putExtra(EXTRA_TIMER_ID, timerId);
        intent.putExtra(EXTRA_REMAINING_SECONDS, remainingSeconds);
        sendBroadcast(intent);
    }

    private void loadTimerData() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                TimerData timerData = new TimerData();
                timerData.remainingSeconds = (Integer) entry.getValue();
                timerData.isRunning = false; // Timers should not be running when the app starts
                timers.put(entry.getKey(), timerData);
            }
        }
    }

    private void saveTimerData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, TimerData> entry : timers.entrySet()) {
            editor.putInt(entry.getKey(), entry.getValue().remainingSeconds);
        }
        editor.apply();
    }
}
