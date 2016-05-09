package com.csc.jv.weather.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.csc.jv.weather.providers.ForecastTable;


public class ForecastItem {

    public final String city_id;
    public final String update_time;
    public final String city_name;
    public final String weather_description;
    public final String temperature;
    public final String clouds;
    public final String wind_speed;
    public final String humidity;
    public final String pressure;

    public ForecastItem(String city_id, String update_time, String city_name, String weather_description, String temperature,
                        String clouds, String wind_speed, String humidity, String pressure) {

        this.city_id = city_id;
        this.update_time = update_time;
        this.city_name = city_name;
        this.weather_description = weather_description;
        this.temperature = temperature;
        this.clouds = clouds;
        this.wind_speed = wind_speed;
        this.humidity = humidity;
        this.pressure = pressure;
    }


    public static ForecastItem fromCursor(Cursor cursor) {

        final String city_id = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_CITY_ID));
        final String update_time = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_UPDATE_TIME));
        final String city_name = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_CITY_NAME));
        final String weather_description = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_WEATHER_DESCRIPTION));
        final String temperature = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_TEMPERATURE));
        final String clouds = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_CLOUDS));
        final String wind_speed = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_WIND_SPEED));
        final String humidity = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_HUMIDITY));
        final String pressure = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_PRESSURE));

        return new ForecastItem(city_id, update_time, city_name, weather_description, temperature, clouds, wind_speed,
                humidity, pressure);
    }

    public ContentValues toCursor() {

        ContentValues values = new ContentValues();

        values.put(ForecastTable.COLUMN_CITY_ID, city_id);
        values.put(ForecastTable.COLUMN_UPDATE_TIME, update_time);
        values.put(ForecastTable.COLUMN_CITY_NAME, city_name);
        values.put(ForecastTable.COLUMN_WEATHER_DESCRIPTION, weather_description);
        values.put(ForecastTable.COLUMN_TEMPERATURE, temperature);
        values.put(ForecastTable.COLUMN_CLOUDS, clouds);
        values.put(ForecastTable.COLUMN_WIND_SPEED, wind_speed);
        values.put(ForecastTable.COLUMN_HUMIDITY, humidity);
        values.put(ForecastTable.COLUMN_PRESSURE, pressure);

        return values;
    }

}
