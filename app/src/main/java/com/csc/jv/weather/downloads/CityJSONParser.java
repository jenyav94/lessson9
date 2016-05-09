package com.csc.jv.weather.downloads;

import android.util.JsonReader;

import com.csc.jv.weather.model.CityItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CityJSONParser {
    private static final String CITY_NAME = "name";
    private static final String CITY_ID = "_id";

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

        while (reader.hasNext()) {
            messages.add(readEntry(reader));
        }

        return messages;
    }

    private CityItem readEntry(JsonReader reader) throws IOException {

        String city_name = null;
        String city_id = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String entryName = reader.nextName();
            switch (entryName) {
                case CITY_NAME:
                    city_name = reader.nextString();
                    break;
                case CITY_ID:
                    city_id = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new CityItem(city_id, city_name);
    }
}
