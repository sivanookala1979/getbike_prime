package com.vave.getbike.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.model.WalletEntry;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by RAM on 11/26/2016.
 */

public class WalletAdapter extends BaseAdapter {

    Context context;
    private List<WalletEntry> searchArrayList;
    private LayoutInflater mInflater;

    public WalletAdapter(Context context, List<WalletEntry> results) {
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
            convertView = mInflater.inflate(R.layout.wallet_history_item, null);
            holder = new ViewHolder();
            holder.walletEntryDate = (TextView) convertView.findViewById(R.id.walletEntryDate);
            holder.walletEntryType = (TextView) convertView.findViewById(R.id.walletEntryType);
            holder.walletEntryAmount = (TextView) convertView.findViewById(R.id.walletEntryAmount);
            holder.walletEntryDescription = (TextView) convertView.findViewById(R.id.walletEntryDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WalletEntry ride = searchArrayList.get(position);
        holder.walletEntryAmount.setText(ride.getAmount() + "");
        if (ride.getAmount() >= 0) {
            holder.walletEntryAmount.setTextColor(ContextCompat.getColor(context, R.color.thick_green));
        } else {
            holder.walletEntryAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        holder.walletEntryType.setText(ride.getType());
        holder.walletEntryDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(ride.getTransactionDateTime()));
        holder.walletEntryDescription.setText(ride.getDescription());
        return convertView;
    }

    static class ViewHolder {

        TextView walletEntryDate;
        TextView walletEntryType;
        TextView walletEntryAmount;
        TextView walletEntryDescription;
    }
}
