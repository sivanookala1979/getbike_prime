package com.vave.getbike.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.CurrentRideStatus;
import com.vave.getbike.model.Profile;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GiveRideTakeRideActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Active Instance
    public static GiveRideTakeRideActivity activeInstance;
    public static final int GPS_PERMISSION_REQUEST_CODE = 8;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 100;
    private static final long FASTEST_INTERVAL = 1000 * 50;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location fusedCurrentLocation;
    GoogleMap googleMap;
    CurrentRideStatus rideStatus = null;
    Button showCurrentRideButton, hailSystemButton;
    LinearLayout giveRideTakeRideLinearLayout;
    String applicationVersionName = "xx";
    int applicationVersionCode = -999;
    String applicationVersionFromStrings = "xxxx";
    private Location mCurrentLocation;
    private LocationManager locationManager;
    private ImageButton giveRide;
    ScheduledFuture<?> future = null;
    private ScheduledExecutorService scheduler = null;
    String networkServiceState = "IN SERVICE", callStatus = "IDLE", dataConnectionState = "Connected";
    int signalStrength = 10;
    TelephonyManager telephonyManager;

    public static GiveRideTakeRideActivity instance() {
        return activeInstance;
    }


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
        scheduler =
                Executors.newScheduledThreadPool(1);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            applicationVersionName = pInfo.versionName;
            applicationVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        applicationVersionFromStrings = getString(R.string.app_version);
        appVersionTextView.setText(applicationVersionName + "~" + applicationVersionCode + "~" + applicationVersionFromStrings);

        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,
                phoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                        phoneStateListener.LISTEN_CALL_STATE |
                        phoneStateListener.LISTEN_CELL_INFO |
                        phoneStateListener.LISTEN_CELL_LOCATION |
                        phoneStateListener.LISTEN_DATA_ACTIVITY |
                        phoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                        phoneStateListener.LISTEN_CELL_INFO |
                        phoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                        phoneStateListener.LISTEN_SERVICE_STATE |
                        phoneStateListener.LISTEN_SIGNAL_STRENGTHS
        );

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GooglePlayServices not available
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
        hailSystemButton = (Button) findViewById(R.id.hailSystemButton);
        hailSystemButton.setOnClickListener(this);
        giveRideTakeRideLinearLayout = (LinearLayout) findViewById(R.id.give_ride_take_ride_linear_layout);
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
        activeInstance = this;
        cleanFuture();
        future = scheduler.scheduleAtFixedRate(new StoreUserLocation(), 30, 60, TimeUnit.SECONDS);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        activeInstance = null;
        cleanFuture();
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
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.hailSystemButton:
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
                                launchActivity(HailCustomerActivity.class);
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
        fusedCurrentLocation.setLatitude(location.getLatitude());
        fusedCurrentLocation.setLongitude(location.getLongitude());
        Log.d(TAG, "fusedCurrentLocation is:" + fusedCurrentLocation);
        if (googleMap != null && fusedCurrentLocation != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(fusedCurrentLocation.getLatitude(), fusedCurrentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fusedCurrentLocation.getLatitude(), fusedCurrentLocation.getLongitude()), 16.0f));
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

    private class StoreUserLocation implements Runnable {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String addressLocationString = "NA";
                    if (LocationDetails.isValid(fusedCurrentLocation)){
                        LocationDetails.storeUserLastKnownLocation(GiveRideTakeRideActivity.this,fusedCurrentLocation.getLatitude(),fusedCurrentLocation.getLongitude());
                        addressLocationString = LocationDetails.getCompleteAddressString(GiveRideTakeRideActivity.this,fusedCurrentLocation.getLatitude(),fusedCurrentLocation.getLongitude());
                    } else if (LocationDetails.isValid(mCurrentLocation)){
                        LocationDetails.storeUserLastKnownLocation(GiveRideTakeRideActivity.this,mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                        addressLocationString = LocationDetails.getCompleteAddressString(GiveRideTakeRideActivity.this,mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                    }
                    UserProfile userProfile = new UserProfile();
                    userProfile.setMobileBatteryLevel(batteryLevelPercentage());
                    userProfile.setMobileSignalLevel(signalStrength);
                    userProfile.setMobileCallStatus(callStatus);
                    userProfile.setMobileNetworkOperator(telephonyManager.getNetworkOperatorName());
                    userProfile.setMobileServiceState(networkServiceState);
                    userProfile.setMobileOperatingSystem(android.os.Build.VERSION.RELEASE);
                    userProfile.setMobileIMEI(telephonyManager.getDeviceId());
                    userProfile.setMobileBrand(android.os.Build.BRAND);
                    userProfile.setMobileModel(android.os.Build.MODEL);
                    userProfile.setLastKnownAddress(addressLocationString);
                    userProfile.setMobileDataConnection(dataConnectionState);
                    LocationDetails.storeMobileTrackingInfo(GiveRideTakeRideActivity.this,userProfile);
                }
            });
        }
    }

    private void cleanFuture() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    private int batteryLevelPercentage() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, intentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        return (int)(batteryPct*100);
    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            super.onCallForwardingIndicatorChanged(cfi);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //checkInternetConnection();
            String callState = "UNKNOWN";
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    callState = "IDLE";
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    callState = "Ringing (" + incomingNumber + ")";
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callState = "Offhook";
                    break;
            }
            Log.i("Phone Stats", "onCallStateChanged " + callState);
            callStatus = callState;
            super.onCallStateChanged(state, incomingNumber);
        }

        public void onCellLocationChanged(CellLocation location) {
            String cellLocationString = location.toString();
            System.out.println("cell location : "+cellLocationString);
            super.onCellLocationChanged(location);
        }

        @Override
        public void onDataActivity(int direction) {
            String directionString = "none";
            switch (direction) {
                case TelephonyManager.DATA_ACTIVITY_IN:
                    directionString = "IN";
                    break;
                case TelephonyManager.DATA_ACTIVITY_OUT:
                    directionString = "OUT";
                    break;
                case TelephonyManager.DATA_ACTIVITY_INOUT:
                    directionString = "INOUT";
                    break;
                case TelephonyManager.DATA_ACTIVITY_NONE:
                    directionString = "NONE";
                    break;
                default:
                    directionString = "UNKNOWN: " + direction;
                    break;
            }
            Log.i("Phone Stats", "onDataActivity " + directionString);
            super.onDataActivity(direction);
        }

        @Override
        public void onDataConnectionStateChanged(int state,int networktype) {
            String connectionState = "Unknown";
            switch (state) {
                case TelephonyManager.DATA_CONNECTED :
                    connectionState = "Connected";
                    break;
                case TelephonyManager.DATA_CONNECTING:
                    connectionState = "Connecting";
                    break;
                case TelephonyManager.DATA_DISCONNECTED:
                    connectionState = "Disconnected";
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    connectionState = "Suspended";
                    break;
                default:
                    connectionState = "Unknown: " + state;
                    break;
            }
            super.onDataConnectionStateChanged(state);
            Log.i("Phone Stats", "onDataConnectionStateChanged "
                    + connectionState);
            dataConnectionState = connectionState;
        }

        @Override
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            super.onMessageWaitingIndicatorChanged(mwi);
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            String serviceStateString = "UNKNOWN";
            switch (serviceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    serviceStateString = "IN SERVICE";
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    serviceStateString = "EMERGENCY ONLY";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    serviceStateString = "OUT OF SERVICE";
                    break;
                case ServiceState.STATE_POWER_OFF:
                    serviceStateString = "POWER OFF";
                    break;
                default:
                    serviceStateString = "UNKNOWN";
                    break;
            }
            Log.i("Phone Stats", "onServiceStateChanged " + serviceStateString);
            networkServiceState = serviceStateString;
            super.onServiceStateChanged(serviceState);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.i("Phone Stats", "onSignalStrengthChanged " + signalStrength);
            setSignalLevel(signalStrength.getLevel());
            super.onSignalStrengthsChanged(signalStrength);
        }
        private void setSignalLevel(int level) {
            int sLevel = (int) ((level / 31.0) * 100);
            Log.i("signalLevel ", "" + sLevel);
            signalStrength = sLevel;
        }
    };

}
