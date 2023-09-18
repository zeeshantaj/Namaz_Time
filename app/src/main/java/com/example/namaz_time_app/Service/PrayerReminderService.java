package com.example.namaz_time_app.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.namaz_time_app.Broadcast.PrayerReminderReceiver;
import com.example.namaz_time_app.Model.PrayerTimeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerReminderService extends Service {

    private AlarmManager alarmManager;
    private PendingIntent[] pendingIntents = new PendingIntent[5]; // One for each prayer time

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String[] prayerTimes = getPrayerTimesFromSharedPreferences();



        if (prayerTimes != null && prayerTimes.length == 5) {
            for (int i = 0; i < 5; i++) {
                Log.e("MyApp", "prayers" + prayerTimes[i]);
                schedulePrayerReminder(prayerTimes[i], i);
            }
        }
        return START_STICKY;
    }

    private void schedulePrayerReminder(String prayerTime, int index) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:a", Locale.US);
            Date prayerTimeDate = sdf.parse(prayerTime);

            // Check if the prayer time is in the future
            if (prayerTimeDate.after(new Date())) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(prayerTimeDate);
                calendar.set(Calendar.SECOND, 0); // Ensure the seconds are set to 0

                Intent intent = new Intent(this, PrayerReminderReceiver.class);
                intent.putExtra("prayer_name", getPrayerName(index));
                intent.putExtra("prayer_time", prayerTime);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Schedule the alarm to repeat every day
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                pendingIntents[index] = pendingIntent;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getPrayerName(int index) {
        switch (index) {
            case 0:
                return "Fajr";
            case 1:
                return "Zohar";
            case 2:
                return "Asar";
            case 3:
                return "Maghrib";
            case 4:
                return "Isha";
            default:
                return "";
        }
    }

    private String[] getPrayerTimesFromSharedPreferences() {
        Log.e("MyApp", "getPrayerTimeFromSharePreference");

        // Implement your logic to retrieve prayer times from SharedPreferences
        // Return an array of five prayer times
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrayerTimes", MODE_PRIVATE);
        PrayerTimeManager prayerTimeManager =new PrayerTimeManager(sharedPreferences);
        String fajrTime = prayerTimeManager.getPrayerTime("fajrTime");
        String zoharTime = prayerTimeManager.getPrayerTime("zoharTime");
        String asarTime = prayerTimeManager.getPrayerTime("asarTime");
        String maghribTime = prayerTimeManager.getPrayerTime("maghribTime");
        String ishaTime = prayerTimeManager.getPrayerTime("ishaTime");

        return new String[]{fajrTime, zoharTime, asarTime, maghribTime, ishaTime};
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
