package com.vave.getbike.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.RideAdapter2;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OpenRidesActivity extends BaseActivity {

    // UI Widgets
    ListView openRidesListView;
    TextView hailModelTextView;
    List<Ride> result = null;
    Button refreshButton;
    ScheduledFuture<?> s = null;
    private ScheduledExecutorService scheduler = null;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduler =
                Executors.newScheduledThreadPool(1);
        setContentView(R.layout.activity_open_rides);
        addToolbarView();
        openRidesListView = (ListView) findViewById(R.id.openRides);
        refreshButton = (Button) findViewById(R.id.refreshOpenRides);
        final double latitude = getIntent().getDoubleExtra("latitude", 0);
        final double longitude = getIntent().getDoubleExtra("longitude", 0);
        hailModelTextView = (TextView) findViewById(R.id.hail_model_text_view);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadOpenRides();
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        openRidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (result != null) {
                    Intent intent = new Intent(OpenRidesActivity.this, AcceptRejectRideActivity.class);
                    intent.putExtra("rideId", result.get(position).getId());
                    startActivity(intent);
                    finish();
                }
            }
        });

        hailModelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpenRidesActivity.this, HailCustomerActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
                finish();
            }
        });

        s = scheduler.scheduleAtFixedRate(new Runnable() {
                                              @Override
                                              public void run() {
                                                  System.out.println("Called the scheduler");
                                                  runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          System.out.println("Called the scheduler on ui thread");
                                                          reloadOpenRides();
                                                      }
                                                  });
                                              }
                                          },
                0, 60,
                TimeUnit.SECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadOpenRides();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (s != null) {
            s.cancel(true);
        }
    }

    private void reloadOpenRides() {
        final Location mCurrentLocation = LocationDetails.getLocationOrShowToast(OpenRidesActivity.this, locationManager);
        if (LocationDetails.isValid(mCurrentLocation)) {

            new GetBikeAsyncTask(OpenRidesActivity.this) {

                @Override
                public void process() {
                    LoginSyncher loginSyncher = new LoginSyncher();
                    RideSyncher rideSyncher = new RideSyncher();
                    loginSyncher.storeLastKnownLocation(new Date(), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    result = rideSyncher.openRides(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                }

                @Override
                public void afterPostExecute() {
                    if (result != null) {
                        openRidesListView.setAdapter(new RideAdapter2(OpenRidesActivity.this, result, true));
                        if (result.size() == 0) {
                            ToastHelper.blueToast(OpenRidesActivity.this, R.string.message_no_open_rides);
                        }
                    }
                }
            }.execute();
        }
    }

}
