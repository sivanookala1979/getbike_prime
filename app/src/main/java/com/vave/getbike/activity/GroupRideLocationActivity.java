/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vave.getbike.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vave.getbike.R;
import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.helpers.GMapV2Direction;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.GroupRider;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;
import com.vave.getbike.utils.HTTPUtils;

import org.w3c.dom.Document;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.vave.getbike.activity.GiveRideTakeRideActivity.GPS_PERMISSION_REQUEST_CODE;
import static com.vave.getbike.utils.GetBikeUtils.isTimePassed;

/**
 * Getting Location Updates.
 * <p>
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 * <p>
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 * <p>
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class GroupRideLocationActivity extends BaseActivity implements
        android.location.LocationListener, View.OnClickListener, OnMapReadyCallback {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int TIME_DELAY_BETWEEN_LOCATION_POSTS = 45;
    protected static final String TAG = "location-updates-sample";
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    /**
     * Provides the entry point to Google Play services.
     */
    // protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    //protected LocationRequest mLocationRequest;
    public static GroupRideLocationActivity activeInstance;
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    // UI Widgets.
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLocationCountTextView;
    protected TextView locationAddress;
    protected LinearLayout locationTrackingPanel,parcelDetailsLayout;
    //    protected ListView listView;
    //protected LocationAdapter adapter;
    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    protected Date mLastUpdateTimeAsDate;
    protected Date lastLocationPostedTime = new Date();
    protected boolean tripStarted = false;
    List<RideLocation> locations = new ArrayList<>();
    GroupRider riderLocations = new GroupRider();
    LocationManager locationManager;
    Ride ride = null;
    private GoogleMap mMap;
    private long rideId;
    private Marker marker = null;
    private int statusCount = 0;
    private int startRide = 0;
    private String buttonStatus = null;
    private String location = null;


    public static GroupRideLocationActivity instance() {
        return activeInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_ride_location);
        addToolbarView();
        rideId = getIntent().getLongExtra("groupId", 0L);
        //riderId = getIntent().getLongExtra("groupRiderId", 0L);
        // Locate the UI widgets.
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);
        mLocationCountTextView = (TextView) findViewById(R.id.locationCount);
        TextView tripIdTextView = (TextView) findViewById(R.id.tripId);
        tripIdTextView.setText("" + rideId);
        locationAddress = (TextView) findViewById(R.id.location_address);
        locationTrackingPanel = (LinearLayout) findViewById(R.id.location_tracking_panel);
        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mStartUpdatesButton.setOnClickListener(this);
        mStopUpdatesButton.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().getBooleanExtra("isTripResumed", false)) {
            //Resume the previous trip;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activeInstance = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeInstance = null;
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }

    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000, 2, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            ToastHelper.redToast(getApplicationContext(), "Could not request location updates");
        }
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void storeRideLocation() {
        if (mCurrentLocation != null) {
            String locationTime = String.format("%s: %s", mLastUpdateTimeLabel, mLastUpdateTime);
            mLastUpdateTimeTextView.setText(locationTime);
            RideLocationDataSource dataSource = new RideLocationDataSource(getApplicationContext());
            dataSource.setUpdataSource();
            dataSource.insert(rideId, mLastUpdateTimeAsDate, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), false);
            locations = dataSource.getRideLocations(rideId);
            dataSource.close();
            postLastKnownLocation();
            if (mMap != null) {
                PolylineOptions polylineOptions = new PolylineOptions();
                LatLng[] latLngs = new LatLng[locations.size()];
                int i = 0;
                for (RideLocation location : locations) {
                    latLngs[i] = new LatLng(location.getLatitude(), location.getLongitude());
                    i++;
                }

                mMap.addPolyline(polylineOptions
                        .add(latLngs)
                        .width(5)
                        .color(Color.parseColor("#FFA500")));
                if (marker != null) {
                    marker.remove();
                }
                if (locations.size() > 0) {
                    RideLocation lastLocation = locations.get(locations.size() - 1);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.top_view_bike_icon)).anchor(0.5f, 0.5f));
                    marker.setRotation(mCurrentLocation.getBearing());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 18.0f);
                    mMap.animateCamera(cameraUpdate);
                }
            }
            mLocationCountTextView.setText("Locations Count:" + locations.size());
        }
    }

    private void postLastKnownLocation() {
        if (!tripStarted || (locations.size() > 0 && locations.size() % 10 == 0)) {
            if (isTimePassed(lastLocationPostedTime, new Date(), TIME_DELAY_BETWEEN_LOCATION_POSTS))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new GetBikeAsyncTask(GroupRideLocationActivity.this) {

                                @Override
                                public void process() {
                                    LoginSyncher loginSyncher = new LoginSyncher();
                                    loginSyncher.storeLastKnownLocation(new Date(), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                                }

                                @Override
                                public void afterPostExecute() {
                                    lastLocationPostedTime = new Date();
                                }
                            }.execute();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTimeAsDate = new Date();
        mLastUpdateTime = DateFormat.getTimeInstance().format(mLastUpdateTimeAsDate);
        if (tripStarted) {
            storeRideLocation();
        } else {
            postLastKnownLocation();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.start_updates_button:
                statusChangedDialog();
                if(startRide==0) {
                    new GetBikeAsyncTask(GroupRideLocationActivity.this) {

                        @Override
                        public void process() {
                            new RideSyncher().startRide(rideId);
                        }

                        @Override
                        public void afterPostExecute() {
                            tripStarted = true;
                        }
                    }.execute();
                }startRide++;
                if(statusCount==riderLocations.getGroupRiderLocations().size()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupRideLocationActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("STOP TRIP");
                    builder.setMessage("Do you want to stop the trip?");
                    builder.setPositiveButton("Stop Trip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopUpdatesButtonHandler(v);
                            LocationDetails.stopTrip(GroupRideLocationActivity.this, rideId);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.stop_updates_button:
                break;
        }
    }

    private void statusChangedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupRideLocationActivity.this);
        builder.setCancelable(false);
        builder.setTitle("STATUS");
        builder.setMessage("Do you want to change the status?");
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStartUpdatesButton.setText(getButtonStatus());
                locationAddress.setText(location);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    GPS_PERMISSION_REQUEST_CODE);
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        animateToLocation();
        startLocationUpdates();
    }

    public void animateToLocation() throws SecurityException {
        Location startLocation = LocationDetails.getLocationOrShowToast(GroupRideLocationActivity.this, locationManager);
        if (mMap != null) {
            if (LocationDetails.isValid(startLocation)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()), 16.0f));
            }
            new GetBikeAsyncTask(GroupRideLocationActivity.this) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    riderLocations = rideSyncher.getRiderLocations(rideId);
                }

                @Override
                public void afterPostExecute() {
                    if (riderLocations.getGroupRiderLocations().size()>0 && riderLocations.getGroupRiderLocations().get(0).getLatitude() != null && riderLocations.getGroupRiderLocations().get(0).getLongitude() != null) {
                        double latitude = riderLocations.getGroupRiderLocations().get(0).getLatitude();
                        double longitude = riderLocations.getGroupRiderLocations().get(0).getLongitude();
                        if (latitude != 0.0 && longitude != 0.0) {
                            LatLng destination = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions()
                                    .position(destination)
                                    .title("Customer Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_pointer)));
                        }
                    }
                    mStartUpdatesButton.setText(getButtonStatus());
                    locationAddress.setText(location);
                }
            }.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    animateToLocation();

                } else {

                }
                return;
            }
        }
    }

    public void rideCancelled(long rideId) {
        if (this.rideId == rideId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                    ToastHelper.blueToast(GroupRideLocationActivity.this, "This ride is cancelled by the user.");
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateToLocation();
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<android.location.Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            android.location.Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private String getButtonStatus(){
        if(statusCount<riderLocations.getGroupRiderLocations().size()) {
            RideLocation rideLocation = riderLocations.getGroupRiderLocations().get(statusCount);
            if (rideLocation.isSourse()) {
                buttonStatus = "Pickup, Id " + rideLocation.getRideId();
                location = rideLocation.getSourseAddress();
            } else {
                buttonStatus = "Drop, Id " + rideLocation.getRideId();
                location = rideLocation.getDestinationAddress();
            }
            statusCount++;
        }
        return buttonStatus;
    }
}