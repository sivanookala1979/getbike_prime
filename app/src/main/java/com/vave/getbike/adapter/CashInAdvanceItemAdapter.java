package com.vave.getbike.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.vave.getbike.R;
import com.vave.getbike.activity.BaseActivity;
import com.vave.getbike.model.CashInAdvance;

import java.util.List;

/**
 * Created by RAM on 6/5/17.
 */

public class CashInAdvanceItemAdapter extends BaseAdapter {

    Context context;
    private List<CashInAdvance> searchArrayList;
    private LayoutInflater mInflater;

    public CashInAdvanceItemAdapter(Context context, List<CashInAdvance> results) {
        searchArrayList = results;
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cash_in_advance_item, null);
            holder = new ViewHolder();
            holder.requestedAtTextView = (TextView) convertView.findViewById(R.id.requestedAtTextView);
            holder.amountTextView = (TextView) convertView.findViewById(R.id.amountTextView);
            holder.riderDescriptionTextView = (TextView) convertView.findViewById(R.id.riderDescriptionTextView);
            holder.adminDescriptionTextView = (TextView) convertView.findViewById(R.id.adminDescriptionTextView);
            holder.requestStatusTextView = (TextView) convertView.findViewById(R.id.requestStatusTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CashInAdvance cashInAdvance = searchArrayList.get(position);
        holder.requestedAtTextView.setText(cashInAdvance.getRequestedAt().toString());
        holder.amountTextView.setText(cashInAdvance.getAmount().toString());
        holder.riderDescriptionTextView.setText(cashInAdvance.getRiderDescription());
        holder.adminDescriptionTextView.setText(cashInAdvance.getAdminDescription());
        if (cashInAdvance.getRequestStatus() == null) {
            holder.requestStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.requestStatusTextView.setText("PENDING");
        } else if (!cashInAdvance.getRequestStatus()) {
            holder.requestStatusTextView.setText("REJECTED");
            holder.requestStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.red));
        } else if (cashInAdvance.getRequestStatus()){
            holder.requestStatusTextView.setText("ACCEPTED");
            holder.requestStatusTextView.setTextColor(ContextCompat.getColor(context,R.color.thick_green));
        } else {
            holder.requestStatusTextView.setText("NA");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView requestedAtTextView;
        TextView amountTextView;
        TextView riderDescriptionTextView;
        TextView adminDescriptionTextView;
        TextView requestStatusTextView;
    }

}
