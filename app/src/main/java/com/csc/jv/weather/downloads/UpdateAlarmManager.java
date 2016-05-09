package com.csc.jv.weather.downloads;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class UpdateAlarmManager {

    private static final int INTERVAL = 3600 * 1000;

    private PendingIntent pendingIntent;

    private AlarmManager alarmManager;

    public UpdateAlarmManager(Context context) {

        Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(WeatherService.ALL_FORECAST_UPDATE);

        pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm() {
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL,
                INTERVAL,
                pendingIntent
        );
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }

}
