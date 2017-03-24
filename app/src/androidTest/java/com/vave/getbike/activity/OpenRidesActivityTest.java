package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenRidesActivityTest {

    @Rule
    public ActivityTestRule<OpenRidesActivity> mActivityTestRule = new ActivityTestRule<>(OpenRidesActivity.class);

    @Test
    public void testInitialLaunch() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        RideSyncher rideSyncher = new RideSyncher();
        rideSyncher.requestRide(21.34, 54.67, "Cash");
        Intent intent = new Intent(targetContext, OpenRidesActivity.class);
        mActivityTestRule.launchActivity(intent);
        onData(anything()).inAdapterView(withId(R.id.openRides)).atPosition(0).perform(click());
        onView(withId(R.id.rideRequestLatLng)).check(matches(withText("21.34,54.67")));
        onView(withId(R.id.rideRequestedBy)).check(matches(withText("Siva Nookala")));
        onView(withId(R.id.rideRequestMobileNumber)).check(matches(withText("9949287789")));
    }

    @Test
    public void testRefreshTESTHappyFlow() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OpenRidesActivity.class);
        mActivityTestRule.launchActivity(intent);
        RideSyncher rideSyncher = new RideSyncher();
        rideSyncher.requestRide(21.34, 54.67, "Cash");
        onView(withId(R.id.refreshOpenRides)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.openRides)).atPosition(0).perform(click());
        onView(withId(R.id.rideRequestLatLng)).check(matches(withText("21.34,54.67")));
        onView(withId(R.id.rideRequestedBy)).check(matches(withText("Siva Nookala")));
        onView(withId(R.id.rideRequestMobileNumber)).check(matches(withText("9949287789")));
    }
}
