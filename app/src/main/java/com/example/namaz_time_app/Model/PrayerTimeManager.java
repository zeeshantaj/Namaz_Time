package com.example.namaz_time_app.Model;

import android.content.SharedPreferences;

public class PrayerTimeManager {

    private SharedPreferences sharedPreferences;

    public PrayerTimeManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void savePrayerTime(String key,String time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, time);
        editor.apply();
    }

    public String getPrayerTime(String key){
        return sharedPreferences.getString(key, "");
    }

    public boolean isPrayerTimeSet() {
        String fajrTime = getPrayerTime("fajrTime");
        String zoharTime = getPrayerTime("zoharTime");
        String asarTime = getPrayerTime("asarTime");
        String maghribTime = getPrayerTime("maghribTime");
        String ishaTime = getPrayerTime("ishaTime");

        return !(fajrTime.isEmpty() || zoharTime.isEmpty() || asarTime.isEmpty() || maghribTime.isEmpty() || ishaTime.isEmpty());
    }

}
