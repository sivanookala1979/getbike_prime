package com.vave.getbike.activity.manual;

import android.app.Activity;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.activity.GiveDestinationAddressActivity;
import com.vave.getbike.activity.LoginActivity;
import com.vave.getbike.activity.LoginActivityTest;
import com.vave.getbike.activity.SplashScreenActivity;
import com.vave.getbike.syncher.RideSyncher;
import com.vave.getbike.utils.SMSIdlingResource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.vave.getbike.activity.BaseGetBikeActivityTest.textCapture;
import static com.vave.getbike.utils.GetBikeTestUtils.isPositive;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ManualHelperTest {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);
    private Activity currentActivity;

    @Test
    public void loginHelper() {
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
        SystemClock.sleep(474747474747l);
    }

    @Test
    public void launchHelper() {
        SystemClock.sleep(SplashScreenActivity.DELAY_MILLIS + 1000);
        simulateTakeRide();

        SystemClock.sleep(474747474747l);
    }

    public void simulateTakeRide() {
        onView(withId(R.id.home)).perform(click());
        onView(withId(R.id.takeRide)).perform(click());
        onView(withId(R.id.yourLocation)).check(matches(withText("Pullareddy Nagar Main Road, Rama Murthy Peta, Kavali, Andhra Pradesh 524201")));
        onView(withId(R.id.destination)).perform(typeText("Musunur"), ViewActions.closeSoftKeyboard());
        GiveDestinationAddressActivity mActivity = (GiveDestinationAddressActivity) getActivityInstance();

        onView(withText("Musunuru, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Musunur, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Tap on a suggestion.
        onView(withText("Musunur, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        // By clicking on the auto complete term, the text should be filled in.
        onView(withId(R.id.destination))
                .check(matches(withText("Musunur, Andhra Pradesh, India")));
        SystemClock.sleep(3000);
        onView(withId(R.id.takeRide)).perform(click());
        AtomicReference<String> rideIdCapture = new AtomicReference<>();
//        onView(withId(R.id.generatedRideId)).check(matches(textCapture(rideIdCapture)));
        RideSyncher rideSyncher = new RideSyncher();
        SystemClock.sleep(3000);
        rideSyncher.acceptRide(Long.parseLong(rideIdCapture.get()));
        SystemClock.sleep(3000);
    }

    private Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                for (Activity act : resumedActivity) {
                    currentActivity = act;
                    break;
                }

            }
        });

        return currentActivity;
    }

}
