package com.vave.getbike.activity.suite;

import com.vave.getbike.activity.AcceptRejectRideActivityTest;
import com.vave.getbike.activity.BankAccountDetailsActivityTest;
import com.vave.getbike.activity.GetBikeWalletHomeTest;
import com.vave.getbike.activity.GiveRideTakeRideActivityTest;
import com.vave.getbike.activity.MyCompletedRidesActivityTest;
import com.vave.getbike.activity.OpenRidesActivityTest;
import com.vave.getbike.activity.ProfileAndSettingsActivityTest;
import com.vave.getbike.activity.RedeemAmountActivityTest;
import com.vave.getbike.activity.RidesGivenByMeActivityTest;
import com.vave.getbike.activity.ScheduleRideActivityTest;
import com.vave.getbike.activity.ShowCompletedRideActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sivanookala on 27/11/16.
 */

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AcceptRejectRideActivityTest.class,
        BankAccountDetailsActivityTest.class,
        GetBikeWalletHomeTest.class,
        GiveRideTakeRideActivityTest.class,
        MyCompletedRidesActivityTest.class,
        OpenRidesActivityTest.class,
        ProfileAndSettingsActivityTest.class,
        RedeemAmountActivityTest.class,
        RidesGivenByMeActivityTest.class,
        ScheduleRideActivityTest.class,
        ShowCompletedRideActivityTest.class,
//        WaitForRiderAfterAcceptanceActivityTest.class,
//        WaitForRiderAllocationActivityTest.class
})
public class SecondaryTestsSuite {

}
