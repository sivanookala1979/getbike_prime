package com.vave.getbike.syncher;

import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.model.GeoFencingLocation;
import com.vave.getbike.model.GroupRider;
import com.vave.getbike.model.PromotionsBanner;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.model.Vendor;
import com.vave.getbike.utils.GsonUtils;
import com.vave.getbike.utils.HTTPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sivanookala on 26/10/16.
 */

public class RideSyncher extends BaseSyncher {

    public Ride requestRide(final double latitude, final double longitude, final String modeOfPayment) {
        return requestRide(latitude, longitude, "Source Not Provided", "Destination Not Provided", modeOfPayment);
    }

    public Ride requestRide(final double latitude, final double longitude, final String sourceAddress, final String destinationAddress, final String modeOfPayment) {
        final GetBikePointer<Ride> result = new GetBikePointer<>();
        new JsonPostHandler("/getBike") {

            @Override
            protected void prepareRequest() {
                put("latitude", latitude);
                put("longitude", longitude);
                put("sourceAddress", sourceAddress);
                put("destinationAddress", destinationAddress);
                put("modeOfPayment", modeOfPayment);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                Ride ride = new Ride();
                ride.setId(jsonResult.getLong("rideId"));
                result.setValue(ride);
            }
        }.handle();
        return result.getValue();
    }

