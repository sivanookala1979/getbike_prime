package com.vave.getbike.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;

public class AcceptRejectRideActivity extends BaseActivity implements View.OnClickListener {

    // UI Widgets.
    TextView rideRequestedBy;
    TextView rideRequestAddress;
    TextView rideRequestLatLng;
    TextView rideRequestMobileNumber;
    TextView rideDestination;
    TextView modeOfPayment;
    Button acceptRide;
    Button rejectRide;
    Button callRequestorButton;

    Ride ride = null;
    private long rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetBikePreferences.setPreferences(getApplicationContext());
        BaseSyncher.setAccessToken(GetBikePreferences.getAccessToken());
        setContentView(R.layout.activity_accept_reject_ride);
        addToolbarView();
        rideId = getIntent().getLongExtra("rideId", 0L);
        rideRequestedBy = (TextView) findViewById(R.id.rideRequestedBy);
        rideRequestAddress = (TextView) findViewById(R.id.rideRequestAddress);
        rideRequestLatLng = (TextView) findViewById(R.id.rideRequestLatLng);
        rideRequestMobileNumber = (TextView) findViewById(R.id.rideRequestMobileNumber);
        rideDestination = (TextView) findViewById(R.id.rideDestinationAddress);
        modeOfPayment = (TextView) findViewById(R.id.rideModeOfPayment);
        acceptRide = (Button) findViewById(R.id.acceptRide);
        rejectRide = (Button) findViewById(R.id.rejectRide);
        callRequestorButton = (Button) findViewById(R.id.callRideRequestor);
        callRequestorButton.setOnClickListener(this);
        if (rideId > 0) {
            new GetBikeAsyncTask(AcceptRejectRideActivity.this) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    ride = rideSyncher.getRideById(rideId);
                }

                @Override
                public void afterPostExecute() {
                    if (ride != null) {
                        if ("RideAccepted".equals(ride.getRideStatus())) {
                            showOpenRides(R.string.error_ride_is_already_allocated);
                        } else if ("RideCancelled".equals(ride.getRideStatus())) {
                            showOpenRides(R.string.error_ride_is_cancelled_by_customer);
                        } else {
                            rideRequestedBy.setText(ride.getRequestorName());
                            rideRequestAddress.setText(ride.getSourceAddress());
                            rideRequestLatLng.setText(ride.getStartLatitude() + "," + ride.getStartLongitude());
                            rideRequestMobileNumber.setText(ride.getRequestorPhoneNumber());
                            rideDestination.setText(ride.getDestinationAddress());
                            modeOfPayment.setText(ride.getModeOfPayment());
                        }
                    } else {
                        showOpenRides(R.string.error_ride_is_not_valid);
                    }
                }
            }.execute();
        }
        acceptRide.setOnClickListener(this);
        rejectRide.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acceptRide:
                new GetBikeAsyncTask(AcceptRejectRideActivity.this) {
                    CallStatus callStatus = null;

                    @Override
                    public void process() {
                        RideSyncher rideSyncher = new RideSyncher();
                        callStatus = rideSyncher.acceptRide(rideId);
                    }

                    @Override
                    public void afterPostExecute() {
                        if (callStatus != null && callStatus.isSuccess()) {
                            Intent intent = new Intent(AcceptRejectRideActivity.this, LocationActivity.class);
                            intent.putExtra("rideId", rideId);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = "Internal error occurred.";
                            String title = "Unknown error";
                            if (callStatus != null) {
                                if (callStatus.getErrorCode() == 9905) {
                                    message = "Please fill your Rider Profile to accept the ride, or wait for admin approval.";
                                    title = "Rider Profile";
                                } else if (callStatus.getErrorCode() == 9904) {
                                    message = "Ride is already allocated to someone else.";
                                    title = "Ride";
                                } else if (callStatus.getErrorCode() == 9903) {
                                    message = "You can't accept this ride, because you are already in ride.";
                                    title = "Ride";
                                } else if (callStatus.getErrorCode() == 9906) {
                                    message = "You can't accept your own ride request.";
                                    title = "Ride";
                                } else if (callStatus.getErrorCode() == 9907) {
                                    message = "Please add money to your wallet.";
                                    title = "Wallet";
                                }
                            }
                            final AlertDialog.Builder builder = new AlertDialog.Builder(AcceptRejectRideActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle(title);
                            builder.setMessage(message);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (callStatus != null && callStatus.getErrorCode() == 9905) {
                                        Intent intent = new Intent(AcceptRejectRideActivity.this, RiderProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (callStatus != null && callStatus.getErrorCode() == 9907) {
                                        Intent intent = new Intent(AcceptRejectRideActivity.this, GetBikeWalletHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(AcceptRejectRideActivity.this, OpenRidesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                            builder.show();
                        }
                    }
                }.execute();
                break;
            case R.id.rejectRide:
                showOpenRides(R.string.error_ride_is_rejected);
                break;
            case R.id.callRideRequestor: {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ride.getRequestorPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(AcceptRejectRideActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        }
    }

    public void showOpenRides(int errorId) {
        ToastHelper.redToast(AcceptRejectRideActivity.this, errorId);
        finish();
        Intent intent = new Intent(AcceptRejectRideActivity.this, OpenRidesActivity.class);
        startActivity(intent);
    }
}
