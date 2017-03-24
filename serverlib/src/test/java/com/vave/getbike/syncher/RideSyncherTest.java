package com.vave.getbike.syncher;

import com.vave.getbike.android.AndroidStubsFactory;
import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by sivanookala on 26/10/16.
 */

public class RideSyncherTest {

    RideSyncher sut;

    public static void closeCurrentRide() {
        Long previousRideId = new LoginSyncher().getCurrentRide("wwwww").getRideId();
        if (previousRideId != null) {
            new RideSyncher().closeRide(previousRideId);
        }
    }

    @Test
    public void requestRideTESTHappyFlow() {
        Ride ride = sut.requestRide(54.2122, 98.22111);
        assertNotNull(ride);
        assertTrue(ride.getId() > 0);
    }

    @Test
    public void getRideByIdTESTHappyFlow() {
        Ride ride = sut.requestRide(24.56, 24.57, "Kavali", "Musunur");
        Ride actual = sut.getRideById(ride.getId());
        assertNotNull(actual);
        assertEquals(ride.getId(), actual.getId());
        cAssertRequestorDetails(actual);
        assertEquals(24.56, actual.getStartLatitude());
        assertEquals(24.57, actual.getStartLongitude());
        assertEquals("Kavali", actual.getSourceAddress());
        assertEquals("Musunur", actual.getDestinationAddress());
    }

    @Test
    public void hailCustomerTESTHappyFlow() {
        CallStatus callStatus = sut.hailCustomer(24.56, 24.57, "Kavali", "Musunur", "9383787373", "Jyothi", "jyothi36636@gmail.com", 'F');
        Ride actual = sut.getRideById(callStatus.getId());
        assertNotNull(actual);
        assertEquals(callStatus.getId(), actual.getId());
        assertEquals(24.56, actual.getStartLatitude());
        assertEquals(24.57, actual.getStartLongitude());
        assertEquals("Kavali", actual.getSourceAddress());
        assertEquals("Musunur", actual.getDestinationAddress());
        assertEquals("RideAccepted", actual.getRideStatus());
    }

    @Test
    public void acceptRideTESTHappyFlow() {
        Ride ride = sut.requestRide(24.56, 24.57);
        CallStatus actual = sut.acceptRide(ride.getId());
        assertTrue(actual.isSuccess());
    }

    @Test
    public void closeRideTESTHappyFlow() {
        // Setup
        Ride ride = sut.requestRide(24.56, 24.57);
        sut.acceptRide(ride.getId());
        RideLocationDataSource dataSource = new RideLocationDataSource(null);
        dataSource.setUpdataSource();
        dataSource.clearAll();
        dataSource.insert(ride.getId(), new Date(), 21.98, 28.65, false);
        dataSource.insert(ride.getId(), new Date(), 21.986, 28.655, false);
        RideLocationSyncher locationSyncher = new RideLocationSyncher();
        locationSyncher.setDataSource(dataSource);
        locationSyncher.storePendingLocations(ride.getId());
        // Exercise SUT
        Ride actual = sut.closeRide(ride.getId());
        // Verify
        assertNotNull(actual);
        assertTrue(actual.getOrderDistance() > 0);
    }

    @Test
    public void openRidesTESTHappyFlow() {
        // Setup
        Ride ride1 = sut.requestRide(24.56, 24.57);
        Ride ride2 = sut.requestRide(24.56, 24.57);
        Ride ride3 = sut.requestRide(24.56, 24.57);
        // Exercise SUT
        List<Ride> actual = sut.openRides(24.56, 24.57);
        // Verify
        assertTrue(actual.size() > 3);
        assertEquals(ride3.getId(), actual.get(0).getId());
        assertEquals(ride2.getId(), actual.get(1).getId());
        assertEquals(ride1.getId(), actual.get(2).getId());
        cAssertRequestorDetails(actual.get(0));
        cAssertRequestorDetails(actual.get(1));
        cAssertRequestorDetails(actual.get(2));
    }

    @Test
    public void getMyCompletedRidesTESTHappyFlow() {
        // Setup
        Ride ride1 = sut.requestRide(24.56, 24.57);
        Ride ride2 = sut.requestRide(24.56, 24.57);
        Ride ride3 = sut.requestRide(24.56, 24.57);
        sut.acceptRide(ride2.getId());
        sut.closeRide(ride2.getId());
        sut.acceptRide(ride3.getId());
        sut.closeRide(ride3.getId());
        // Exercise SUT
        List<Ride> actual = sut.getMyCompletedRides();
        // Verify
        assertTrue(actual.size() > 1);
        assertEquals(ride3.getId(), actual.get(0).getId());
        assertEquals(ride2.getId(), actual.get(1).getId());
        cAssertRequestorDetails(actual.get(0));
        cAssertRequestorDetails(actual.get(1));
    }

