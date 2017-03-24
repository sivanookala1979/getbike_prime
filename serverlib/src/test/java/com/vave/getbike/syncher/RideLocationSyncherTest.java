package com.vave.getbike.syncher;

import com.vave.getbike.android.AndroidStubsFactory;
import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.model.Ride;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by sivanookala on 01/11/16.
 */

public class RideLocationSyncherTest {

    RideLocationSyncher sut;

    @Test
    public void storePendingLocationsTESTHappyFlow() {
        RideSyncher rideSyncher = new RideSyncher();
        Ride ride = rideSyncher.requestRide(24.56, 24.57);
        rideSyncher.acceptRide(ride.getId());
        RideLocationDataSource dataSource = new RideLocationDataSource(null);
        dataSource.setUpdataSource();
        dataSource.clearAll();
        dataSource.insert(ride.getId(), new Date(), 21.98, 28.65, false);
        dataSource.insert(ride.getId(), new Date(), 21.986, 28.655, false);
        sut.setDataSource(dataSource);
        boolean actual = sut.storePendingLocations(ride.getId());
        assertTrue(actual);
        dataSource.close();
    }

    @Before
    public void setUp() {
        sut = new RideLocationSyncher();
        BaseSyncher.testSetup();
        AndroidStubsFactory.IS_TEST = true;
        Long previousRideId = new LoginSyncher().getCurrentRide("sss").getRideId();
        if (previousRideId != null) {
            new RideSyncher().closeRide(previousRideId);
        }
    }

    @After
    public void tearDown() {
        sut = null;
    }

}
