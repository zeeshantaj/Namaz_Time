package com.example.namaz_time_app;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.namaz_time_app.Model.PrayerTimeManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout fajarLay, zoharLay, asarLay, maghribLay, ishaLay;
    private TextView fajrTxt, zoharTxt, asarTxt, maghribTxt, ishaTxt, nearNamazName,timerTxt;
    private String fajrTime,zoharTime,asarTime,maghribTime, ishaTime;
    private TextView currentTimeTxt;
    private Handler handler;
    private Runnable runnable;
    private Calendar calendar;
    private SharedPreferences sharedPreferences;
    long timeDifferenceMillis;

    private PrayerTimeManager prayerTimeManager;
    private Button startBtn;

    private static final int NOTIFICATION_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        currentTimeTxt = findViewById(R.id.currentTIme);
        fajarLay = findViewById(R.id.fajarLayout);
        zoharLay = findViewById(R.id.zoharLayout);
        asarLay = findViewById(R.id.asarLayout);
        maghribLay = findViewById(R.id.maghribLayout);
        ishaLay = findViewById(R.id.ishaLayout);

        fajrTxt = findViewById(R.id.fajrTime);
        zoharTxt = findViewById(R.id.zoharTime);
        asarTxt = findViewById(R.id.asarTime);
        maghribTxt = findViewById(R.id.maghribTime);
        ishaTxt = findViewById(R.id.ishaTime);
        timerTxt = findViewById(R.id.timerTxt);
        nearNamazName = findViewById(R.id.textView3);

        calendar = Calendar.getInstance();
        sharedPreferences = getSharedPreferences("MyPrayerTimes", MODE_PRIVATE);

        prayerTimeManager = new PrayerTimeManager(sharedPreferences);

        if (!prayerTimeManager.isPrayerTimeSet()) {
            Toast.makeText(this, "Data not set yet", Toast.LENGTH_SHORT).show();
        }
        fajrTime = prayerTimeManager.getPrayerTime("fajrTime");
        zoharTime = sharedPreferences.getString("zoharTime", "");
        asarTime = sharedPreferences.getString("asarTime", "");
        maghribTime = sharedPreferences.getString("maghribTime", "");
        ishaTime = sharedPreferences.getString("ishaTime", "");

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentTime();
                handler.postDelayed(this, 1000);
            }
        };
        fajarLay();
        zoharLay();
        asarLay();
        maghribLay();
        ishaLay();
        compareTime();


        if (Build.VERSION.SDK_INT >= 33){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_PERMISSION_CODE);
        }else if (Build.VERSION.SDK_INT <= 30){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            } else {
                // Permission already granted, proceed with loading the image

                Toast.makeText(this, "Permission already exist", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission is required to send notification ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void fajarLay() {

        fajrTxt.setText(fajrTime);

        fajarLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = view.getHour();
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }
                        String formatedStr = String.format(Locale.US, "%02d:%02d:%s", hour, minute, timeSet);
                        prayerTimeManager.savePrayerTime("fajrTime",formatedStr);
                        fajrTxt.setText(formatedStr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });


    }

    private void zoharLay() {

        zoharTxt.setText(zoharTime);
        zoharLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = view.getHour();
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }
                        String formatedStr = String.format(Locale.US, "%02d:%02d:%s", hour, minute, timeSet);
                        prayerTimeManager.savePrayerTime("zoharTime",formatedStr);
                        zoharTxt.setText(formatedStr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });


    }

    private void asarLay() {
        asarTxt.setText(asarTime);
        asarLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = view.getHour();
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }
                        String formatedStr = String.format(Locale.US, "%02d:%02d:%s", hour, minute, timeSet);

                        prayerTimeManager.savePrayerTime("asarTime",formatedStr);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("asarTime", formatedStr); // Save the fajr time
