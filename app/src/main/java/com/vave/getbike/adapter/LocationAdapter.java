package com.vave.getbike.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.RideLocation;

import java.util.List;

/**
 * Created by sivanookala on 26/10/16.
 */

public class LocationAdapter extends ArrayAdapter<RideLocation> {

    public LocationAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public LocationAdapter(Context context, int resource, List<RideLocation> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.location_list_item_row, null);
        }

        RideLocation p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.locationTime);
            TextView tt2 = (TextView) v.findViewById(R.id.latitude);
            TextView tt3 = (TextView) v.findViewById(R.id.longitude);

            if (tt1 != null) {
                tt1.setText(DateUtils.formatDateTime(getContext(), p.getLocationTime().getTime(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE ));
            }

            if (tt2 != null) {
                tt2.setText(p.getLatitude() + "");
            }

            if (tt3 != null) {
                tt3.setText(p.getLongitude() + "");
            }
        }

        return v;
    }

}
