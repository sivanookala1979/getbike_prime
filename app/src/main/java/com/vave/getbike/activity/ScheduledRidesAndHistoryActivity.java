package com.vave.getbike.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.vave.getbike.R;
import com.vave.getbike.adapter.HistoryPagerAdapter;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.List;

public class ScheduledRidesAndHistoryActivity extends BaseActivity {

    TabLayout mPagerSlidingTabStrip;
    ViewPager mViewPager;
    List<String> statusList = new ArrayList<String>();
    LoginSyncher loginSyncher = new LoginSyncher();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_rides_and_history);
        addToolbarView();
        mPagerSlidingTabStrip = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        statusList.add("Rides Taken");
        statusList.add("Rides Given");
        createTabView();
    }



    private void createTabView() {
        HistoryPagerAdapter mPagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager(), statusList);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerSlidingTabStrip.setupWithViewPager(mViewPager);
        for (int i = 0; i < mPagerSlidingTabStrip.getTabCount(); i++) {
            mPagerSlidingTabStrip.getTabAt(i).setText(statusList.get(i));
        }
    }
}
