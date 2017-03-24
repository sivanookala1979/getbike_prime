package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.test.InstrumentationRegistry;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AcceptRejectRideActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<AcceptRejectRideActivity> mActivityTestRule = new ActivityTestRule<>(AcceptRejectRideActivity.class);

    @Test
    public void clickOnAcceptRide() {
        BaseSyncher.testSetup();
        closeCurrentRide();
        setupMockLocation((LocationManager) mActivityTestRule.getActivity().getSystemService(Context.LOCATION_SERVICE));
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Kavali", "Ongole", "Cash");
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", ride.getId());
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.rideRequestLatLng)).check(matches(withText("21.34,54.67")));
        onView(withId(R.id.rideRequestedBy)).check(matches(withText("Siva Nookala")));
        onView(withId(R.id.rideRequestMobileNumber)).check(matches(withText("9949287789")));
        onView(withId(R.id.rideRequestAddress)).check(matches(withText("Kavali")));
        onView(withId(R.id.rideDestinationAddress)).check(matches(withText("Ongole")));
        onView(withId(R.id.acceptRide)).perform(click());
        onView(withId(R.id.reached_customer_button)).perform(click());
        onView(withId(R.id.start_updates_button)).check(matches(isDisplayed()));
        cleanUpOpenRidesActivity();
    }

    @Test
    public void clickOnRejectRide() {
        BaseSyncher.testSetup();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Kavali", "Ongole", "Cash");

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", ride.getId());

        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.rideRequestLatLng)).check(matches(withText("21.34,54.67")));
        onView(withId(R.id.rideRequestedBy)).check(matches(withText("Siva Nookala")));
        onView(withId(R.id.rideRequestMobileNumber)).check(matches(withText("9949287789")));
        onView(withId(R.id.rideRequestAddress)).check(matches(withText("Kavali")));
        onView(withId(R.id.rideDestinationAddress)).check(matches(withText("Ongole")));
        onView(withId(R.id.rejectRide)).perform(click());
        assertTrue(mActivityTestRule.getActivity().isFinishing());
        cleanUpOpenRidesActivity();
    }

    @Test
    public void withValidRide() {
        BaseSyncher.testSetup();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(21.34, 54.67, "Kavali", "Ongole", "Cash");

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", ride.getId());
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.rideRequestLatLng)).check(matches(withText("21.34,54.67")));
        onView(withId(R.id.rideRequestedBy)).check(matches(withText("Siva Nookala")));
        onView(withId(R.id.rideRequestMobileNumber)).check(matches(withText("9949287789")));
        onView(withId(R.id.rideRequestAddress)).check(matches(withText("Kavali")));
        onView(withId(R.id.rideDestinationAddress)).check(matches(withText("Ongole")));
        cleanUpOpenRidesActivity();
    }

    @Test
    public void withInvalidRideId() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, AcceptRejectRideActivity.class);
        intent.putExtra("rideId", 3726262l);
        mActivityTestRule.launchActivity(intent);
        assertToast(R.string.error_ride_is_not_valid, mActivityTestRule);
        cleanUpOpenRidesActivity();
    }

    public void cleanUpOpenRidesActivity() {
        getActivityInstance().finish();
        compulsoryWait(2000);
    }

}
