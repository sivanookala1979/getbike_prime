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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * Created by sivanookala on 01/11/16.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class BankAccountDetailsActivityTest extends BaseGetBikeActivityTest {

    @Rule
    public ActivityTestRule<BankAccountDetailsActivity> mActivityTestRule = new ActivityTestRule<>(BankAccountDetailsActivity.class);

    @Test
    public void testUpdateBankAccountDetailsHappyFlow() {
        BaseSyncher.testSetup();
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, MyCompletedRidesActivity.class);
        mActivityTestRule.launchActivity(intent);
        onView(withId(R.id.accountHolderName)).perform(clearText()).perform(typeText("Siva Nookala"), closeSoftKeyboard());
        onView(withId(R.id.accountNumber)).perform(clearText()).perform(typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.ifscCode)).perform(clearText()).perform(typeText("IFC120003"), closeSoftKeyboard());
        onView(withId(R.id.bankName)).perform(clearText()).perform(typeText("Axis"), closeSoftKeyboard());
        onView(withId(R.id.branchName)).perform(clearText()).perform(typeText("KAVALI BRANCH"), closeSoftKeyboard());
        onView(withId(R.id.updateBankDetails)).perform(click());
        assertTrue(mActivityTestRule.getActivity().isFinishing());
    }

    @Test
    public void testUpdateBankAccountDetailsWithEmptyAccountHolderName() {
        BaseSyncher.testSetup();
        onView(withId(R.id.accountNumber)).perform(clearText()).perform(typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.ifscCode)).perform(clearText()).perform(typeText("IFC120003"), closeSoftKeyboard());
        onView(withId(R.id.bankName)).perform(clearText()).perform(typeText("Axis"), closeSoftKeyboard());
        onView(withId(R.id.branchName)).perform(clearText()).perform(typeText("KAVALI BRANCH"), closeSoftKeyboard());
        onView(withId(R.id.accountHolderName)).perform(clearText());
        onView(withId(R.id.updateBankDetails)).perform(click());
        assertToast("Please enter valid account holder name", mActivityTestRule);
    }

    @Test
    public void testUpdateBankAccountDetailsWithEmptyBankName() {
        BaseSyncher.testSetup();
        onView(withId(R.id.accountHolderName)).perform(clearText()).perform(typeText("Adarsh T"), closeSoftKeyboard());
        onView(withId(R.id.accountNumber)).perform(clearText()).perform(typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.ifscCode)).perform(clearText()).perform(typeText("IFC120003"), closeSoftKeyboard());
        onView(withId(R.id.bankName)).perform(clearText());
        onView(withId(R.id.branchName)).perform(clearText()).perform(typeText("KAVALI BRANCH"), closeSoftKeyboard());
        onView(withId(R.id.updateBankDetails)).perform(click());
        assertToast("Please enter valid bank name", mActivityTestRule);
    }
}
