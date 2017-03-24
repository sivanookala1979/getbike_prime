package com.vave.getbike.syncher;

import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.model.GeoFencingLocation;
import com.vave.getbike.model.PromotionsBanner;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.utils.GsonUtils;
import com.vave.getbike.utils.HTTPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    public CallStatus hailCustomer(final double latitude, final double longitude, final String sourceAddress, final String destinationAddress, final String phoneNumber, final String name, final String email, final char gender, final String modeOfPayment) {
        final CallStatus result = new CallStatus();
        new JsonPostHandler("/hailCustomer") {

            @Override
            protected void prepareRequest() {
                put("latitude", latitude);
                put("longitude", longitude);
                put("sourceAddress", sourceAddress);
                put("destinationAddress", destinationAddress);
                put("phoneNumber", phoneNumber);
                put("name", name);
                put("email", email);
                put("gender", gender + "");
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
            }
        }.handle();
        return result;
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
}