//                        editor.apply();


                        asarTxt.setText(formatedStr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });


    }

    private void maghribLay() {


        maghribTxt.setText(maghribTime);
        maghribLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = view.getHour();
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }
                        String formatedStr = String.format(Locale.US, "%02d:%02d:%s", hour, minute, timeSet);
                        prayerTimeManager.savePrayerTime("maghribTime",formatedStr);
                        maghribTxt.setText(formatedStr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });


    }

    private void ishaLay() {


        ishaTxt.setText(ishaTime);
        ishaLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = view.getHour();
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "pm";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "am";
                        } else if (hour == 12) {
                            timeSet = "pm";
                        } else {
                            timeSet = "am";
                        }
                        String formatedStr = String.format(Locale.US, "%02d:%02d:%s", hour, minute, timeSet);
                        prayerTimeManager.savePrayerTime("ishaTime",formatedStr);
                        ishaTxt.setText(formatedStr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

    }
    private void compareTime() {
        Calendar currentCalendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:a",Locale.US);
        Date calendarTime = currentCalendar.getTime();
        String currentTime = sdf.format(calendarTime);

        String[] times = {fajrTime, zoharTime, asarTime, maghribTime, ishaTime};
        int nextTime = nextTimeArrayIndex(currentTime, times);
        if (nextTime>=0){

            switch (nextTime) {
                case 0:
                    fajrTxt.setTextColor(getResources().getColor(R.color.green));
                    nearNamazName.setText("FAJAR");
                    break;
                case 1:
                    zoharTxt.setTextColor(getResources().getColor(R.color.green));
                    nearNamazName.setText("ZOHAR");
                    break;
                case 2:
                    asarTxt.setTextColor(getResources().getColor(R.color.green));
                    nearNamazName.setText("ASAR");
                    break;
                case 3:
                    maghribTxt.setTextColor(getResources().getColor(R.color.green));
                    nearNamazName.setText("MAGHRIB");
                    break;
                case 4:
                    ishaTxt.setTextColor(getResources().getColor(R.color.green));
                    nearNamazName.setText("ISHA");
                    break;
                default:
                    break;
            }

            String providedTime = times[nextTime];

            try {

                Calendar calendar1 = Calendar.getInstance();

                Date currentTime2 = sdf.parse(currentTime);
                calendar1.setTime(currentTime2);
                Date providedTime2 = sdf.parse(providedTime);
                timeDifferenceMillis = providedTime2.getTime()-currentTime2.getTime();

                currentCalendar.setTime(currentTime2);


            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }



            CountDownTimer countDownTimer = new CountDownTimer(timeDifferenceMillis,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    NumberFormat f = new DecimalFormat("00");
                    long hr = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    timerTxt.setText(f.format(hr) + ":" + f.format(min) + ":" + f.format(sec));
                }
                @Override
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "Time finished", Toast.LENGTH_SHORT).show();
                }
            }.start();
        }

    }



    int nextTimeArrayIndex(String Finding, String[] fromArray) {
        if (Finding == null || Finding.length() < 5) {
            return -1; // Handle invalid input
        }

        int shortest = -1, shortestsec = -1;
        long minsecdif = (24 * 60 * 60 + 1), minsec = (24 * 60 * 60 + 1);
        int hr = Integer.parseInt(Finding.substring(0, 2));
        int min = Integer.parseInt(Finding.substring(3, 5));
        long seconds = convertToSec(hr, min, 0, Finding.substring(Finding.length() - 2));

        for (int i = 0; i < fromArray.length; i++) {
            if (fromArray[i] == null || fromArray[i].length() < 5) {
                continue; // Skip invalid entries
            }

            int temphr = Integer.parseInt(fromArray[i].substring(0, 2));
            int tempmin = Integer.parseInt(fromArray[i].substring(3, 5));
            String amOrPm = fromArray[i].substring(fromArray[i].length() - 2);
            long tempsec = convertToSec(temphr, tempmin, 0, amOrPm);
            if ((tempsec - seconds) > 0 && minsecdif > (tempsec - seconds)) {
                minsecdif = (tempsec - seconds);
                shortest = i;
            }
            if (minsec > tempsec) {
                minsec = tempsec;
                shortestsec = i;
            }
        }
        if (shortest >= 0) {
            return shortest;
        } else {
            return shortestsec;
        }
    }

    long convertToSec(int hr,int min,int sec,String AMorPM){
        if(hr==12)
        {
            hr=0;
        }
        long secs = (hr*60*60) + (min*60) + (sec*60);
        if(AMorPM.equalsIgnoreCase("pm"))
        {
            secs += (12*60*60);
        }
        return secs;
    }
    private void currentTime() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");
        String formattedDate = df.format(c.getTime());
        currentTimeTxt.setText(formattedDate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(runnable);

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

}
//    int nextTimeArrayIndex(String Finding,String[] fromArray){
//        int shortest=-1,shortestsec=-1;
//        long minsecdif=(24*60*60+1),minsec=(24*60*60+1);
//        int hr=Integer.parseInt(Finding.substring(0, 2));
//        int min=Integer.parseInt(Finding.substring(3, 5));
//        long seconds = convertToSec(hr, min, 0, Finding.substring(Finding.length()-2));
//        System.out.println("seconds :" + seconds);
//
//
//        for (int i = 0; i < fromArray.length; i++) {
//            int temphr=Integer.parseInt(fromArray[i].substring(0, 2));
//            int tempmin = Integer.parseInt(fromArray[i].substring(3,5));
//            long tempsec = convertToSec(temphr, tempmin, 0, fromArray[i].substring(Finding.length()-2));
//            System.out.println("Compared to :" + tempsec);
//            if((tempsec - seconds) > 0 && minsecdif > (tempsec - seconds))
//            {
//                minsecdif = (tempsec - seconds);
//                shortest = i;
//            }
//            if(minsec > tempsec)
//            {
//                minsec = tempsec;
//                shortestsec=i;
//            }
//        }
//        if(shortest >=0)
//        {
//            return  shortest;
//        }
//        else
//        {
//            return  shortestsec;
//        }
//    }