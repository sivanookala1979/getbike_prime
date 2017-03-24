package com.vave.getbike.activity;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.widget.TextView;

import com.vave.getbike.model.CurrentRideStatus;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivanookala on 13/12/16.
 */

public class BaseGetBikeActivityTest {

    protected Activity currentActivity;

    public static void closeCurrentRide() {
        CurrentRideStatus currentRideStatus = new LoginSyncher().getCurrentRide("xx~xxx~uui");
        if (currentRideStatus != null) {
            new RideSyncher().closeRide(currentRideStatus.getRideId());
        }
    }

    public static Matcher<View> textCapture(final AtomicReference<String> ref) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text: ");
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                ref.set(textView.getText().toString());
                return true;
            }
        };
    }

    protected void assertToast(int errorId, ActivityTestRule mActivityTestRule) {
        onView(withText(errorId)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        compulsoryWait(2000);
    }

    protected void assertToast(String errorMessage, ActivityTestRule mActivityTestRule) {
        onView(withText(errorMessage)).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        compulsoryWait(2000);
    }

    protected Activity getActivityInstance() {
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

    public void manualReview(int milliSeconds) {
        SystemClock.sleep(milliSeconds);
    }

    public void compulsoryWait(int milliSeconds) {
        SystemClock.sleep(milliSeconds);
    }

    public void waitForever() {
        SystemClock.sleep(3828228);
    }

    public void setupMockLocation(LocationManager mLocationManager) {
        try {
            mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        mLocationManager.addTestProvider
                (
                        LocationManager.GPS_PROVIDER,
                        "requiresNetwork" == "",
                        "requiresSatellite" == "",
                        "requiresCell" == "",
                        "hasMonetaryCost" == "",
                        "supportsAltitude" == "",
                        "supportsSpeed" == "",
                        "supportsBearing" == "",

                        android.location.Criteria.POWER_LOW,
                        android.location.Criteria.ACCURACY_FINE
                );

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);

        newLocation.setLatitude(14.90266817);
        newLocation.setLongitude(79.99396721);
//        newLocation.setLatitude(17.4257212708051);
//        newLocation.setLongitude(78.42207347165521);
        newLocation.setTime(new Date().getTime());
        newLocation.setElapsedRealtimeNanos(new Date().getTime());
        newLocation.setAccuracy(500);

        mLocationManager.setTestProviderEnabled
                (
                        LocationManager.GPS_PROVIDER,
                        true
                );

        mLocationManager.setTestProviderStatus
                (
                        LocationManager.GPS_PROVIDER,
                        LocationProvider.AVAILABLE,
                        null,
                        System.currentTimeMillis()
                );

        mLocationManager.setTestProviderLocation
                (
                        LocationManager.GPS_PROVIDER,
                        newLocation
                );
    }
}
