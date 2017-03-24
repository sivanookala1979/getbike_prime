package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class WaitForRiderAfterAcceptanceActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<WaitForRiderAfterAcceptanceActivity> mActivityTestRule = new ActivityTestRule<>(WaitForRiderAfterAcceptanceActivity.class);

    @Test
    public void testAfterAccept() {
        BaseSyncher.testSetup();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Cash");
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, WaitForRiderAfterAcceptanceActivity.class);
        intent.putExtra("rideId", ride.getId());
        rideSyncher.acceptRide(ride.getId());
        mActivityTestRule.launchActivity(intent);
        manualReview(2000);
    }

}
