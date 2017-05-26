package com.vave.getbike.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.GroupRideDetails;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;

import java.util.List;

/**
 * Created by RAM on 11/26/2016.
 */

public class GroupRideDetailsAdapter extends BaseAdapter {

    Context context;
    private List<Ride> rideDetailsList;
    private LayoutInflater mInflater;

    public GroupRideDetailsAdapter(Context context, List<Ride> rideDetailsList) {
        this.context = context;
        this.rideDetailsList = rideDetailsList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return rideDetailsList.size();
    }

    public Object getItem(int position) {
        return rideDetailsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_ride_details_item, null);
            holder = new ViewHolder();
            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#f2f2f2"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.parcelsCount = (TextView) convertView.findViewById(R.id.parcelsCount);
            holder.pickupAddress = (TextView) convertView.findViewById(R.id.pickupAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Ride rideDetails = rideDetailsList.get(position);
        if(rideDetails!=null) {
            holder.parcelsCount.setText(rideDetails.getNumberOfRides()+"");
            holder.pickupAddress.setText(rideDetails.getSourceAddress());
        }

        return convertView;
    }

    static class ViewHolder {

        TextView parcelsCount;
        TextView pickupAddress;
    }
}
