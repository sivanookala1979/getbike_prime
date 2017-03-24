package com.vave.getbike.datasource;

import com.vave.getbike.model.RideLocation;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sivanookala on 26/10/16.
 */

public class RideLocationDataSourceTest extends BaseDataSourceTest {

    @Test
    public void insertTESTHappyFlow() {
        RideLocationDataSource sut = new RideLocationDataSource(null);
        sut.setUpdataSource();
        sut.clearAll();
        long rideId = 234L;
        int countBefore = sut.getRideLocations(rideId).size();
        sut.insert(rideId, new Date(), 21.45, 82.54, false);
        List<RideLocation> rideLocations = sut.getRideLocations(rideId);
        assertEquals(countBefore + 1, rideLocations.size());
        RideLocation newRideLocation = rideLocations.get(countBefore);
        assertTrue(newRideLocation.getId() > 0);
        assertEquals(rideId, newRideLocation.getRideId().longValue());
        assertEquals(21.45, newRideLocation.getLatitude());
        assertEquals(82.54, newRideLocation.getLongitude());
        assertEquals(false, newRideLocation.getPosted().booleanValue());
    }

    @Test
    public void markAsPostedTESTHappyFlow() {
        RideLocationDataSource sut = new RideLocationDataSource(null);
        sut.setUpdataSource();
        sut.clearAll();
        long rideId = 234L;
        int countBefore = sut.getRideLocations(rideId).size();
        sut.insert(rideId, new Date(), 21.45, 82.54, false);
        List<RideLocation> rideLocations = sut.getRideLocations(rideId);
        RideLocation newRideLocation = rideLocations.get(countBefore);
        assertFalse(newRideLocation.getPosted());
        sut.markAsPosted(newRideLocation.getId());
        rideLocations = sut.getRideLocations(rideId);
        RideLocation rideLocationAfterUpdate = rideLocations.get(countBefore);
        assertTrue(rideLocationAfterUpdate.getPosted());
    }

    @Test
    public void getPendingRideLocationsTESTHappyFlow() {
        RideLocationDataSource sut = new RideLocationDataSource(null);
        sut.setUpdataSource();
        sut.clearAll();
        long rideId = 234L;
        assertEquals(0, sut.getPendingRideLocations(rideId).size());
        sut.insert(rideId, new Date(), 21.45, 82.54, false);
        assertEquals(1, sut.getPendingRideLocations(rideId).size());
        RideLocation newRideLocation = sut.getRideLocations(rideId).get(0);
        sut.markAsPosted(newRideLocation.getId());
        assertEquals(0, sut.getPendingRideLocations(rideId).size());
    }

}
