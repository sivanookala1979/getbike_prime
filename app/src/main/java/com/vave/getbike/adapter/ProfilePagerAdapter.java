package com.vave.getbike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.vave.getbike.fragments.ProfileFragment;

import java.util.List;

/**
 * Created by adarsht on 30/11/16.
 */

public class ProfilePagerAdapter extends FragmentPagerAdapter{
    List<String> statusList;

    public ProfilePagerAdapter(FragmentManager fm, List<String> statusList) {
        super(fm);
        this.statusList = statusList;
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return statusList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return ProfileFragment.newInstance(position, (String)getPageTitle(position));
    }
}