    @Test
    public void getRidesGivenByMeTESTHappyFlow() {
        // Setup
        Ride ride1 = sut.requestRide(24.56, 24.57);
        Ride ride2 = sut.requestRide(24.56, 24.57);
        Ride ride3 = sut.requestRide(24.56, 24.57);
        sut.acceptRide(ride2.getId());
        sut.closeRide(ride2.getId());
        sut.acceptRide(ride3.getId());
        sut.closeRide(ride3.getId());
        // Exercise SUT
        List<Ride> actual = sut.getRidesGivenByMe();
        // Verify
        assertTrue(actual.size() > 1);
        assertEquals(ride3.getId(), actual.get(0).getId());
        assertEquals(ride2.getId(), actual.get(1).getId());
        cAssertRequestorDetails(actual.get(0));
        cAssertRequestorDetails(actual.get(1));
    }

    @Test
    public void getCompleteRideByIdTESTHappyFlow() {
        // Setup
        Ride ride = sut.requestRide(24.56, 24.57);
        sut.acceptRide(ride.getId());
        RideLocationDataSource dataSource = new RideLocationDataSource(null);
        dataSource.setUpdataSource();
        dataSource.clearAll();
        dataSource.insert(ride.getId(), new Date(), 21.98, 28.65, false);
        dataSource.insert(ride.getId(), new Date(), 21.986, 28.655, false);
        RideLocationSyncher locationSyncher = new RideLocationSyncher();
        locationSyncher.setDataSource(dataSource);
        locationSyncher.storePendingLocations(ride.getId());
        sut.closeRide(ride.getId());
        List<RideLocation> rideLocations = new ArrayList<>();
        // Exercise SUT
        Ride actual = sut.getCompleteRideById(ride.getId(), rideLocations);
        // Verify
        assertNotNull(actual);
        assertTrue(actual.getOrderDistance() > 0);
        int knownNumberOfRideLocations = 2;
        assertEquals(knownNumberOfRideLocations, rideLocations.size());
    }

    @Test
    public void loadNearByRidersTESTHappyFlow() {
        // Setup

        // Exercise SUT
        List<RideLocation> actual = sut.loadNearByRiders(24.56, 24.57);
        // Verify
        int knownNumberOfRiders = 4;
        assertEquals(knownNumberOfRiders, actual.size());
    }

    @Test
    public void estimateRideTESTHappyFlow() {
        // Setup
        ArrayList<RideLocation> rideLocations = new ArrayList<>();
        rideLocations.add(createRideLocation(23.45, 65.45));
        rideLocations.add(createRideLocation(23.46, 65.55));
        rideLocations.add(createRideLocation(23.47, 65.65));
        rideLocations.add(createRideLocation(23.48, 65.75));
        // Exercise SUT
        Ride actual = sut.estimateRide(rideLocations);
        // Verify
        assertEquals(30.77, actual.getOrderDistance());
        assertEquals(218.4, actual.getOrderAmount());
    }

