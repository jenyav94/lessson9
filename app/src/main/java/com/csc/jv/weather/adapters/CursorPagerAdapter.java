package com.csc.jv.weather.adapters;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.csc.jv.weather.fragments.ExtendCityFragment;
import com.csc.jv.weather.providers.ForecastTable;

public class CursorPagerAdapter extends FragmentStatePagerAdapter {
    private final String[] projection;
    private Cursor cursor;

    public CursorPagerAdapter(FragmentManager fm, String[] projection, Cursor cursor) {
        super(fm);
        this.projection = projection;
        this.cursor = cursor;
    }

    @Override
    public ExtendCityFragment getItem(int position) {
        if (cursor == null)
            return null;

        cursor.moveToPosition(position);
        ExtendCityFragment frag;
        try {
            final String cursor_id = cursor.getString(cursor.getColumnIndex(ForecastTable._ID));
            frag = ExtendCityFragment.newInstance(cursor_id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
//        Bundle args = new Bundle();
//        for (int i = 0; i < projection.length; ++i) {
//            args.putString(projection[i], cursor.getString(i));
//        }
//        frag.setArguments(args);
        return frag;
    }


    @Override
    public int getCount() {
        if (cursor == null)
            return 0;
        else
            return cursor.getCount();
    }

    public void swapCursor(Cursor c) {
        if (cursor == c)
            return;

        this.cursor = c;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}
