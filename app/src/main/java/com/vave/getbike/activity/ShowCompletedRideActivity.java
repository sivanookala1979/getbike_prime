package com.vave.getbike.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.RideSyncher;
import com.vave.getbike.utils.HTTPUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.vave.getbike.utils.StringUtils.isStringValid;

public class ShowCompletedRideActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final int GALLERY_REQUET_CODE = 11111;
    private static final int CAMERA_REQUEST_CODE = 100;
    String customerImageBitmapToString;
    TextView tripDateTime, tripId, userName, bikeType, fromTime, toTime, fromAddress, toAddress, totalFare, taxAndFee, subTotal, roundingOff, totalBill, cash, currentRatingStatus, freeRideDiscount;
    LinearLayout freeRideDiscountPanel;
    RatingBar ratingBar;
    Button payThroughWalletButton, amountReceivedFromCustomerButton;
    private GoogleMap mMap;
    private Ride ride = null;
    private long rideId;
    LinearLayout galleryView, camera,uploadBillPictureLayout;
    ImageView parcelBillPhoto;
    Button uploadBillPictureButton;
    private List<RideLocation> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetBikePreferences.setPreferences(getApplicationContext());
        BaseSyncher.setAccessToken(GetBikePreferences.getAccessToken());
        setContentView(R.layout.trip_details_screen);

        addToolbarView();
        freeRideDiscountPanel = (LinearLayout) findViewById(R.id.freeRideDiscountPanel);
        tripDateTime = (TextView) findViewById(R.id.tipDateTime);
        tripId = (TextView) findViewById(R.id.tripId);
        galleryView = (LinearLayout) findViewById(R.id.gallery);
        camera = (LinearLayout) findViewById(R.id.camera);
        uploadBillPictureLayout = (LinearLayout) findViewById(R.id.uploadBillPictureLayout);
        parcelBillPhoto = (ImageView) findViewById(R.id.parcelBillPhoto);
        uploadBillPictureButton = (Button) findViewById(R.id.uploadBillPictureButton);
        galleryView.setOnClickListener(this);
        camera.setOnClickListener(this);
        uploadBillPictureButton.setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userName = (TextView) findViewById(R.id.userName);
        bikeType = (TextView) findViewById(R.id.bikeType);
        fromTime = (TextView) findViewById(R.id.fromTime);
        toTime = (TextView) findViewById(R.id.toTime);
        fromAddress = (TextView) findViewById(R.id.fromAddress);
        toAddress = (TextView) findViewById(R.id.toAddress);
        totalFare = (TextView) findViewById(R.id.totalFare);
        taxAndFee = (TextView) findViewById(R.id.taxFee);
        subTotal = (TextView) findViewById(R.id.subTotal);
        roundingOff = (TextView) findViewById(R.id.roundingOff);
        totalBill = (TextView) findViewById(R.id.totalBill);
        cash = (TextView) findViewById(R.id.cashAmount);
        payThroughWalletButton = (Button) findViewById(R.id.payThroughWalletButton);
        amountReceivedFromCustomerButton = (Button) findViewById(R.id.amountReceivedFromCustomerButton);
        currentRatingStatus = (TextView) findViewById(R.id.currentRatingStatus);
        freeRideDiscount = (TextView) findViewById(R.id.freeRideDiscount);
        rideId = getIntent().getLongExtra("rideId", 0L);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        payThroughWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("PayU".equals(ride.getModeOfPayment()) || "Paytm".equals(ride.getModeOfPayment())) {
                    Intent intent = new Intent(ShowCompletedRideActivity.this, PayUPaymentActivity.class);
                    intent.putExtra("rideId", ride.getId());
                    intent.putExtra("isRideOnline", true);
                    // TODO: 23/02/17 This is duplicate and complicated code. We need to make this better.
                    if (!ride.isFreeRide()) {
                        intent.putExtra("fareAmount", ride.getTotalBill());
                    } else if (ride.isFreeRide() && (ride.getTotalBill() - ride.getFreeRideDiscount()) > 0.0) {
                        intent.putExtra("fareAmount", (ride.getTotalBill() - ride.getFreeRideDiscount()));
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
        amountReceivedFromCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make isPaid as true in this region;
                new GetBikeAsyncTask(ShowCompletedRideActivity.this) {

                    @Override
                    public void process() {
                        new RideSyncher().updatePaymentStatus(rideId);
                    }

                    @Override
                    public void afterPostExecute() {

                    }
                }.execute();
                finish();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                new GetBikeAsyncTask(ShowCompletedRideActivity.this) {

                    @Override
                    public void process() {
                        new RideSyncher().rateRide(rideId, (int) rating);
                    }

                    @Override
                    public void afterPostExecute() {

                    }
                }.execute();
            }
        });
    }

    public void updateRideDetails() {
        String indianRupee = getApplicationContext().getResources().getString(R.string.Rs);
        if (ride != null) {
            // TODO: 23/02/17 This is duplicate and complicated code. We need to make this better.
            if (ride.isUserCustomer() && ride.getTotalBill() > 0.0 && (!(ride.isPaid())) && ("PayU".equals(ride.getModeOfPayment()) || "Paytm".equals(ride.getModeOfPayment()))) {
                if (!ride.isFreeRide()) {
                    payThroughWalletButton.setVisibility(View.VISIBLE);
                } else if (ride.isFreeRide() && (ride.getTotalBill() - ride.getFreeRideDiscount()) > 0.0) {
                    payThroughWalletButton.setVisibility(View.VISIBLE);
                }
            } else if (ride.isUserRider() && (!ride.isPaid())) {
                amountReceivedFromCustomerButton.setVisibility(View.VISIBLE);
            }
            SimpleDateFormat onlyTimeFormat = new SimpleDateFormat("h:mm a");
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, MMM dd,h:mm a");
            userName.setText("" + ride.getRequestorName());
            totalFare.setText(indianRupee + " " + ride.getTotalFare());
            taxAndFee.setText(indianRupee + " " + ride.getTaxesAndFees());
            subTotal.setText(indianRupee + " " + ride.getSubTotal());
            roundingOff.setText(indianRupee + " " + ride.getRoundingOff());
            totalBill.setText(indianRupee + " " + ride.getTotalBill());
            cash.setText(indianRupee + " " + ride.getTotalBill());
            if (isStringValid(ride.getActualSourceAddress())) {
                fromAddress.setText(ride.getActualSourceAddress());
            } else {
                fromAddress.setText(ride.getSourceAddress());
            }
            if (isStringValid(ride.getActualDestinationAddress())) {
                toAddress.setText(ride.getActualDestinationAddress());
            } else {
                toAddress.setText(ride.getDestinationAddress());
            }
            tripId.setText("Trip ID : " + ride.getId());
            if (ride.getRideStartedAt() != null) {
                fromTime.setText(onlyTimeFormat.format(ride.getRideStartedAt()));
                tripDateTime.setText(fullDateFormat.format(ride.getRideStartedAt()));
            }
            if (ride.getRideEndedAt() != null) {
                toTime.setText(onlyTimeFormat.format(ride.getRideEndedAt()));
            }
            if (ride.getRating() != null && ride.getRating() > 0) {
                ratingBar.setRating(ride.getRating());
                currentRatingStatus.setText("You rated");
            } else {
                currentRatingStatus.setText("Please rate your ride");
            }
            if (ride.isFreeRide()) {
                freeRideDiscountPanel.setVisibility(View.VISIBLE);
                freeRideDiscount.setText(indianRupee + " " + ride.getFreeRideDiscount());
                cash.setText(indianRupee + " " + Math.max(0.0, ride.getTotalBill() - ride.getFreeRideDiscount()));
            }
            if (isStringValid(ride.getParcelDropoffImageName())){
                String imageUrl = BaseSyncher.BASE_URL + "/" + ride.getParcelDropoffImageName();
                Picasso.with(ShowCompletedRideActivity.this).load(imageUrl).placeholder(R.drawable.picture).into(parcelBillPhoto);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (rideId > 0) {
            new GetBikeAsyncTask(ShowCompletedRideActivity.this) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    locations.clear();
                    ride = rideSyncher.getCompleteRideById(rideId, locations);
                }

                @Override
                public void afterPostExecute() {
                    updateRideDetails();
                    if (mMap != null && rideId > 0 && locations.size() > 0) {
                        PolylineOptions polylineOptions = new PolylineOptions();
                        LatLng[] latLngs = new LatLng[locations.size()];
                        int i = 0;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for (RideLocation location : locations) {
                            latLngs[i] = new LatLng(location.getLatitude(), location.getLongitude());
                            builder.include(latLngs[i]);
                            i++;
                        }
                        LatLngBounds bounds = builder.build();

                        mMap.addPolyline(polylineOptions
                                .add(latLngs)
                                .width(5)
                                .color(Color.parseColor("#FFA500")));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                        mMap.animateCamera(cameraUpdate);
                    }
                }

            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gallery:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUET_CODE);
                break;
            case R.id.camera:
                Intent takePicture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
                break;
            case R.id.uploadBillPictureButton:
                if (customerImageBitmapToString != null){
                    new GetBikeAsyncTask(ShowCompletedRideActivity.this) {
                        boolean result = false;

                        @Override
                        public void process() {
                            result = new RideSyncher().storeParcelBillPhoto(customerImageBitmapToString,ride.getId());
                        }

                        @Override
                        public void afterPostExecute() {
                            if (result) {
                                ToastHelper.redToast(ShowCompletedRideActivity.this, "Data successfully uploaded.");
                            } else {
                                ToastHelper.redToast(ShowCompletedRideActivity.this, "Failed to upload data try again.");
                            }
                        }
                    }.execute();
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bitmap bitmap;
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                parcelBillPhoto.setImageBitmap(bitmap);
            }
            if (requestCode == GALLERY_REQUET_CODE && resultCode == Activity.RESULT_OK) {
                bitmap = HTTPUtils.getBitmapFromCameraData(data, getBaseContext());
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                parcelBillPhoto.setImageBitmap(bitmap);
            }
        }
    }
}
