package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideLocationSyncher;
import com.vave.getbike.syncher.RideSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vave.getbike.utils.GetBikeTestUtils.isPositive;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShowCompletedRideActivityTest {

    @Rule
    public ActivityTestRule<ShowCompletedRideActivity> mActivityTestRule = new ActivityTestRule<>(ShowCompletedRideActivity.class);

    @Test
    public void testInitialLaunch() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Ride ride = setupCompleteRide(targetContext);
        Intent intent = new Intent(targetContext, ShowCompletedRideActivity.class);
        intent.putExtra("rideId", ride.getId());
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.tripId)).check(matches(withText("Trip ID : " + ride.getId())));
        onView(withId(R.id.fromAddress)).check(matches(withText("Source Not Provided")));
    }

    @NonNull
    public static Ride setupCompleteRide(Context targetContext) {
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(24.56, 24.57, "Cash");
        rideSyncher.acceptRide(ride.getId());
        RideLocationDataSource dataSource = new RideLocationDataSource(targetContext);
        dataSource.setUpdataSource();
        dataSource.clearAll();
        for (int i = 0; i < LAT_LONGS.length; i += 2) {
            dataSource.insert(ride.getId(), new Date(), LAT_LONGS[i], LAT_LONGS[i + 1], false);
        }
        RideLocationSyncher locationSyncher = new RideLocationSyncher();
        locationSyncher.setDataSource(dataSource);
        locationSyncher.storePendingLocations(ride.getId());
        rideSyncher.closeRide(ride.getId());
        return ride;
    }

    public static double LAT_LONGS[] = {14.9026234, 79.9940092,
            14.9026312, 79.9940611,
            14.9026337, 79.9940604,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9025803, 79.9940529,
            14.9026445, 79.9939145,
            14.9027952, 79.9939259,
            14.9027952, 79.9939259,
            14.9029727, 79.993934,
            14.9027644, 79.9941403,
            14.9027644, 79.9941403,
            14.9025803, 79.9940529,
            14.9025803, 79.9940529,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092,
            14.9026234, 79.9940092
    };

}
