package com.vave.getbike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vave.getbike.fragments.HistoryFragment;

import java.util.List;

/**
 * Created by ram on 12/13/2016.
 */

public class HistoryPagerAdapter extends FragmentPagerAdapter {
    List<String> statusList;

    public HistoryPagerAdapter(FragmentManager fm, List<String> statusList) {
        super(fm);
        this.statusList = statusList;
    }

    @Override
    public Fragment getItem(int position) {
        return HistoryFragment.newInstance(position, (String)getPageTitle(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return statusList.get(position);
    }

    @Override
    public int getCount() {
        return statusList.size();
    }
}
