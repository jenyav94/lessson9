package com.csc.jv.weather.downloads;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.csc.jv.weather.MainActivity;
import com.csc.jv.weather.model.ForecastItem;
import com.csc.jv.weather.providers.ForecastTable;
import com.csc.jv.weather.providers.WeatherContentProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class WeatherService extends IntentService {

    public static final String RESPONSE = "response";
    public static final String FORECAST = "forecast";
    public static final String FORECAST_UPDATE = "forecast_update";
    public static final String ALL_FORECAST_UPDATE = "all_forecast_update";
    public static final String FORECAST_URL = "forecast";
    public static final String FORECAST_ID = "forecast_id";

    public WeatherService() {
        super("WeatherService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            String urlString;
            String action = intent.getAction();

            switch (action) {
                case ALL_FORECAST_UPDATE:

                    Cursor cursor = getContentResolver().query(WeatherContentProvider.ENTRIES_URI,
                            null, null, null, null);

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            final String city_name = cursor.getString(cursor.getColumnIndex(ForecastTable.COLUMN_CITY_NAME));
                            final String _id = cursor.getString(cursor.getColumnIndex(ForecastTable._ID));
                            urlString = MainActivity.FORECAST_URL + city_name + MainActivity.APPID + MainActivity.API_KEY;

                            try {
                                List<ForecastItem> result = loadJSONFromNetwork(urlString);

                                if (result != null) {
                                    for (ForecastItem item : result) {

                                        ContentValues values = item.toCursor();

                                        Uri uri = ContentUris.withAppendedId(
                                                WeatherContentProvider.ENTRIES_URI,
                                                Long.valueOf(_id));

                                        getContentResolver().update(uri, values, null, null);
                                    }

                                }
                            } catch (IOException ignored) {
                            }
                        }

                        Intent responseIntent = new Intent();
                        responseIntent.setAction(RESPONSE);
                        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        sendBroadcast(responseIntent);

                        cursor.close();
                    }

                    break;
                case FORECAST_UPDATE:
                    urlString = intent.getStringExtra(FORECAST_URL);
                    String cursor_id = intent.getStringExtra(FORECAST_ID);
                    try {

                        List<ForecastItem> result = loadJSONFromNetwork(urlString);

                        if (result != null) {
                            for (ForecastItem item : result) {

                                ContentValues values = item.toCursor();

                                Uri uri = ContentUris.withAppendedId(WeatherContentProvider.ENTRIES_URI,
                                        Long.valueOf(cursor_id));

                                getContentResolver().update(uri, values, null, null);
                            }

                            Intent responseIntent = new Intent();
                            responseIntent.setAction(RESPONSE);
                            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            sendBroadcast(responseIntent);
                        }
                    } catch (IOException ignored) {
                    }
                    break;
                case FORECAST:
                    urlString = intent.getStringExtra(FORECAST_URL);
                    try {

                        List<ForecastItem> result = loadJSONFromNetwork(urlString);

                        if (result != null) {
                            for (ForecastItem item : result) {

                                ContentValues values = item.toCursor();

                                getContentResolver().insert(WeatherContentProvider.ENTRIES_URI, values);
                            }
                        }
                    } catch (IOException ignored) {
                    }
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

    }


    private List loadJSONFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        // Instantiate the parser
        ForecastJSONParser jsonParser = new ForecastJSONParser();
        List<ForecastItem> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = jsonParser.readJsonStream(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }


    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // conn.setReadTimeout(10000 /* milliseconds */);
        //conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

}