    public CallStatus hailCustomer(final double latitude, final double longitude, final String sourceAddress, final String destinationAddress, final String phoneNumber, final String name, final String email, final char gender, final String modeOfPayment, final Long vendorId) {
        final CallStatus result = new CallStatus();
        new JsonPostHandler("/hailCustomer") {

            @Override
            protected void prepareRequest() {
                put("latitude", latitude);
                put("longitude", longitude);
                put("sourceAddress", sourceAddress);
                put("destinationAddress", destinationAddress);
                if (vendorId != null) {
                    put("vendorId", vendorId);
                } else {
                    put("phoneNumber", phoneNumber);
                    put("name", name);
                    put("email", email);
                    put("gender", gender + "");
                }
                put("modeOfPayment", modeOfPayment);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setSuccess();
                    result.setId(jsonResult.getLong("rideId"));
                } else {
                    result.setErrorCode((Integer) jsonResult.get("errorCode"));
                }

            }
        }.handle();
        return result;
    }

    public CallStatus acceptRide(long rideId) {
        final CallStatus result = new CallStatus();
        new JsonGetHandler("/acceptRide?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setSuccess();
                } else {
                    result.setErrorCode((Integer) jsonResult.get("errorCode"));
                }
            }
        }.handle();
        return result;
    }

    public boolean startRide(long rideId) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(null);
        result.setValue(false);
        new JsonGetHandler("/startRide?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean cancelRide(long rideId) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(null);
        result.setValue(false);
        new JsonGetHandler("/cancelRide?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean rateRide(long rideId, int rating) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(null);
        result.setValue(false);
        new JsonGetHandler("/rateRide?rideId=" + rideId + "&rating=" + rating) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean updatePaymentStatus(long rideId) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(null);
        result.setValue(false);
        new JsonGetHandler("/updatePaymentStatus?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public Ride closeRide(long rideId) {
        final GetBikePointer<Ride> result = new GetBikePointer<>(null);
        new JsonGetHandler("/closeRide?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    Ride ride = new Ride();
                    JSONObject jsonRideObject = (JSONObject) jsonResult.get("ride");
                    ride.setId(jsonRideObject.getLong("id"));
                    ride.setOrderDistance(jsonRideObject.getDouble("orderDistance"));
                    result.setValue(ride);
                }
            }
        }.handle();
        return result.getValue();
    }

    public Ride getRideById(long rideId) {
        final GetBikePointer<Ride> result = new GetBikePointer<>(null);
        new JsonGetHandler("/getRideById?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    Ride ride = createRideFromJson(jsonResult);
                    result.setValue(ride);
                }
            }
        }.handle();
        return result.getValue();
    }

    public Ride getCompleteRideById(long rideId, final List<RideLocation> rideLocations) {
        final GetBikePointer<Ride> result = new GetBikePointer<>(null);
        new JsonGetHandler("/getCompleteRideById?rideId=" + rideId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    Ride ride = createRideFromJson(jsonResult);
                    if (jsonResult.has("rideLocations")) {
                        JSONArray rideLocationsArray = jsonResult.getJSONArray("rideLocations");
                        for (int i = 0; i < rideLocationsArray.length(); i++) {
                            RideLocation rideLocation = GsonUtils.getGson().fromJson(rideLocationsArray.get(i).toString(), RideLocation.class);
                            rideLocations.add(rideLocation);
                        }
                    }
                    result.setValue(ride);
                }
            }
        }.handle();
        return result.getValue();
    }

    private Ride createRideFromJson(JSONObject jsonResult) throws JSONException {
        JSONObject jsonRideObject = (JSONObject) jsonResult.get("ride");
        Ride ride = GsonUtils.getGson().fromJson(jsonRideObject.toString(), Ride.class);
        if (jsonResult.has("requestorName") && !jsonResult.isNull("requestorName")) {
            ride.setRequestorName(jsonResult.getString("requestorName"));
        }
        ride.setRequestorPhoneNumber(jsonResult.getString("requestorPhoneNumber"));
        return ride;
    }

    public List<Ride> openRides(double latitude, double longitude) {
        final ArrayList<Ride> result = new ArrayList<>();
        new JsonGetHandler("/openRides?latitude=" + latitude + "&longitude=" + longitude) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("rides")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("rides");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        result.add(createRideFromJson(ridesArray.getJSONObject(i)));
                    }
                }
                if (jsonResult.has("groupRides")) {
                    JSONArray groupRidesArray = jsonResult.getJSONArray("groupRides");
                    for (int i = 0; i < groupRidesArray.length(); i++) {
                        JSONObject jsonObject = groupRidesArray.getJSONObject(i);
                        Ride groupRide = new Ride();
                        if (jsonObject.has("groupId"))
                            groupRide.setId(jsonObject.getLong("groupId"));
                        if (jsonObject.has("isGroupRide"))
                            groupRide.setGroupRide(jsonObject.getBoolean("isGroupRide"));
                        if (jsonObject.has("numberOfRides"))
                            groupRide.setNumberOfRides(jsonObject.getInt("numberOfRides"));
                        if (jsonObject.has("firstRide")) {
                            JSONObject jsonFirstRideObject = (JSONObject) jsonObject.get("firstRide");
                            groupRide.setRequestedAt(new Date(jsonFirstRideObject.getLong("requestedAt")));
                            groupRide.setSourceAddress(jsonFirstRideObject.getString("sourceAddress"));
                        }
                        if (jsonObject.has("lastRide")) {
                            JSONObject jsonLastRideObject = (JSONObject) jsonObject.get("lastRide");
                            groupRide.setDestinationAddress(jsonLastRideObject.getString("destinationAddress"));
                        }
                        result.add(groupRide);
                    }
                }
            }
        }.handle();
        return result;
    }

    public List<Vendor> getVendors() {
        final ArrayList<Vendor> result = new ArrayList<>();
        new JsonGetHandler("/getVendors") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("vendors")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("vendors");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        Vendor vendor = new Vendor();
                        vendor.setId(ridesArray.getJSONObject(i).getLong("id"));
                        vendor.setName(ridesArray.getJSONObject(i).getString("name"));
                        result.add(vendor);
                    }
                }
            }
        }.handle();
        return result;
    }

    public GroupRider getRiderLocations(Long groupId) {
        final GroupRider groupRider = new GroupRider();
        final ArrayList<RideLocation> result = new ArrayList<>();
        new JsonGetHandler("/getRiderLocations/"+groupId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("groupId"))
                    groupRider.setGroupId(jsonResult.getLong("groupId"));
                    if (jsonResult.has("groupRiderId"))
                        groupRider.setGroupRiderId(jsonResult.getLong("groupRiderId"));
                    if (jsonResult.has("riderLocations")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("riderLocations");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        JSONObject jsonObject = ridesArray.getJSONObject(i);
                        RideLocation rideLocation = new RideLocation();
                        if (jsonObject.has("rideId"))
                            rideLocation.setRideId(jsonObject.getLong("rideId"));
                        if (jsonObject.has("lat"))
                            rideLocation.setLatitude(jsonObject.getDouble("lat"));
                        if (jsonObject.has("lng"))
                            rideLocation.setLongitude(jsonObject.getDouble("lng"));
                        if (jsonObject.has("source"))
                            rideLocation.setSourse(jsonObject.getBoolean("source"));
                        if (jsonObject.has("sourceAddress"))
                            rideLocation.setSourseAddress(jsonObject.getString("sourceAddress"));
                        if (jsonObject.has("destinationAddress"))
                            rideLocation.setDestinationAddress(jsonObject.getString("destinationAddress"));
                        result.add(rideLocation);
                    }
                }
                groupRider.setGroupRiderLocations(result);
            }
        }.handle();
        return groupRider;
    }

    public List<Ride> getMyCompletedRides() {
        final ArrayList<Ride> result = new ArrayList<>();
        new JsonGetHandler("/getMyCompletedRides") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("rides")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("rides");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        result.add(createRideFromJson(ridesArray.getJSONObject(i)));
                    }
                }
            }
        }.handle();
        return result;
    }

    public List<Ride> getRidesGivenByMe() {
        final ArrayList<Ride> result = new ArrayList<>();
        new JsonGetHandler("/getRidesGivenByMe") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("rides")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("rides");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        result.add(createRideFromJson(ridesArray.getJSONObject(i)));
                    }
                }
            }
        }.handle();
        return result;
    }

    public Ride estimateRide(List<RideLocation> rideLocations) {
        Ride result = null;
        try {
            JSONArray jsonArray = new JSONArray();
            int index = 0;
            for (RideLocation rideLocation : rideLocations) {
                JSONObject rideLocationObject = new JSONObject();
                rideLocationObject.put("latitude", rideLocation.getLatitude());
                rideLocationObject.put("longitude", rideLocation.getLongitude());
                jsonArray.put(index++, rideLocationObject);
            }
            String response = HTTPUtils.getDataFromServer(BASE_URL + "/estimateRide", "POST", jsonArray.toString());
            result = GsonUtils.getGson().fromJson(response, Ride.class);
        } catch (Exception ex) {
            handleException(ex);
        }
        return result;
    }

    public List<RideLocation> loadNearByRiders(double latitude, double longitude) {
        final List<RideLocation> result = new ArrayList<>();
        new JsonGetHandler("/loadNearByRiders?latitude=" + latitude + "&longitude=" + longitude) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("riders")) {
                    JSONArray ridesArray = jsonResult.getJSONArray("riders");
                    for (int i = 0; i < ridesArray.length(); i++) {
                        result.add(GsonUtils.getGson().fromJson(ridesArray.get(i).toString(), RideLocation.class));
                    }
                }
            }
        }.handle();
        return result;
    }

    public List<GeoFencingLocation> geoFencingAreaValidation(double latitude, double longitude) {
        final List<GeoFencingLocation> geoFencingLocations = new ArrayList<>();
        new JsonGetHandler("/geoFencingAreaValidation?latitude=" + latitude + "&longitude=" + longitude) {
            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "failure".equals(jsonResult.get("result")) && jsonResult.has("locations")) {
                    JSONArray locationArray = jsonResult.getJSONArray("locations");
                    for (int i = 0; i < locationArray.length(); i++) {
                        geoFencingLocations.add(GsonUtils.getGson().fromJson(locationArray.get(i).toString(), GeoFencingLocation.class));
                    }
                }
            }
        }.handle();
        return geoFencingLocations;
    }

    public void userRequestFromNonGeoFencingLocation(final double latitude, final double longitude, final String addressArea) {
        new JsonPostHandler("/userRequestFromNonGeoFencingLocation") {

            @Override
            protected void prepareRequest() {
                put("latitude", latitude);
                put("longitude", longitude);
                put("addressArea", addressArea);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {

            }
        }.handle();
    }

    public boolean saveUserCashInAdvanceRequest(final double amount, final String riderDescription) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/saveUserCashInAdvanceRequest") {

            @Override
            protected void prepareRequest() {
                put("amount", amount);
                put("riderDescription", riderDescription);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean saveLeaveRequest(final String leavesRequired,final String riderDescription, final String fromDate, final String toDate) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/saveLeaveRequest") {

            @Override
            protected void prepareRequest() {
                put("leavesRequired",leavesRequired);
                put("riderDescription",riderDescription);
                put("fromDate",fromDate);
                put("toDate",toDate);

            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }



    public PromotionsBanner getPromotionalBannerWithUrl(final String resolution) {
        final PromotionsBanner promotionsBanner = new PromotionsBanner();
        new JsonGetHandler("/promotion/sendPromotion?resolution=" + resolution) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    promotionsBanner.setImageName(jsonResult.getString(resolution));
                    promotionsBanner.setImageUrl(jsonResult.getString("promotionsURL"));
                }
            }
        }.handle();
        return promotionsBanner;
    }

    public boolean storeParcelBillPhoto(final String encodedImageData,final long rideId) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/storeParcelBillPhoto") {

            @Override
            protected void prepareRequest() {
                put("imageData", encodedImageData);
                put("rideId", rideId);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    double customerTripsAmount = 0.0;
    double parcelTripsAmount = 0.0;
    public double getCustomerTripsAmountForDate(final String date){
        new JsonGetHandler("/getTripsAmountForDate?dateString=" + date) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    System.out.println("Testing phase ... .. customer trips amount......"+jsonResult.getDouble("customerTripsAmount"));
                    customerTripsAmount = jsonResult.getDouble("customerTripsAmount");
                }
            }
        }.handle();
        return customerTripsAmount;
    }

    public double getParcelTripsAmountForDate(final String date){
        new JsonGetHandler("/getTripsAmountForDate?dateString=" + date) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    System.out.println("Testing phase ... .. parcel trips amount......"+jsonResult.getDouble("parcelTripsAmount"));
                    parcelTripsAmount = jsonResult.getDouble("parcelTripsAmount");
                }
            }
        }.handle();
        return parcelTripsAmount;
    }

}
