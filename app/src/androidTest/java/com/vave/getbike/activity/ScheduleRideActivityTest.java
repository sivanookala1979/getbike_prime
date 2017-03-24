package com.vave.getbike.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.syncher.BaseSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScheduleRideActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<ScheduleRideActivity> mActivityTestRule = new ActivityTestRule<>(ScheduleRideActivity.class);

    @Test
    public void testUpdateProfile() {
        BaseSyncher.testSetup();
        onView(withId(R.id.fromAddress)).perform(typeText("kava"), closeSoftKeyboard());
        onView(withText("Kavali, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.toAddress)).perform(typeText("nell"), closeSoftKeyboard());
        onView(withText("Nellore, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
    }
}
