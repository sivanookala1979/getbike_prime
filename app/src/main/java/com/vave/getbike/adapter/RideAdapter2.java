package com.vave.getbike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.Ride;

import java.util.List;

/**
 * Created by RAM on 11/26/2016.
 */

public class RideAdapter2 extends BaseAdapter {

    Context context;
    private List<Ride> searchArrayList;
    private LayoutInflater mInflater;
    private boolean showAcceptRideTextView = true;

    public RideAdapter2(Context context, List<Ride> results, boolean value) {
        searchArrayList = results;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        showAcceptRideTextView = value;
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String indianRupee = context.getResources().getString(R.string.Rs);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ride_history_item, null);
            holder = new ViewHolder();
            holder.rideId = (TextView) convertView.findViewById(R.id.ride_id);
            holder.fromAddress = (TextView) convertView.findViewById(R.id.fromAddress);
            holder.toAddress = (TextView) convertView.findViewById(R.id.toAddress);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.acceptRideTextViewInOpenRides = (TextView) convertView.findViewById(R.id.acceptRideTextView);
            holder.rideDateTime = (TextView) convertView.findViewById(R.id.rideDateTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Ride ride = searchArrayList.get(position);
        holder.rideId.setText("Trip ID : " + ride.getId());
        if (ride.getTotalBill() != null) {
            holder.price.setText(indianRupee + ride.getTotalBill());
        } else {
            holder.price.setText("");
        }
        if (showAcceptRideTextView) {
            holder.acceptRideTextViewInOpenRides.setVisibility(View.VISIBLE);
        }
        holder.fromAddress.setText(ride.getSourceAddress());
        holder.toAddress.setText(ride.getDestinationAddress());
        holder.rideDateTime.setText(ride.getRequestedAt() + "");
        return convertView;
    }

    static class ViewHolder {

        TextView rideId;
        TextView fromAddress;
        TextView toAddress;
        TextView price;
        TextView rideDateTime;
        TextView acceptRideTextViewInOpenRides;

    }
}
