package com.vave.getbike.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.LeaveInAdvance;

import java.util.List;

/**
 * Created by vave on 13/5/17.
 */

public class LeaveInAdvanceItemAdapter extends BaseAdapter {

    Context context;
    private List<LeaveInAdvance> searchArrayList;
    private LayoutInflater mInflater;

    public LeaveInAdvanceItemAdapter(Context context, List<LeaveInAdvance> results) {
        searchArrayList = results;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.leave_in_advance_item, null);
            holder = new ViewHolder();
            holder.leaveRequestedAtTextView = (TextView) convertView.findViewById(R.id.leaveRequestedAtTextView);
            holder.datesTextView = (TextView) convertView.findViewById(R.id.datesTextView);
            holder.leaveRiderDescription = (TextView) convertView.findViewById(R.id.leaveRiderDescription);
            holder.leaveAdminDescription = (TextView) convertView.findViewById(R.id.leaveAdminDescription);
            holder.leaveStatusTextView = (TextView) convertView.findViewById(R.id.leaveStatusTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LeaveInAdvance leaveInAdvance = searchArrayList.get(position);
        holder.leaveRequestedAtTextView.setText(leaveInAdvance.getRequestedAt().toString());
        holder.datesTextView.setText(leaveInAdvance.getFromDate()+" TO "+leaveInAdvance.getToDate());
        holder.leaveRiderDescription.setText(leaveInAdvance.getRiderDescription());
        holder.leaveAdminDescription.setText(leaveInAdvance.getAdminDescription());
        if (leaveInAdvance.getRequestStatus() == null){
            holder.leaveStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.leaveStatusTextView.setText("PENDING");
        } else if (!leaveInAdvance.getRequestStatus()) {
            holder.leaveStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.leaveStatusTextView.setText("REJECTED");
        } else if (leaveInAdvance.getRequestStatus()) {
            holder.leaveStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.thick_green));
            holder.leaveStatusTextView.setText("ACCEPTED");
        } else {
            holder.leaveStatusTextView.setText("NA");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView leaveRequestedAtTextView;
        TextView datesTextView;
        TextView leaveRiderDescription;
        TextView leaveAdminDescription;
        TextView leaveStatusTextView;
    }
}
