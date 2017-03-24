package com.vave.getbike.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.CurrentRideStatus;
import com.vave.getbike.model.GeoFencingLocation;
import com.vave.getbike.model.Profile;
import com.vave.getbike.model.PromotionsBanner;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import pl.droidsonroids.gif.GifImageView;

public class GiveRideTakeRideActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int GPS_PERMISSION_REQUEST_CODE = 8;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 100;
    private static final long FASTEST_INTERVAL = 1000 * 50;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location fusedCurrentLocation;
    GoogleMap googleMap;
    CurrentRideStatus rideStatus = null;
    Button showCurrentRideButton;
    LinearLayout giveRideTakeRideLinearLayout;
    String applicationVersionName = "xx";
    int applicationVersionCode = -999;
    String applicationVersionFromStrings = "xxxx";
    private Location mCurrentLocation;
    private LocationManager locationManager;
    private ImageButton takeRide;
    private ImageButton giveRide;
    boolean geoFencingValidationResult = false;
    String geoFencingAddresses = " ";
    PromotionsBanner promotionsBanner;
    final static String TAG_NAME = "GiveRideTakeRideScreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        addNavigationMenu(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetBikePreferences.setPreferences(getApplicationContext());
        TextView appVersionTextView = (TextView) findViewById(R.id.appVersionTextView);
        if (!(GetBikePreferences.isTutorialCompleted())) {
            //Show tutorial;
            final GifImageView getBikePromotionsGifView = (GifImageView) findViewById(R.id.get_bike_promotions_gif_view);
            final GifImageView sharePromoCodeGifView = (GifImageView) findViewById(R.id.share_promo_code_gif_view);
            final GifImageView giveRideTakeRideGifView = (GifImageView) findViewById(R.id.take_ride_give_ride_gif_view);

            giveRideTakeRideGifView.setVisibility(View.VISIBLE);
            giveRideTakeRideGifView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    giveRideTakeRideGifView.setVisibility(View.GONE);
                    getBikePromotionsGifView.setVisibility(View.VISIBLE);
                }
            });
            getBikePromotionsGifView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBikePromotionsGifView.setVisibility(View.GONE);
                    sharePromoCodeGifView.setVisibility(View.VISIBLE);
                }
            });
            sharePromoCodeGifView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePromoCodeGifView.setVisibility(View.GONE);
                }
            });
            GetBikePreferences.setIsTutorialCompleted(true);
        }

        //Code for promotional banner;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateInStringFormat = simpleDateFormat.format(new Date());
        if (!(GetBikePreferences.getPromotionsBannerCompletedOn().equals(currentDateInStringFormat))){
            //show promotions banner;
            float resolutionDensity = getResources().getDisplayMetrics().density;
            Log.d(TAG_NAME, "device screen size is " + resolutionDensity);
            if (resolutionDensity == 0.75) {
                //ldpi; 240 X 175
                showPromotionsBanner("ldpi",240,175);
            } else if (resolutionDensity == 1.0) {
                //mdpi; 320 X 234
                showPromotionsBanner("mdpi",320,234);
            } else if (resolutionDensity == 2.0) {
                //xhdpi; 640 X 467
                showPromotionsBanner("xhdpi",640,467);
            } else if (resolutionDensity == 3.0) {
                //xxhdpi; 960 X 701
                showPromotionsBanner("xxhdpi",960,351);
            } else {
                //hdpi; 480 X 351
                showPromotionsBanner("hdpi",480,351);
            }
            GetBikePreferences.setPromotionsBannerCompletedOn(currentDateInStringFormat);
        }

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            applicationVersionName = pInfo.versionName;
            applicationVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        applicationVersionFromStrings = getString(R.string.app_version);
        appVersionTextView.setText(applicationVersionName + "~" + applicationVersionCode + "~" + applicationVersionFromStrings);

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        showCurrentRideButton = (Button) findViewById(R.id.show_current_ride_button);
        giveRideTakeRideLinearLayout = (LinearLayout) findViewById(R.id.give_ride_take_ride_linear_layout);
        takeRide = (ImageButton) findViewById(R.id.takeRide);
        takeRide.setOnClickListener(this);
        giveRide = (ImageButton) findViewById(R.id.giveRide);
        giveRide.setOnClickListener(this);
        showCurrentRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rideStatus != null && rideStatus.getRideId() != null && rideStatus.getRideId() > 0) {
                    resumeRide(rideStatus.getRideId());
                } else if (rideStatus != null && rideStatus.getRequestId() != null && rideStatus.getRequestId() > 0) {
                    final long requestId = rideStatus.getRequestId();

                    new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

                        Ride ride = null;

                        @Override
                        public void process() {
                            ride = new RideSyncher().getRideById(rideStatus.getRequestId());
                        }

                        @Override
                        public void afterPostExecute() {
                            if (ride != null && "RideRequested".equals(ride.getRideStatus())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("Trip Details");
                                builder.setMessage("Your previous trip was not yet closed, Do you want to resume it again?");
                                builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(GiveRideTakeRideActivity.this, WaitForRiderAllocationActivity.class);
                                        intent.putExtra("rideId", requestId);
                                        startActivity(intent);

                                    }
                                });
                                builder.setNegativeButton("Cancel Ride", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        processCancelRide(requestId);
                                    }

                                });
                                builder.show();
                            } else if (ride != null && "RideAccepted".equals(ride.getRideStatus())) {
                                Intent intent = new Intent(GiveRideTakeRideActivity.this, WaitForRiderAfterAcceptanceActivity.class);
                                intent.putExtra("rideId", requestId);
                                startActivity(intent);
                            }
                        }
                    }.execute();

                    Log.d("Tag", "clicked on show current ride button");

                }
            }

        });
    }

    public void processCancelRide(final long requestId) {
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {
            boolean result = false;

            @Override
            public void process() {
                RideSyncher rideSyncher = new RideSyncher();
                result = rideSyncher.cancelRide(requestId);
            }

            @Override
            public void afterPostExecute() {
                if (result) {
                    ToastHelper.blueToast(GiveRideTakeRideActivity.this, "Successfully cancelled the ride.");
                    updateCurrentRideId();
                } else {
                    ToastHelper.redToast(GiveRideTakeRideActivity.this, "Failed to cancel the ride.");
                    Intent intent = new Intent(GiveRideTakeRideActivity.this, WaitForRiderAfterAcceptanceActivity.class);
                    intent.putExtra("rideId", requestId);
                    startActivity(intent);
                }
            }
        }.execute();
    }

    public void resumeRide(final long rideID) {
        Log.d("Tag", "clicked on show current ride button");
        AlertDialog.Builder builder = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Trip Details");
        builder.setMessage("Your previous trip was not yet closed, Do you want to resume it again?");
        builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(GiveRideTakeRideActivity.this, LocationActivity.class);
                intent.putExtra("rideId", rideID);
                intent.putExtra("isTripResumed", true);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Close Ride", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Need to close the ride
                dialogInterface.dismiss();
                LocationDetails.stopTrip(GiveRideTakeRideActivity.this, rideID);
            }
        });
        builder.show();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMapParam) {
        this.googleMap = googleMapParam;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    GPS_PERMISSION_REQUEST_CODE);
            return;
        }
        resetLocation();
    }

    public void resetLocation() throws SecurityException {
        mCurrentLocation = LocationDetails.getLocationOrShowToast(GiveRideTakeRideActivity.this, locationManager);

        if (googleMap != null && mCurrentLocation != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16.0f));
            googleMap.setMyLocationEnabled(true);
            loadNearByRiders(googleMap, mCurrentLocation);
            geoFencingValidation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
    }

    public void geoFencingValidation(final double userLatitude, final double userLongitude){
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

            List<GeoFencingLocation> geoFencingLocations = new ArrayList<GeoFencingLocation>();

            @Override
            public void process() {
                geoFencingLocations = new RideSyncher().geoFencingAreaValidation(userLatitude, userLongitude);
            }

            @Override
            public void afterPostExecute() {
                if (geoFencingLocations.size() > 0) {
                    geoFencingAddresses = " ";
                    for (GeoFencingLocation locations : geoFencingLocations) {
                        StringTokenizer stringTokenizer = new StringTokenizer(locations.getAddressArea());
                        geoFencingAddresses = geoFencingAddresses + stringTokenizer.nextToken(",") + ",";
                    }
                    geoFencingAddresses = geoFencingAddresses.substring(0, geoFencingAddresses.length() - 1);
                    //store the user location in Non GeoLocations table in db;
                    saveUserRequestFromNonGeoFencingLocation(userLatitude,userLongitude);
                    geoFencingValidationPopUpMessage(geoFencingAddresses);
                } else {
                    geoFencingValidationResult = true;
                }
            }
        }.execute();
    }

    public void saveUserRequestFromNonGeoFencingLocation(final double latitude, final double longitude) {
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

            @Override
            public void process() {
                RideSyncher rideSyncher = new RideSyncher();
                rideSyncher.userRequestFromNonGeoFencingLocation(latitude,longitude,getCompleteAddressString(latitude,longitude));
            }

            @Override
            public void afterPostExecute() {

            }
        }.execute();
    }

    public void geoFencingValidationPopUpMessage(String locationAreas) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Notification");
        builder.setMessage("Currently we are available in" + locationAreas);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void loadNearByRiders(final GoogleMap googleMap, final Location currentLocation) {
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

            List<RideLocation> rideLocations = new ArrayList<RideLocation>();

            @Override
            public void process() {
                rideLocations = new RideSyncher().loadNearByRiders(currentLocation.getLatitude(), currentLocation.getLongitude());
            }

            @Override
            public void afterPostExecute() {
                if (rideLocations.size() > 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (RideLocation location : rideLocations) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        builder.include(latLng);
                        googleMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike)));
                    }
                    builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 15);
                    googleMap.animateCamera(cameraUpdate);
                }
            }
        }.execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takeRide:
                if (geoFencingValidationResult){
                    launchActivity(GiveDestinationAddressActivity.class);
                }
                else {
                    geoFencingValidationPopUpMessage(geoFencingAddresses);
                }
                break;
            case R.id.giveRide:
                new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {
                    Profile publicProfile;

                    @Override
                    public void process() {
                        publicProfile = new LoginSyncher().getPublicProfile(0l);
                    }

                    @Override
                    public void afterPostExecute() {
                        if (publicProfile != null) {
                            if (publicProfile.getDrivingLicenseNumber() != null && publicProfile.getVehicleNumber() != null) {
                                launchActivity(OpenRidesActivity.class);
                            } else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("Rider profile");
                                builder.setMessage("Please fill your rider profile to give a ride.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(GiveRideTakeRideActivity.this, RiderProfileActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            ToastHelper.serverToast(GiveRideTakeRideActivity.this);
                        }
                    }
                }.execute();
                break;
        }
    }

    public void launchActivity(Class targetActivity) {
        resetLocation();
        if (googleMap != null && fusedCurrentLocation != null) {
            if (fusedCurrentLocation.getLatitude() != 0.0 && fusedCurrentLocation.getLongitude() != 0.0) {
                Intent intent = new Intent(GiveRideTakeRideActivity.this, targetActivity);
                intent.putExtra("latitude", fusedCurrentLocation.getLatitude());
                intent.putExtra("longitude", fusedCurrentLocation.getLongitude());
                startActivity(intent);
            }
        } else if (LocationDetails.isValid(mCurrentLocation)) {
            Intent intent = new Intent(GiveRideTakeRideActivity.this, targetActivity);
            intent.putExtra("latitude", mCurrentLocation.getLatitude());
            intent.putExtra("longitude", mCurrentLocation.getLongitude());
            startActivity(intent);
        } else {
            ToastHelper.gpsToast(GiveRideTakeRideActivity.this);
            LocationDetails.displayLocationSettingsRequest(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    resetLocation();

                } else {

                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCurrentRideId();
        resetLocation();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    public void updateCurrentRideId() {
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

            @Override
            public void process() {
                rideStatus = new LoginSyncher().getCurrentRide(applicationVersionName + "~" + applicationVersionCode + "~" + applicationVersionFromStrings);
            }

            @Override
            public void afterPostExecute() {
                if (rideStatus != null && rideStatus.isPending()) {
                    showCurrentRideButton.setVisibility(View.VISIBLE);
                    giveRideTakeRideLinearLayout.setVisibility(View.GONE);
                } else {
                    showCurrentRideButton.setVisibility(View.GONE);
                    giveRideTakeRideLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        }.execute();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        fusedCurrentLocation = location;
        Log.d(TAG, "fusedCurrentLocation is:" + fusedCurrentLocation);
        if (googleMap != null && fusedCurrentLocation != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(fusedCurrentLocation.getLatitude(), fusedCurrentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fusedCurrentLocation.getLatitude(), fusedCurrentLocation.getLongitude()), 16.0f));
            loadNearByRiders(googleMap, fusedCurrentLocation);
            geoFencingValidation(fusedCurrentLocation.getLatitude(),fusedCurrentLocation.getLongitude());
        }
    }

    protected void startLocationUpdates() {
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
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("Do you want to Exit");
            alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showPromotionsBanner(final String resolution, final int size1, final int size2) {
        //Code for promotional banner;
        final AlertDialog.Builder builder = new AlertDialog.Builder(GiveRideTakeRideActivity.this);
        final ImageView image1 = new ImageView(GiveRideTakeRideActivity.this);
        LinearLayout imageLayout = new LinearLayout(GiveRideTakeRideActivity.this);
        imageLayout.setOrientation(LinearLayout.VERTICAL);
        image1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageLayout.addView(image1);
        builder.setView(imageLayout);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });
        new GetBikeAsyncTask(GiveRideTakeRideActivity.this) {

            @Override
            public void process() {
                promotionsBanner = new RideSyncher().getPromotionalBannerWithUrl(resolution);
            }

            @Override
            public void afterPostExecute() {
                if (promotionsBanner != null) {
                    if (promotionsBanner.getImageName() != null) {
                        Picasso.with(getApplicationContext()).load(BaseSyncher.BASE_URL + "/uploads/" + promotionsBanner.getImageName()).placeholder(R.drawable.picture).resize(size1, size2).into(image1);
                        builder.show();
                    }
                }
            }
        }.execute();
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+promotionsBanner.getImageUrl()));
                startActivity(i);
            }
        });

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                strAdd = strReturnedAddress.toString();
                if (strAdd.endsWith(", ")) {
                    strAdd = strAdd.substring(0, strAdd.length() - 2);
                }
                Log.w("My Current loction", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }

}