    @Test
    public void estimateRideTESTMusunurToKavali() throws JSONException {
        // Setup
        String actualLocations = "[{\"longitude\":79.9940862,\"latitude\":14.9025192},{\"longitude\":79.99409,\"latitude\":14.90252},{\"longitude\":79.99387,\"latitude\":14.90253},{\"longitude\":79.99355,\"latitude\":14.90257},{\"longitude\":79.99353,\"latitude\":14.90258},{\"longitude\":79.99306,\"latitude\":14.90258},{\"longitude\":79.99299,\"latitude\":14.90258},{\"longitude\":79.9927,\"latitude\":14.90259},{\"longitude\":79.99239,\"latitude\":14.90264},{\"longitude\":79.9923903,\"latitude\":14.9026353},{\"longitude\":79.9923903,\"latitude\":14.9026353},{\"longitude\":79.99239,\"latitude\":14.90264},{\"longitude\":79.99237,\"latitude\":14.90232},{\"longitude\":79.99236,\"latitude\":14.9022},{\"longitude\":79.99234,\"latitude\":14.9018},{\"longitude\":79.99233,\"latitude\":14.9016},{\"longitude\":79.99229,\"latitude\":14.9011},{\"longitude\":79.99228,\"latitude\":14.90096},{\"longitude\":79.99227,\"latitude\":14.90081},{\"longitude\":79.99224,\"latitude\":14.90042},{\"longitude\":79.99224,\"latitude\":14.90033},{\"longitude\":79.99224,\"latitude\":14.90032},{\"longitude\":79.99224,\"latitude\":14.90031},{\"longitude\":79.99224,\"latitude\":14.90029},{\"longitude\":79.9922,\"latitude\":14.89988},{\"longitude\":79.99218,\"latitude\":14.89941},{\"longitude\":79.99214,\"latitude\":14.89884},{\"longitude\":79.99211,\"latitude\":14.89877},{\"longitude\":79.99208,\"latitude\":14.8987},{\"longitude\":79.99206,\"latitude\":14.89832},{\"longitude\":79.992,\"latitude\":14.89752},{\"longitude\":79.99197,\"latitude\":14.89702},{\"longitude\":79.99195,\"latitude\":14.89681},{\"longitude\":79.99194,\"latitude\":14.89659},{\"longitude\":79.99191,\"latitude\":14.89619},{\"longitude\":79.9919,\"latitude\":14.89594},{\"longitude\":79.99177,\"latitude\":14.89409},{\"longitude\":79.99172,\"latitude\":14.89358},{\"longitude\":79.99153,\"latitude\":14.89186},{\"longitude\":79.99132,\"latitude\":14.89015},{\"longitude\":79.99129,\"latitude\":14.88994},{\"longitude\":79.99126,\"latitude\":14.88964},{\"longitude\":79.99121,\"latitude\":14.8894},{\"longitude\":79.99113,\"latitude\":14.88908},{\"longitude\":79.99112,\"latitude\":14.88904},{\"longitude\":79.99095,\"latitude\":14.88849},{\"longitude\":79.99078,\"latitude\":14.88793},{\"longitude\":79.99069,\"latitude\":14.88756},{\"longitude\":79.99067,\"latitude\":14.88752},{\"longitude\":79.99054,\"latitude\":14.88708},{\"longitude\":79.99046,\"latitude\":14.88684},{\"longitude\":79.99026,\"latitude\":14.88617},{\"longitude\":79.9902,\"latitude\":14.88599},{\"longitude\":79.98991,\"latitude\":14.88503},{\"longitude\":79.98985,\"latitude\":14.8848},{\"longitude\":79.98982,\"latitude\":14.88468},{\"longitude\":79.98963,\"latitude\":14.88403},{\"longitude\":79.9896,\"latitude\":14.88392},{\"longitude\":79.98946,\"latitude\":14.88346},{\"longitude\":79.98912,\"latitude\":14.88232},{\"longitude\":79.98875,\"latitude\":14.88117},{\"longitude\":79.98854,\"latitude\":14.88049},{\"longitude\":79.98846,\"latitude\":14.88023},{\"longitude\":79.9884,\"latitude\":14.88005},{\"longitude\":79.98839,\"latitude\":14.88},{\"longitude\":79.98826,\"latitude\":14.87964},{\"longitude\":79.98808,\"latitude\":14.87904},{\"longitude\":79.98808,\"latitude\":14.87903},{\"longitude\":79.9880793,\"latitude\":14.8790314},{\"longitude\":79.9880793,\"latitude\":14.8790314},{\"longitude\":79.98808,\"latitude\":14.87903},{\"longitude\":79.98807,\"latitude\":14.87903},{\"longitude\":79.98768,\"latitude\":14.87914},{\"longitude\":79.98759,\"latitude\":14.87917},{\"longitude\":79.9875,\"latitude\":14.87918},{\"longitude\":79.98717,\"latitude\":14.87921},{\"longitude\":79.98701,\"latitude\":14.87922},{\"longitude\":79.98677,\"latitude\":14.87924},{\"longitude\":79.98673,\"latitude\":14.87925},{\"longitude\":79.98662,\"latitude\":14.87926},{\"longitude\":79.9865,\"latitude\":14.87929},{\"longitude\":79.9863,\"latitude\":14.87936},{\"longitude\":79.98624,\"latitude\":14.87936},{\"longitude\":79.98617,\"latitude\":14.87936},{\"longitude\":79.98601,\"latitude\":14.87934},{\"longitude\":79.98574,\"latitude\":14.87929},{\"longitude\":79.9853,\"latitude\":14.87923},{\"longitude\":79.98464,\"latitude\":14.87915},{\"longitude\":79.98381,\"latitude\":14.87899},{\"longitude\":79.9838054,\"latitude\":14.8789926}]\n";
        ArrayList<RideLocation> rideLocations = new ArrayList<>();
        JSONArray locationsJsonObject = new JSONArray(actualLocations);
        for (int i = 0; i < locationsJsonObject.length(); i++) {
            rideLocations.add(createRideLocation(locationsJsonObject.getJSONObject(i).getDouble("latitude"), locationsJsonObject.getJSONObject(i).getDouble("longitude")));
        }
        // Exercise SUT
        Ride actual = sut.estimateRide(rideLocations);
        // Verify
        assertEquals(3.33, actual.getOrderDistance());
        assertEquals(26.3, actual.getOrderAmount());
    }

    @Before
    public void setUp() {
        sut = new RideSyncher();
        BaseSyncher.testSetup();
        AndroidStubsFactory.IS_TEST = true;
        closeCurrentRide();
    }

    @After
    public void tearDown() {
        sut = null;
    }

    private void cAssertRequestorDetails(Ride actual) {
        assertNotNull(actual.getSourceAddress());
        assertNotNull(actual.getRequestorPhoneNumber());
        assertNotNull(actual.getRequestorName());
        assertNotNull(actual.getRequestedAt());
    }

    private RideLocation createRideLocation(double latitude, double longitude) {
        RideLocation result = new RideLocation();
        result.setLatitude(latitude);
        result.setLongitude(longitude);
        return result;
    }

}
