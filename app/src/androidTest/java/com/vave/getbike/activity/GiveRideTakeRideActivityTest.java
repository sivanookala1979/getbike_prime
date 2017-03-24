package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.vave.getbike.R;
import com.vave.getbike.syncher.BaseSyncher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GiveRideTakeRideActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<GiveRideTakeRideActivity> mActivityTestRule = new ActivityTestRule<>(GiveRideTakeRideActivity.class);

    @Test
    public void testKavaliToMungamur() {
        BaseSyncher.testSetup();
        closeCurrentRide();
        setupMockLocation((LocationManager) mActivityTestRule.getActivity().getSystemService(Context.LOCATION_SERVICE));
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, GiveRideTakeRideActivity.class);
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.takeRide)).perform(click());
        onView(withId(R.id.yourLocation)).check(matches(withText("Pullareddy Nagar Main Road, Rama Murthy Peta, Kavali, Andhra Pradesh 524201")));
        onView(withId(R.id.destination)).perform(typeText("Mungamur"), ViewActions.closeSoftKeyboard());
        GiveRideTakeRideActivity mActivity = mActivityTestRule.getActivity();

        onView(withText("Mungamur, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Mungamur Road, Ongole, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Tap on a suggestion.
        onView(withText("Mungamur, Andhra Pradesh, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        // By clicking on the auto complete term, the text should be filled in.
        onView(withId(R.id.destination))
                .check(matches(withText("Mungamur, Andhra Pradesh, India")));
        manualReview(2000);
        onView(withId(R.id.rideEstimate))
                .check(matches(withText("Estimated ₹ 89.3 for 12.33 km")));
    }

    @Test
    public void testKavaliToHyderabad() {
        BaseSyncher.testSetup();
        closeCurrentRide();

        onView(withId(R.id.takeRide)).perform(click());
        onView(withId(R.id.yourLocation)).check(matches(withText("Pullareddy Nagar Main Road, Rama Murthy Peta, Kavali, Andhra Pradesh 524201")));
        onView(withId(R.id.destination)).perform(typeText("Hyderabad"), ViewActions.closeSoftKeyboard());
        GiveRideTakeRideActivity mActivity = mActivityTestRule.getActivity();

        onView(withText("Hyderabad, Telangana, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Tap on a suggestion.
        onView(withText("Hyderabad, Telangana, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        // By clicking on the auto complete term, the text should be filled in.
        onView(withId(R.id.destination))
                .check(matches(withText("Hyderabad, Telangana, India")));
        manualReview(2000);
        onView(withId(R.id.rideEstimate))
                .check(matches(withText("Estimated ₹ 2515.9 for 358.98 km")));
    }

    @Test
    public void giveRideToKnownUser() {
        BaseSyncher.testSetup();
        closeCurrentRide();

        onView(withId(R.id.giveRide)).perform(click());
        onView(withId(R.id.hail_model_text_view)).perform(click());
        onView(withId(R.id.yourLocation)).check(matches(withText("Pullareddy Nagar Main Road, Rama Murthy Peta, Kavali, Andhra Pradesh 524201")));
        onView(withId(R.id.customerMobileNumber)).perform(typeText("9949257729"));
        onView(withId(R.id.destination)).perform(typeText("Hyderabad"), ViewActions.closeSoftKeyboard());
        GiveRideTakeRideActivity mActivity = mActivityTestRule.getActivity();

        onView(withText("Hyderabad, Telangana, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Tap on a suggestion.
        onView(withText("Hyderabad, Telangana, India"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        // By clicking on the auto complete term, the text should be filled in.
        onView(withId(R.id.destination))
                .check(matches(withText("Hyderabad, Telangana, India")));
        manualReview(2000);
        onView(withId(R.id.rideEstimate))
                .check(matches(withText("Estimated ₹ 2515.9 for 358.98 km")));
        onView(withId(R.id.giveRide)).perform(click());
        AtomicReference<String> rideIdCapture = new AtomicReference<>();
        onView(withId(R.id.tripId)).check(matches(textCapture(rideIdCapture)));
        onView(withId(R.id.start_updates_button)).perform(click());
        manualReview(2000);
        onView(withId(R.id.stop_updates_button)).perform(click());
        onView(withText("STOP TRIP")).check(matches(isDisplayed()));
        onView(withText("Do you want to stop the trip?")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        manualReview(2000);
        onView(withId(R.id.tripId)).check(matches(withText(rideIdCapture.get())));
    }

}
