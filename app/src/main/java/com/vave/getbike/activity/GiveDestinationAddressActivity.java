package com.vave.getbike.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GMapV2Direction;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikeTextWatcher;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.LocationSyncher;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Ride;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.syncher.RideSyncher;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.vave.getbike.utils.GetBikeUtils.trimList;

public class GiveDestinationAddressActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    public static final int MAX_DIRECTION_POINTS = 40;
    GoogleMap googleMap;
    TextView yourLocation;
    LatLng yourLocationLatLng;
    TextView rideEstimateTextView;
    AutoCompleteTextView destination;
    GMapV2Direction googleMapV2Direction;
    ImageButton takeRideButton;
    LatLng destPosition;
    Document document;
    Polyline polyline;
    List<String> locations = new ArrayList<String>();
    AlertDialog alertDialog1;
    CharSequence[] values = {" Cash ", " Paytm ", " PayU "};
    String modeOfPayment = "Cash";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_destination_address);
        yourLocation = (TextView) findViewById(R.id.yourLocation);
        destination = (AutoCompleteTextView) findViewById(R.id.destination);
        rideEstimateTextView = (TextView) findViewById(R.id.rideEstimate);
        takeRideButton = (ImageButton) findViewById(R.id.takeRide);
        takeRideButton.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        yourLocationLatLng = new LatLng(latitude, longitude);
        if (latitude > 0 && longitude > 0) {
            yourLocation.setText(getCompleteAddressString(latitude, longitude));
        } else {
            Location mCurrentLocation = LocationDetails.getLocationOrShowToast(GiveDestinationAddressActivity.this, locationManager);
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            if (mCurrentLocation != null && mCurrentLocation.getLatitude() > 0 && mCurrentLocation.getLongitude() > 0) {
                yourLocation.setText(getCompleteAddressString(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            }
        }
        googleMapV2Direction = new GMapV2Direction();
        addTextChangedListener();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (googleMap != null && yourLocationLatLng != null && yourLocationLatLng.latitude != 0.0 && yourLocationLatLng.longitude != 0.0) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(yourLocationLatLng.latitude, yourLocationLatLng.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike_pointer)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourLocationLatLng, 16.0f));
        }

    }

    private void addTextChangedListener() {

        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                new GetBikeAsyncTask(GiveDestinationAddressActivity.this) {

                    LocationDetails fromLocationDetailsByName = null;

                    @Override
                    public void process() {
                        LocationSyncher locationSyncher = new LocationSyncher();
                        fromLocationDetailsByName = locationSyncher
                                .getLocationDetailsByNameRecursive(destination
                                        .getText().toString());
                    }

                    @Override
                    public void afterPostExecute() {
                        if (fromLocationDetailsByName != null) {
                            destPosition = new LatLng(fromLocationDetailsByName.getLatitude(), fromLocationDetailsByName.getLongitude());
                            drawMarker(destPosition, false);
                            drawPath();

                        } else {
                            ToastHelper.redToast(getApplicationContext(),
                                    "Location details not found");
                        }
                    }
                }.execute();
            }
        });
        destination.addTextChangedListener(new GetBikeTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                GetBikeAsyncTask asyncTask = new GetBikeAsyncTask(GiveDestinationAddressActivity.this) {
                    @Override
                    public void process() {
                        locations.clear();
                        String key = destination.getText().toString();
                        if (key.length() >= 3) {
                            LocationSyncher locationSyncher = new LocationSyncher(
                                    key);
                            locations = locationSyncher.getLocations();
                        }
                    }

                    @Override
                    public void afterPostExecute() {
                        String[] countries = locations
                                .toArray(new String[locations.size()]);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                R.layout.package_spinner_item, countries);
                        destination.setAdapter(arrayAdapter);
                    }

                };
                asyncTask.setShowProgress(false);
                asyncTask.execute();

            }
        });
    }

    private void drawMarker(LatLng point, boolean isDestination) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        BitmapDescriptor fromResource = BitmapDescriptorFactory
                .fromResource(isDestination ? R.mipmap.location_pointer : R.mipmap.location_pointer);
        googleMap
                .addMarker(markerOptions.icon(fromResource));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
    }

    private void drawPath() {
        if (yourLocationLatLng != null && destPosition != null) {
            hideKeyBoard();
            new GetBikeAsyncTask(GiveDestinationAddressActivity.this) {
                ArrayList<LatLng> directionPoints = null;
                Ride estimatedRide = null;

                @Override
                public void process() {
                    document = googleMapV2Direction.getDocument(yourLocationLatLng,
                            destPosition, GMapV2Direction.MODE_DRIVING);
                    directionPoints = googleMapV2Direction
                            .getDirection(document);
                    RideSyncher rideSyncher = new RideSyncher();
                    List<LatLng> trimmedList = trimList(directionPoints, MAX_DIRECTION_POINTS);
                    ArrayList<RideLocation> rideLocations = new ArrayList<RideLocation>();
                    for (LatLng directionPoint : trimmedList) {
                        rideLocations.add(getRideLocation(directionPoint));
                    }
                    estimatedRide = rideSyncher.estimateRide(rideLocations);
                }

                @NonNull
                public RideLocation getRideLocation(LatLng directionPoint) {
                    RideLocation rideLocation = new RideLocation();
                    rideLocation.setLatitude(directionPoint.latitude);
                    rideLocation.setLongitude(directionPoint.longitude);
                    return rideLocation;
                }

                @Override
                public void afterPostExecute() {
                    if (estimatedRide == null) {
                        ToastHelper.redToast(GiveDestinationAddressActivity.this, "Failed to estimate ride");
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (estimatedRide != null) {
                        rideEstimateTextView.setVisibility(View.VISIBLE);
                        rideEstimateTextView.setText("Estimated ₹ " + estimatedRide.getOrderAmount() + " for " + estimatedRide.getOrderDistance() + " km");

                        PolylineOptions rectLine = new PolylineOptions().width(3)
                                .color(Color.RED);
                        for (int i = 0; i < directionPoints.size(); i++) {
                            rectLine.add(directionPoints.get(i));
                        }
                        polyline = googleMap.addPolyline(rectLine);
                        polyline.setColor(Color.parseColor("#FFA500"));
                        polyline.setWidth(10f);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        builder.include(yourLocationLatLng);
                        builder.include(destPosition);
                        LatLngBounds bounds = builder.build();
                        int padding = 100;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cu);
                    }
                }
            }.execute();
        } else {
            ToastHelper.redToast(getApplicationContext(),
                    "Please select from and to locations.");
        }
    }

    public void hideKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takeRide:
                AlertDialog.Builder builder = new AlertDialog.Builder(GiveDestinationAddressActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Select Your Mode Of Payment");
                builder.setSingleChoiceItems(values, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                modeOfPayment = "Cash";
                                break;
                            case 1:
                                modeOfPayment = "Paytm";
                                break;
                            case 2:
                                modeOfPayment = "PayU";
                                break;
                        }
                    }
                });
                builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(GiveDestinationAddressActivity.this, "Selected mode of payment is:" + modeOfPayment, Toast.LENGTH_LONG).show();
                        new GetBikeAsyncTask(GiveDestinationAddressActivity.this) {
                            Long rideID = null;

                            @Override
                            public void process() {
                                RideSyncher sut = new RideSyncher();
                                Ride ride = sut.requestRide(yourLocationLatLng.latitude, yourLocationLatLng.longitude, yourLocation.getText().toString(), destination.getText().toString(), modeOfPayment);
                                rideID = ride.getId();
                            }

                            @Override
                            public void afterPostExecute() {
                                if (rideID != null) {
                                    Intent intent = new Intent(GiveDestinationAddressActivity.this, WaitForRiderAllocationActivity.class);
                                    intent.putExtra("rideId", rideID);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    ToastHelper.redToast(GiveDestinationAddressActivity.this, "Failed to book a ride, please retry.");
                                }
                            }
                        }.execute();
                        dialog.dismiss();
                    }
                });
                alertDialog1 = builder.create();
                alertDialog1.show();
                break;
        }
    }
}
