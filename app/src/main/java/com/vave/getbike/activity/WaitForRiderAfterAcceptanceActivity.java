package com.vave.getbike.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.vave.getbike.model.Profile;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import static com.vave.getbike.utils.GetBikeUtils.distanceInKms;
import static com.vave.getbike.utils.GetBikeUtils.round2;

public class WaitForRiderAfterAcceptanceActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    static WaitForRiderAfterAcceptanceActivity activeInstance;
    GoogleMap googleMap;
    long rideId;
    TextView allottedRiderDetailsView, timeInMinutesView;
    ImageButton callRider = null, showVehicleDetails = null, cancelRide = null;
    Profile riderProfile = null;
    private Ride ride = null;
    private LocationManager locationManager;

    public static WaitForRiderAfterAcceptanceActivity instance() {
        return activeInstance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeInstance = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeInstance = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetBikePreferences.setPreferences(getApplicationContext());
        BaseSyncher.setAccessToken(GetBikePreferences.getAccessToken());
        setContentView(R.layout.activity_wait_for_rider_after_acceptance);
        rideId = getIntent().getLongExtra("rideId", 0L);
        allottedRiderDetailsView = (TextView) findViewById(R.id.allottedRiderDetails);
        timeInMinutesView = (TextView) findViewById(R.id.timeInMinutes);
        callRider = (ImageButton) findViewById(R.id.callRider);
        callRider.setOnClickListener(this);
        cancelRide = (ImageButton) findViewById(R.id.cancelRide);
        cancelRide.setOnClickListener(this);
        showVehicleDetails = (ImageButton) findViewById(R.id.showVehicle);
        showVehicleDetails.setOnClickListener(this);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        new GetBikeAsyncTask(WaitForRiderAfterAcceptanceActivity.this) {

            @Override
            public void process() {
                riderProfile = null;
                ride = null;
                RideSyncher rideSyncher = new RideSyncher();
                ride = rideSyncher.getRideById(rideId);
                if (ride != null) {
                    riderProfile = new LoginSyncher().getPublicProfile(ride.getRiderId());
                }
            }

            @Override
            public void afterPostExecute() {
                if (googleMap != null && rideId > 0 && ride != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    LatLng startLatLng = new LatLng(ride.getStartLatitude(), ride.getStartLongitude());
                    builder.include(startLatLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                    googleMap.animateCamera(cameraUpdate);
                    googleMap.addMarker(new MarkerOptions()
                            .position(startLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
                    if (riderProfile != null) {
                        allottedRiderDetailsView.setText(riderProfile.getName() + "\n" + riderProfile.getPhoneNumber() + "\n" + riderProfile.getVehicleNumber());
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callRider: {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + riderProfile.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(WaitForRiderAfterAcceptanceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
                break;
            }
            case R.id.showVehicle: {
                final Dialog dialog = new Dialog(WaitForRiderAfterAcceptanceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.vehicle_details_dialogue);
                ImageView picture = (ImageView) dialog.findViewById(R.id.vehiclePicture);
                TextView number = (TextView) dialog.findViewById(R.id.vehicleNumber);
                Button close = (Button) dialog.findViewById(R.id.close);
                if (riderProfile != null) {
                    number.setText("" + riderProfile.getVehicleNumber());
                    Picasso.with(this).load(BaseSyncher.BASE_URL + "/" + riderProfile.getVehiclePlateImageName()).placeholder(R.drawable.picture).into(picture);

                }
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
            }
            case R.id.cancelRide: {
                new GetBikeAsyncTask(WaitForRiderAfterAcceptanceActivity.this) {
                    boolean result = false;

                    @Override
                    public void process() {
                        RideSyncher rideSyncher = new RideSyncher();
                        result = rideSyncher.cancelRide(rideId);
                    }

                    @Override
                    public void afterPostExecute() {
                        if (result) {
                            ToastHelper.blueToast(WaitForRiderAfterAcceptanceActivity.this, "Successfully cancelled the ride.");
                            finish();
                        } else {
                            ToastHelper.redToast(WaitForRiderAfterAcceptanceActivity.this, "Failed to cancel the ride.");
                        }
                    }
                }.execute();
                break;
            }

        }
    }

    public void rideCompleted(long rideId) {
        if (rideId == ride.getId()) {
            Intent intent = new Intent(WaitForRiderAfterAcceptanceActivity.this, ShowCompletedRideActivity.class);
            intent.putExtra("rideId", ride.getId());
            startActivity(intent);
            finish();
        }
    }

    public void processRiderLocation(final double latitude, final double longtiude, final boolean rideStarted, long rideId) {
        if (ride != null && ride.getId() == rideId) {
            if (googleMap != null && locationManager != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Location customerLocation = LocationDetails.getLocationOrShowToast(WaitForRiderAfterAcceptanceActivity.this, locationManager);
                        if (customerLocation != null) {
                            googleMap.clear();
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            LatLng customerLatLng = new LatLng(customerLocation.getLatitude(), customerLocation.getLongitude());
                            builder.include(customerLatLng);
                            LatLng riderLatLng = new LatLng(latitude, longtiude);
                            builder.include(riderLatLng);
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 15);
                            googleMap.animateCamera(cameraUpdate);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(riderLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.top_view_bike_icon)));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(customerLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
                            timeInMinutesView.setText(round2(distanceInKms(customerLocation.getLatitude(), customerLocation.getLongitude(), latitude, longtiude)) + " kms away");
                            if (rideStarted) {
                                timeInMinutesView.setText("Ride Started");
                                cancelRide.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                });
            }
        }
    }
}
