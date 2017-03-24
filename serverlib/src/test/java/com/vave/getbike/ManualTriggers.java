package com.vave.getbike;

import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;

/**
 * Created by sivanookala on 30/11/16.
 */

public class ManualTriggers {

    public static void main(String s[]) {
        acceptRide(48);
    }

    public static void acceptRide(long rideId) {
        BaseSyncher.testSetup();
        CallStatus actual = new RideSyncher().acceptRide(rideId);
    }

}
