package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RidesGivenByMeActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<RidesGivenByMeActivity> mActivityTestRule = new ActivityTestRule<>(RidesGivenByMeActivity.class);

    @Test
    public void testWithCompleteRide() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride1 = rideSyncher.requestRide(24.56, 24.57, "Cash");
        compulsoryWait(1000);
        Ride ride2 = rideSyncher.requestRide(24.56, 24.57, "Cash");
        compulsoryWait(1000);
        Ride ride3 = rideSyncher.requestRide(24.56, 24.57, "Cash");
        compulsoryWait(1000);
        rideSyncher.acceptRide(ride2.getId());
        rideSyncher.closeRide(ride2.getId());
        rideSyncher.acceptRide(ride3.getId());
        rideSyncher.closeRide(ride3.getId());
        ShowCompletedRideActivityTest.setupCompleteRide(targetContext);
        Intent intent = new Intent(targetContext, RidesGivenByMeActivity.class);
        mActivityTestRule.launchActivity(intent);
        onData(anything()).inAdapterView(withId(R.id.myCompletedRides)).atPosition(0).perform(click());
        onView(withId(R.id.tripId)).check(matches(isDisplayed()));
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()));
    }

}
