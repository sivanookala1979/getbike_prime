package com.vave.getbike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;

import java.util.List;

/**
 * Created by RAM on 11/26/2016.
 */

public class GroupRideRouteAdapter extends BaseAdapter {

    Context context;
    private List<RideLocation> rideLocationsList;
    private LayoutInflater mInflater;

    public GroupRideRouteAdapter(Context context, List<RideLocation> rideLocationsList) {
        this.context = context;
        this.rideLocationsList = rideLocationsList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return rideLocationsList.size();
    }

    public Object getItem(int position) {
        return rideLocationsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_ride_route_item, null);
            holder = new ViewHolder();
            holder.rideId = (TextView) convertView.findViewById(R.id.ride_id);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.locationImage = (ImageView) convertView.findViewById(R.id.location_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RideLocation rideLocation = rideLocationsList.get(position);
        if(rideLocation.isSourse()) {
            holder.rideId.setText("PickUp ID : "+rideLocation.getRideId());
            holder.address.setText(rideLocation.getSourseAddress());
            holder.locationImage.setImageResource(R.drawable.pick_location_to_icon);
        }else{
            holder.rideId.setText("Drop ID : "+rideLocation.getRideId());
            holder.address.setText(rideLocation.getDestinationAddress());
            holder.locationImage.setImageResource(R.drawable.pick_up_location_from_icon);
        }
        return convertView;
    }

    static class ViewHolder {

        TextView rideId, address;
        ImageView locationImage;
    }
}
