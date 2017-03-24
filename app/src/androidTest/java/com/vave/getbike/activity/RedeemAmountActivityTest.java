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
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by adarsht on 06/12/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RedeemAmountActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<RedeemAmountActivity> mActivityTestRule = new ActivityTestRule<>(RedeemAmountActivity.class);

    @Test
    public void testRechargeMobileHappyFlow() {
        BaseSyncher.testSetup();
        onView(withId(R.id.rechargeMobile)).perform(click());
        onView(withId(R.id.rechargeMobileNumber))
                .perform(typeText("9949257729"), closeSoftKeyboard());
        onView(withId(R.id.rechargeAmount))
                .perform(typeText("50"), closeSoftKeyboard());
        onView(withId(R.id.rechargeSubmit)).perform(click());
        assertToast("Recharge successful", mActivityTestRule);
    }

    @Test
    public void testRechargeMobileWithEmptyAmount() {
        BaseSyncher.testSetup();
        onView(withId(R.id.rechargeMobile)).perform(click());
        onView(withId(R.id.rechargeMobileNumber))
                .perform(typeText("9949257729"), closeSoftKeyboard());
        onView(withId(R.id.rechargeSubmit)).perform(click());
        assertToast("Please enter valid amount", mActivityTestRule);
    }

    @Test
    public void testRedeemWalletHappyFlow() {
        BaseSyncher.testSetup();
        onView(withId(R.id.redeemToWallet)).perform(click());
        onView(withId(R.id.redeemMobileNumber))
                .perform(typeText("9949257729"), closeSoftKeyboard());
        onView(withId(R.id.redeemAmount))
                .perform(typeText("20.0"), closeSoftKeyboard());
        onView(withId(R.id.redeemSubmit)).perform(click());
        assertToast("transaction successful.", mActivityTestRule);
    }

    @Test
    public void testRedeemWalletEmptyMobileNumber() {
        BaseSyncher.testSetup();
        onView(withId(R.id.redeemToWallet)).perform(click());
        onView(withId(R.id.redeemAmount))
                .perform(typeText("50.0"), closeSoftKeyboard());
        onView(withId(R.id.redeemSubmit)).perform(click());
        assertToast("Please enter valid mobile number", mActivityTestRule);
    }

    @Test
    public void testRedeemBankHappyFlow() {
        BaseSyncher.testSetup();
        onView(withId(R.id.redeemToBank)).perform(click());
        onView(withId(R.id.bankAmount))
                .perform(typeText("20.0"), closeSoftKeyboard());
        onView(withId(R.id.redeemBankSubmit)).perform(click());
        assertToast("transaction successful.", mActivityTestRule);
    }

    @Test
    public void testRedeemBankInvalidAmount() {
        BaseSyncher.testSetup();
        onView(withId(R.id.redeemToBank)).perform(click());
        onView(withId(R.id.redeemBankSubmit)).perform(click());
        assertToast("Please enter valid amount.", mActivityTestRule);
    }
}
