package com.vave.getbike.syncher;

import com.google.gson.Gson;
import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.model.CurrentRideStatus;
import com.vave.getbike.model.Profile;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.utils.GsonUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sivanookala on 25/10/16.
 */

public class LoginSyncher extends BaseSyncher {

    public CallStatus signup(final String name, final String mobileNumber, final String email, final char gender, final String signupPromoCode) {
        final GetBikePointer<CallStatus> result = new GetBikePointer<>(null);
        new JsonPostHandler("/signup") {

            @Override
            protected void prepareRequest() {
                put("name", name);
                put("phoneNumber", mobileNumber);
                put("email", email);
                put("gender", gender);
                put("signupPromoCode", signupPromoCode);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                CallStatus callStatus = new CallStatus();
                System.out.println("json result in login syncher is:" + jsonResult);
                if (jsonResult.has("id")) {
                    callStatus.setId(((Integer) jsonResult.get("id")).longValue());
                    callStatus.setSuccess();
                } else {
                    callStatus.setErrorCode((Integer) jsonResult.get("errorCode"));
                }
                result.setValue(callStatus);
            }
        }.handle();
        return result.getValue();
    }

    public boolean login(final String mobileNumber) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/login") {

            @Override
            protected void prepareRequest() {
                put("phoneNumber", mobileNumber);
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

    public boolean loginWithOtp(final String mobileNumber, final String otpNumber) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/loginWithOtp") {

            @Override
            protected void prepareRequest() {
                put("phoneNumber", mobileNumber);
                put("otp", otpNumber);
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValue(true);
                    System.out.println(jsonResult.get("authToken"));
                    BaseSyncher.setAccessToken((String) jsonResult.get("authToken"));
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean storeGcmCode(String gcmCode) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(null);
        result.setValue(false);
        new JsonGetHandler("/storeGcmCode?gcmCode=" + gcmCode) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setValue(true);
                }
            }
        }.handle();
        return result.getValue();
    }

    public boolean storeVehicleNumber(final String encodedImageData, final String vehicleNumber) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/storeVehiclePlate") {

            @Override
            protected void prepareRequest() {

                put("vehiclePlateNumber", vehicleNumber);
                put("imageData", encodedImageData);
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

    public boolean storeDrivingLicense(final String encodedImageData, final String drivingLicenseNumber) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        new JsonPostHandler("/storeDrivingLicense") {

            @Override
            protected void prepareRequest() {

                put("drivingLicenseNumber", drivingLicenseNumber);
                put("imageData", encodedImageData);
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

    public Profile getPublicProfile(Long userId) {
        final GetBikePointer<Profile> result = new GetBikePointer<>(null);
        new JsonGetHandler("/getPublicProfile/" + userId) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    Profile profile = GsonUtils.getGson().fromJson(jsonResult.get("profile").toString(), Profile.class);
                    result.setValue(profile);
                }
            }
        }.handle();
        return result.getValue();
    }

    public CurrentRideStatus getCurrentRide(String versionDetails) {
        final CurrentRideStatus result = new CurrentRideStatus();
        new JsonGetHandler("/getCurrentRide?version=" + versionDetails) {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    result.setPending(true);
                    if (jsonResult.has("rideId")) {
                        result.setRideId(jsonResult.getLong("rideId"));
                    }
                    if (jsonResult.has("requestId")) {
                        result.setRequestId(jsonResult.getLong("requestId"));
                    }
                }
            }
        }.handle();
        return result;
    }

    public boolean storeLastKnownLocation(final Date lastLocationTime, final Double latitude, final Double longitude) {
        final GetBikePointer<Boolean> result = new GetBikePointer<>(false);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        new JsonPostHandler("/storeLastKnownLocation") {

            @Override
            protected void prepareRequest() {
                put("lastLocationTime", dateFormat.format(lastLocationTime));
                put("lastKnownLatitude", latitude);
                put("lastKnownLongitude", longitude);
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

    public UserProfile getUserProfile() {
        final GetBikePointer<UserProfile> result = new GetBikePointer<>(null);
        new JsonGetHandler("/getPrivateProfile") {

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && jsonResult.get("result").equals("success")) {
                    UserProfile userProfile = GsonUtils.getGson().fromJson(jsonResult.get("privateProfile").toString(), UserProfile.class);
                    result.setValue(userProfile);
                }
            }
        }.handle();
        return result.getValue();
    }

    public SaveResult updateUserProfile(final UserProfile userProfile, final String imageData) {
        final SaveResult result = new SaveResult();
        new JsonPostHandler("/updatePrivateProfile") {

            @Override
            protected void prepareRequest() {
                try {
                    JSONObject userJsonObject = new JSONObject(new Gson().toJson(userProfile));
                    jsonRequest.put("user", userJsonObject);
                    put("imageData", imageData);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }

            @Override
            protected void processResult(JSONObject jsonResult) throws Exception {
                if (jsonResult.has("result") && "success".equals(jsonResult.get("result"))) {
                    result.setValid(true);
                }
            }
        }.handle();
        return result;
    }
}
