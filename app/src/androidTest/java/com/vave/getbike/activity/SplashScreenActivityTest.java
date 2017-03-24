package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.utils.SMSIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

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
public class SplashScreenActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Test
    public void testWithLoginAndLogout() {
        GetBikePreferences.reset();
        SystemClock.sleep(SplashScreenActivity.DELAY_MILLIS + 1000);
        onView(withId(R.id.login_button)).perform(click());
        SMSIdlingResource smsIdlingResource = new SMSIdlingResource();
        ((LoginActivity) getActivityInstance()).setSmsListener(smsIdlingResource);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        Espresso.registerIdlingResources(smsIdlingResource);
        onView(withId(R.id.mobile))
                .perform(typeText("9949287789"), closeSoftKeyboard());
        onView(withId(R.id.send_otp)).perform(click());
        smsIdlingResource.waitForSms();
        onView(withId(R.id.received_otp)).check(matches(isPositive()));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
        Espresso.unregisterIdlingResources(smsIdlingResource);
        GetBikePreferences.reset();
    }

    @Test
    public void testWithLoginOnlyAndRelaunchingTheSplashScreen() {
        GetBikePreferences.reset();
        SystemClock.sleep(SplashScreenActivity.DELAY_MILLIS + 1000);
        onView(withId(R.id.login_button)).perform(click());
        SMSIdlingResource smsIdlingResource = new SMSIdlingResource();
        ((LoginActivity) getActivityInstance()).setSmsListener(smsIdlingResource);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        Espresso.registerIdlingResources(smsIdlingResource);
        onView(withId(R.id.mobile)).perform(typeText("9949287789"), closeSoftKeyboard());
        onView(withId(R.id.send_otp)).perform(click());
        smsIdlingResource.waitForSms();
        onView(withId(R.id.received_otp)).check(matches(isPositive()));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.giveRide)).check(matches(isDisplayed()));
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mActivityTestRule.launchActivity(new Intent(targetContext, SplashScreenActivity.class));
        SystemClock.sleep(SplashScreenActivity.DELAY_MILLIS + 1000);
        onView(withId(R.id.giveRide)).check(matches(isDisplayed()));
        Espresso.unregisterIdlingResources(smsIdlingResource);
    }

    @Before
    public void setUp() {
        GetBikePreferences.reset();
    }

    @After
    public void tearDown() {
        GetBikePreferences.reset();
    }
}
