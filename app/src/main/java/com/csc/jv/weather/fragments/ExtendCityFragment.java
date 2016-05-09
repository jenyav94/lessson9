package com.csc.jv.weather.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csc.jv.weather.MainActivity;
import com.csc.jv.weather.R;
import com.csc.jv.weather.downloads.WeatherService;
import com.csc.jv.weather.model.ForecastItem;
import com.csc.jv.weather.providers.WeatherContentProvider;


public class ExtendCityFragment extends Fragment {


    private static final String CURSOR_ID = "cursor_id";

    private String cursor_id;

    private ForecastItem forecastItem;

    private UpdateBroadcastReceiver updateBroadcastReceiver;

    private OnFragmentInteractionListener mListener;

    public ExtendCityFragment() {
    }


    public static ExtendCityFragment newInstance(String _id) {
        ExtendCityFragment fragment = new ExtendCityFragment();
        Bundle args = new Bundle();
        args.putString(CURSOR_ID, _id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View viewPager = getActivity().findViewById(R.id.pager);
        if (viewPager != null) {
            setHasOptionsMenu(true);
        }

        if (getArguments() != null) {
            cursor_id = getArguments().getString(CURSOR_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateBroadcastReceiver = new UpdateBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(WeatherService.RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(updateBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(updateBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extend_city, container, false);
        Uri uri = ContentUris.withAppendedId(WeatherContentProvider.ENTRIES_URI, Long.valueOf(cursor_id));
        Cursor cursor = null;

        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                forecastItem = ForecastItem.fromCursor(cursor);

                ((TextView) view.findViewById(R.id.update_time)).setText(forecastItem.update_time);
                ((TextView) view.findViewById(R.id.city_name)).setText(forecastItem.city_name);
                ((TextView) view.findViewById(R.id.weather_description)).setText(forecastItem.weather_description);
                ((TextView) view.findViewById(R.id.temperature)).setText(forecastItem.temperature);
                ((TextView) view.findViewById(R.id.clouds)).setText(forecastItem.clouds);
                ((TextView) view.findViewById(R.id.wind_speed)).setText(forecastItem.wind_speed);
                ((TextView) view.findViewById(R.id.humidity)).setText(forecastItem.humidity);
                ((TextView) view.findViewById(R.id.pressure)).setText(forecastItem.pressure);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.extend_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_refresh:
                Intent intent = new Intent(getActivity(), WeatherService.class);
                String urlString = MainActivity.FORECAST_URL + forecastItem.city_name
                        + MainActivity.APPID + MainActivity.API_KEY;
                intent
                        .setAction(WeatherService.FORECAST_UPDATE)
                        .putExtra(WeatherService.FORECAST_URL, urlString)
                        .putExtra(WeatherService.FORECAST_ID, cursor_id);

                getActivity().startService(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public class UpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_extend_container);

            if (currentFragment != null) {
                FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction
                        .detach(currentFragment)
                        .attach(currentFragment)
                        .commit();
            }
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
