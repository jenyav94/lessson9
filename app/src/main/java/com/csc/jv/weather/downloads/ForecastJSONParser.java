package com.csc.jv.weather.downloads;


import android.util.JsonReader;

import com.csc.jv.weather.model.ForecastItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastJSONParser {


    private static final String CITY_NAME = "name";
    private static final String CITY_ID = "id";
    private static final String WEATHER = "weather";
    private static final String MAIN = "main";
    private static final String DESCRIPTION = "description";
    private static final String WIND = "wind";
    private static final String SPEED = "speed";
    private static final String CLOUDS = "clouds";
    private static final String TEMPERATURE = "temp";
    private static final String HUMIDITY = "humidity";
    private static final String PRESSURE = "pressure";
    private static final String ALL = "all";


    public List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readFeed(reader);

        } finally {
            reader.close();
        }
    }

    public List readFeed(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginObject();
        while (reader.hasNext()) {
            messages.add(readEntry(reader));
        }
        reader.endObject();

        return messages;
    }

    private ForecastItem readEntry(JsonReader reader) throws IOException {

        String city_name = null;
        String city_id = null;
        String weather_description = null;
        String wind_speed = null;
        String clouds = null;
        String temperature = null;
        String humidity = null;
        String pressure = null;


        while (reader.hasNext()) {
            String entryName = reader.nextName();
            switch (entryName) {
                case CITY_NAME:
                    city_name = reader.nextString();
                    break;
                case CITY_ID:
                    city_id = reader.nextString();
                    break;
                case WEATHER:
                    weather_description = readWeather(reader);
                    break;
                case MAIN:
                    temperature = readTemperature(reader) + " " + '\u00B0';
                    pressure = readPressure(reader) + " hPa";
                    humidity = readHumidity(reader) + " " + '\u0025';
                    break;
                case WIND:
                    wind_speed = readWindSpeed(reader) + " meter/sec";
                    break;
                case CLOUDS:
                    clouds = readClouds(reader) + " " + '\u0025';
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return new ForecastItem(city_id, format.format(new Date()), city_name, weather_description, temperature,
                clouds, wind_speed, humidity, pressure);
    }

    private String readClouds(JsonReader reader) throws IOException {
        String clouds = "";

        reader.beginObject();

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(ALL)) {
                clouds = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return clouds;
    }

    private String readWindSpeed(JsonReader reader) throws IOException {
        String windSpeed = "";

        reader.beginObject();

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(SPEED)) {
                windSpeed = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return windSpeed;
    }

    private String readHumidity(JsonReader reader) throws IOException {

        String humidity = "";

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(HUMIDITY)) {
                humidity = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return humidity;
    }

    private String readPressure(JsonReader reader) throws IOException {

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(PRESSURE)) {
                return reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        return "";
    }

    private String readTemperature(JsonReader reader) throws IOException {

        reader.beginObject();

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(TEMPERATURE)) {

                double tempInF = Double.valueOf(reader.nextString());
                int tempInC = (int) (tempInF - 273.15);

                return String.valueOf(tempInC);
            } else {
                reader.skipValue();
            }
        }

        return "";
    }

    private String readWeather(JsonReader reader) throws IOException {
        String description = "";

        reader.beginArray();
        reader.beginObject();

        while (reader.hasNext()) {
            String entryName = reader.nextName();
            if (entryName.equals(DESCRIPTION)) {
                description = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        reader.endArray();

        return description;
    }

}
