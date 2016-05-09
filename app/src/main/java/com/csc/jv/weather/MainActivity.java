package com.csc.jv.weather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.csc.jv.weather.downloads.UpdateAlarmManager;
import com.csc.jv.weather.downloads.WeatherService;
import com.csc.jv.weather.fragments.CityListFragment;
import com.csc.jv.weather.fragments.ExtendCityFragment;

public class MainActivity extends AppCompatActivity implements
        CityListFragment.OnListFragmentInteractionListener,
        ExtendCityFragment.OnFragmentInteractionListener {

    private static final String PREFERENCE = "preference_file";

    private static final String FIRST_RUN = "first_run";

    private static final String[] defaultCities = {"petersburg", "moscow", "vladivostok"};

    public static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    public static final String API_KEY = "0f4eedb28dd6da08060fea6e01430283";

    public static final String APPID = "&APPID=";

    public static final String CURSOR_POSITION = "cursor_position";

    private static final String ALARM = "alarm";

    private SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PREFERENCE, 0);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_main_container, CityListFragment.newInstance(1))
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (settings.getBoolean(FIRST_RUN, true)) {

            Intent intent = new Intent(this, WeatherService.class);

            for (String city : defaultCities) {
                String urlString = FORECAST_URL + city + APPID + API_KEY;
                intent.setAction(WeatherService.FORECAST).putExtra(WeatherService.FORECAST_URL, urlString);
                startService(intent);
            }

            UpdateAlarmManager updateAlarmManager = new UpdateAlarmManager(this);
            updateAlarmManager.setAlarm();

            settings.edit().putBoolean(FIRST_RUN, false).apply();
            settings.edit().putBoolean(ALARM, true).apply();
        }
    }

    @Override
    public void onListFragmentInteraction(String cursor_id, int cursor_position) {

        boolean dual_pane = getResources().getBoolean(R.bool.dual_pane);

        if (dual_pane) {
            ExtendCityFragment extendCityFragment = ExtendCityFragment.newInstance(cursor_id);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_extend_container, extendCityFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ExtendActivity.class);
            intent.putExtra(CURSOR_POSITION, cursor_position);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_refresh_all:
                Intent intent = new Intent(this, WeatherService.class);
                intent.setAction(WeatherService.ALL_FORECAST_UPDATE);
                startService(intent);
                break;
            case R.id.action_add:
                addCityAction();
                break;
            case R.id.action_alarm:
                UpdateAlarmManager updateAlarmManager = new UpdateAlarmManager(this);

                if (settings.getBoolean(ALARM, false)) {
                    updateAlarmManager.cancelAlarm();
                    settings.edit().putBoolean(ALARM, false).apply();
                    Toast.makeText(getApplicationContext(), R.string.switch_off, Toast.LENGTH_SHORT).show();
                } else {
                    updateAlarmManager.setAlarm();
                    settings.edit().putBoolean(ALARM, true).apply();
                    Toast.makeText(getApplicationContext(), R.string.switch_on, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addCityAction() {
        final EditText taskDescription = new EditText(this);
        final Intent intent = new Intent(this, WeatherService.class);

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ThemeDialogCustom))
                .setTitle(R.string.add_city)
                .setView(taskDescription)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cityName = taskDescription.getText().toString();
                        String urlString = MainActivity.FORECAST_URL
                                + cityName.trim()
                                + MainActivity.APPID
                                + MainActivity.API_KEY;

                        intent.setAction(WeatherService.FORECAST).putExtra(WeatherService.FORECAST_URL, urlString);
                        startService(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
