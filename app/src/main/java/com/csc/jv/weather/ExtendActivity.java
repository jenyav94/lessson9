package com.csc.jv.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.csc.jv.weather.adapters.CursorPagerAdapter;
import com.csc.jv.weather.downloads.WeatherService;
import com.csc.jv.weather.fragments.ExtendCityFragment;
import com.csc.jv.weather.providers.WeatherContentProvider;

public class ExtendActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ExtendCityFragment.OnFragmentInteractionListener {

    private CursorPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private UpdateBroadcastReceiver updateBroadcastReceiver;

    private int cursor_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend);

        Bundle bundle = getIntent().getExtras();
        cursor_position = 0;
        if (bundle != null) {
            cursor_position = bundle.getInt(MainActivity.CURSOR_POSITION);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new CursorPagerAdapter(
                getSupportFragmentManager(),
                null,
                null);


        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);

            // ничего лучше не нашел, как вообще с этим быть???
            viewPager.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager.setCurrentItem(cursor_position);
                }
            }, 100);
//            viewPager.setCurrentItem(cursor_position);
        }

        getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateBroadcastReceiver = new UpdateBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(WeatherService.RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(updateBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(updateBroadcastReceiver);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, WeatherContentProvider.ENTRIES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pagerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pagerAdapter.swapCursor(null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class UpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());

            if (currentFragment != null) {
                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction
                        .detach(currentFragment)
                        .attach(currentFragment)
                        .commit();
            }
        }
    }
}
