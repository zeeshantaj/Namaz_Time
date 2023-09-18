package com.example.namaz_time_app.Broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.namaz_time_app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        String prayerTime = intent.getStringExtra("prayer_time");

        // Check if the prayer time has arrived (just to be sure)
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:a", Locale.US);
        try {
            Date currentTime = new Date();
            Date prayerTimeDate = sdf.parse(prayerTime);
            if (currentTime.after(prayerTimeDate)) {
                // The prayer time has already passed, so do nothing
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Play a sound
        playSound(context);

        // You can also show a notification here if you want
        showNotification(context, prayerName, "It's time for " + prayerName);

        Log.e("MyApp", "Receiver: " + prayerName + " at " + prayerTime);
    }

    private void playSound(Context context) {
        // You can use the MediaPlayer class to play a sound
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.allhuakbar_tasbih); // Replace with your sound file
        mediaPlayer.start();

        // You may also want to use a SoundPool for better performance
        // SoundPool soundPool = new SoundPool.Builder().build();
        // int soundId = soundPool.load(context, R.raw.your_sound_file, 1);
        // soundPool.play(soundId, 1, 1, 0, 0, 1);

    }
    private void showNotification(Context context, String title, String content) {
        // Implement your code to show a notification here
        // You can use the NotificationCompat.Builder class to create and show a notification
    }
}
