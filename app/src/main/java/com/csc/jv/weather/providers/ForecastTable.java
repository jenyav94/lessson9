package com.csc.jv.weather.providers;

import android.provider.BaseColumns;


public interface ForecastTable extends BaseColumns {
    String TABLE_NAME = "forecast";

    String COLUMN_CITY_ID = "city_id";
    String COLUMN_UPDATE_TIME = "update_time";
    String COLUMN_CITY_NAME = "city_name";
    String COLUMN_WEATHER_DESCRIPTION = "weather_description";
    String COLUMN_TEMPERATURE = "temperature";
    String COLUMN_CLOUDS = "clouds";
    String COLUMN_WIND_SPEED = "wind_speed";
    String COLUMN_HUMIDITY = "humidity";
    String COLUMN_PRESSURE = "pressure";
}
