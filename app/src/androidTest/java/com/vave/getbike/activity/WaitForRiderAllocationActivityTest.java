package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class WaitForRiderAllocationActivityTest {

    @Rule
    public ActivityTestRule<WaitForRiderAllocationActivity> mActivityTestRule = new ActivityTestRule<>(WaitForRiderAllocationActivity.class);

    @Test
    public void testInitialLaunch() {
        BaseSyncher.testSetup();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Cash");
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", ride.getId());
        Ride rideFromServer = rideSyncher.getRideById(ride.getId());

        mActivityTestRule.launchActivity(intent);
//        onView(withId(R.id.generatedRideId)).check(matches(withText(rideFromServer.getId() + "")));
//        onView(withId(R.id.rideRequestedAt)).check(matches(withText(rideFromServer.getRequestedAt() + "")));
        onView(withId(R.id.allottedRiderDetails)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testAfterAccept() {
        BaseSyncher.testSetup();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Cash");
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", ride.getId());
        Ride rideFromServer = rideSyncher.getRideById(ride.getId());
        rideSyncher.acceptRide(ride.getId());
        mActivityTestRule.launchActivity(intent);
        mActivityTestRule.getActivity().instance().rideAccepted(ride.getId());
        Ride rideFromServerAfterAccepted = rideSyncher.getRideById(ride.getId());
        onView(withId(R.id.allottedRiderDetails)).check(matches(withText(rideFromServerAfterAccepted.getRiderId() + "")));
    }

}
