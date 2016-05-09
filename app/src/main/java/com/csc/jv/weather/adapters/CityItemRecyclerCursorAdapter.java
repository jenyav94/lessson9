package com.csc.jv.weather.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csc.jv.weather.R;
import com.csc.jv.weather.fragments.CityListFragment;
import com.csc.jv.weather.model.ForecastItem;
import com.csc.jv.weather.providers.ForecastTable;
import com.csc.jv.weather.providers.WeatherContentProvider;


public class CityItemRecyclerCursorAdapter extends CursorRecyclerViewAdapter<CityItemRecyclerCursorAdapter.ViewHolder> {

    private final CityListFragment.OnListFragmentInteractionListener mListener;

    public CityItemRecyclerCursorAdapter(Context context, Cursor cursor, CityListFragment.OnListFragmentInteractionListener listener) {
        super(context, cursor);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_city_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {

        final ForecastItem forecastItem = ForecastItem.fromCursor(cursor);
        final String _id = cursor.getString(cursor.getColumnIndex(ForecastTable._ID));
        final int position = cursor.getPosition();

        holder.mItem = forecastItem;
        holder.mIdView.setText(forecastItem.city_name);
        holder.mContentView.setText(forecastItem.temperature);
        holder.mUpdateTime.setText(forecastItem.update_time);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(_id, position);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.ThemeDialogCustom))
                        .setTitle(R.string.delete)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.getContentResolver().delete(
                                        Uri.withAppendedPath(WeatherContentProvider.ENTRIES_URI, _id),
                                        null,
                                        null);
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

                return true;
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mUpdateTime;
        public ForecastItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.city_name_in_list);
            mContentView = (TextView) view.findViewById(R.id.temperature_in_list);
            mUpdateTime = (TextView) view.findViewById(R.id.update_time_in_list);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
