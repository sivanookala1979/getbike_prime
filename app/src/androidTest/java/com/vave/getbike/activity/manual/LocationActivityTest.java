package com.vave.getbike.activity.manual;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.activity.LocationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LocationActivityTest {

    @Rule
    public ActivityTestRule<LocationActivity> mActivityTestRule = new ActivityTestRule<>(LocationActivity.class);

    @Test
    public void locationTESTHappyFlow() {
//        onView(withId(R.id.requestRide)).perform(click());
//        onView(withId(R.id.requestRide)).perform(click());
//        onView(withId(R.id.getBikeResult)).check(matches(isPositive()));

        //onView(withId(R.id.startRide)).perform(click());
        onView(withId(R.id.start_updates_button)).perform(click());
        SystemClock.sleep(200000);
//        onView(withId(R.id.stop_updates_button)).perform(click());
//        SystemClock.sleep(20000);
    }

}
