package com.vave.getbike.activity;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.RideSyncher;
import com.vave.getbike.utils.SMSIdlingResource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
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
public class LoginActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    public void loginTESTHappyFlow_OldTest() {
        SMSIdlingResource smsIdlingResource = new SMSIdlingResource();
        mActivityTestRule.getActivity().setSmsListener(smsIdlingResource);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        Espresso.registerIdlingResources(smsIdlingResource);

        onView(withId(R.id.mobile))
                .perform(typeText("9949287789"), closeSoftKeyboard());
        onView(withId(R.id.send_otp)).perform(click());
        smsIdlingResource.waitForSms();
        onView(withId(R.id.received_otp)).check(matches(isPositive()));
        onView(withId(R.id.login)).perform(click());
        //onView(withId(R.id.requestRide)).perform(click());
        onView(withId(R.id.getBikeResult)).check(matches(isPositive()));
        //onView(withId(R.id.startRide)).perform(click());
        onView(withId(R.id.start_updates_button)).perform(click());
        SystemClock.sleep(20000);
        onView(withId(R.id.stop_updates_button)).perform(click());
        onView(withId(R.id.totalDistance)).check(matches(isPositive()));
        SystemClock.sleep(10000);

    }

    public void loginTESTHappyFlow() {
        SMSIdlingResource smsIdlingResource = new SMSIdlingResource();
        mActivityTestRule.getActivity().setSmsListener(smsIdlingResource);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        Espresso.registerIdlingResources(smsIdlingResource);

        onView(withId(R.id.mobile))
                .perform(typeText("9949287789"), closeSoftKeyboard());
        onView(withId(R.id.send_otp)).perform(click());
        smsIdlingResource.waitForSms();
        onView(withId(R.id.received_otp)).check(matches(isPositive()));
        onView(withId(R.id.login)).perform(click());
        Espresso.unregisterIdlingResources(smsIdlingResource);
    }

    @Test
    public void loginTESTOpenRidesActivity() {
        SMSIdlingResource smsIdlingResource = new SMSIdlingResource();
        mActivityTestRule.getActivity().setSmsListener(smsIdlingResource);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        Espresso.registerIdlingResources(smsIdlingResource);

        onView(withId(R.id.mobile))
                .perform(typeText("9949287789"), closeSoftKeyboard());
        onView(withId(R.id.send_otp)).perform(click());
        smsIdlingResource.waitForSms();
        onView(withId(R.id.received_otp)).check(matches(isPositive()));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.giveRide)).check(matches(isDisplayed()));
        Espresso.unregisterIdlingResources(smsIdlingResource);
    }

}
