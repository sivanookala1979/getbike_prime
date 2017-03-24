package com.vave.getbike.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.vave.getbike.R;
import com.vave.getbike.adapter.ProfilePagerAdapter;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.model.Profile;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsht on 30/11/16.
 */

public class RiderProfileActivity extends BaseActivity {

    TabLayout mPagerSlidingTabStrip;
    ViewPager mViewPager;
    List<String> statusList = new ArrayList<String>();
    LoginSyncher loginSyncher = new LoginSyncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile);
        GetBikePreferences.setPreferences(getApplicationContext());
        addToolbarView();
        mPagerSlidingTabStrip = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        statusList.add("YOUR VEHICLE");
        statusList.add("YOUR DRIVING LICENCE");

        getProfileDetails();
    }

    private void getProfileDetails() {
        new GetBikeAsyncTask(RiderProfileActivity.this) {

            @Override
            public void process() {
                Profile publicProfile = loginSyncher.getPublicProfile(0l);
                if (publicProfile != null) {
                    GetBikePreferences.setPublicProfile(publicProfile);
                }
            }

            @Override
            public void afterPostExecute() {
                createTabView();
            }
        }.execute();
    }

    private void createTabView() {
        ProfilePagerAdapter mPagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), statusList);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerSlidingTabStrip.setupWithViewPager(mViewPager);
        for (int i = 0; i < mPagerSlidingTabStrip.getTabCount(); i++) {
            mPagerSlidingTabStrip.getTabAt(i).setText(statusList.get(i));
        }
    }
}
