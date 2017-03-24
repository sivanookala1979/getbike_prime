package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GetBikeWalletHomeTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<GetBikeWalletHome> mActivityTestRule = new ActivityTestRule<>(GetBikeWalletHome.class);

    @Test
    public void testRechargeMobile() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, GetBikeWalletHome.class);
        mActivityTestRule.launchActivity(intent);
        manualReview(10000);
        onView(withId(R.id.redeem)).perform(click());
        onView(withId(R.id.rechargeMobile)).perform(click());
        onView(withId(R.id.rechargeMobileNumber))
                .perform(typeText("9949257729"), closeSoftKeyboard());
        onView(withId(R.id.rechargeAmount))
                .perform(typeText("50"), closeSoftKeyboard());
        onView(withId(R.id.rechargeSubmit)).perform(click());
        assertToast("Recharge successful", mActivityTestRule);
        onView(withId(R.id.rechargeSubmit)).perform(pressBack());
        manualReview(10000);
    }

    @Test
    public void tesShowHideWalletDetails() {
        BaseSyncher.testSetup();
        onView(withId(R.id.redeem)).perform(click());
        manualReview(1000);
        onView(withId(R.id.rechargeMobile)).perform(click());
        manualReview(1000);
        onView(withId(R.id.rechargeMobile)).perform(click());
        manualReview(1000);
        onView(withId(R.id.redeemToWallet)).perform(click());
        manualReview(1000);
        onView(withId(R.id.redeemToWallet)).perform(click());
        manualReview(1000);
        onView(withId(R.id.redeemToBank)).perform(click());
        manualReview(1000);
        onView(withId(R.id.redeemToBank)).perform(click());
        manualReview(1000);
    }

    @Test
    public void testAddMoneyFromPayU() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, GetBikeWalletHome.class);
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.addMoney)).perform(click());
        onView(withId(R.id.amountRadioButton100)).check(matches(isChecked()));
        onView(withId(R.id.amountRadioButton200)).perform(click());
        onView(withId(R.id.btnPayNow)).perform(click());
        onView(withId(R.id.edit_text_card_number)).perform(typeText("5123456789012346"));
        onView(withId(R.id.edit_text_card_cvv)).perform(typeText("123"));
        onView(withId(R.id.edit_text_expiry_month)).perform(click());
        waitForever();
    }
}
