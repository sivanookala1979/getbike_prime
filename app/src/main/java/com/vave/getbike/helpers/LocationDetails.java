/*
 * (c) Copyright 2001-2016 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vave.getbike.activity.ShowCompletedRideActivity;
import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.RideLocationSyncher;
import com.vave.getbike.syncher.RideSyncher;

/**
 * @author Adarsh.T
 * @version 1.0, 09-Feb-2016
 */
public class LocationDetails {

    public static final int LOCATION_EXPIRY_TIME_IN_MILLI_SECONDS = 60000;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    String address;
    double latitude;
    double longitude;
    String city;

    public static boolean isValid(Location location) {
        return location != null && location.getLatitude() != 0.0 && location.getLatitude() != 0.0;
    }

    public static Location getLocationOrShowToast(Context context, LocationManager locationManager) throws SecurityException {
        Location result = null;
        if (locationManager != null) {
            result = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (result != null && isExpired(result)) {
                result = null;
            }
            if (result == null) {
                result = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (result == null) {
                result = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        if (!isValid(result)) {
            ToastHelper.gpsToast(context);
            displayLocationSettingsRequest(context);
        }
        return result;
    }

    public static void stopTrip(Context context, final long rideId) {
        new GetBikeAsyncTask(context) {
            Ride closedRide = null;

            @Override
            public void process() {
                RideLocationDataSource dataSource = new RideLocationDataSource(context);
                dataSource.setUpdataSource();
                RideLocationSyncher locationSyncher = new RideLocationSyncher();
                locationSyncher.setDataSource(dataSource);
                locationSyncher.storePendingLocations(rideId);
                dataSource.close();
                RideSyncher sut = new RideSyncher();
                closedRide = sut.closeRide(rideId);
            }

            @Override
            public void afterPostExecute() {
                if (closedRide != null) {
                    Intent intent = new Intent(context, ShowCompletedRideActivity.class);
                    intent.putExtra("rideId", closedRide.getId());
                    context.startActivity(intent);
                }
            }
        }.execute();
    }

    public static boolean isExpired(Location result) {
        return (System.currentTimeMillis() - result.getTime()) > LOCATION_EXPIRY_TIME_IN_MILLI_SECONDS;
    }

    public static void displayLocationSettingsRequest(final Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
