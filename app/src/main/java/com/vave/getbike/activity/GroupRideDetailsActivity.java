package com.vave.getbike.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.GroupRideDetailsAdapter;
import com.vave.getbike.adapter.RideAdapter2;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.GroupRideDetails;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupRideDetailsActivity extends BaseActivity {

    List<Ride> groupRide = new ArrayList<>();
    ListView groupRides;
    GroupRideDetailsAdapter adapter;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_ride_details);
        addToolbarView();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        groupRides = (ListView) findViewById(R.id.groupRides);
        getGroupRides();
    }

    private void getGroupRides() {
        final Location mCurrentLocation = LocationDetails.getLocationOrShowToast(GroupRideDetailsActivity.this, locationManager);
        if (LocationDetails.isValid(mCurrentLocation)) {

            new GetBikeAsyncTask(GroupRideDetailsActivity.this) {

                @Override
                public void process() {
                    LoginSyncher loginSyncher = new LoginSyncher();
                    RideSyncher rideSyncher = new RideSyncher();
                    loginSyncher.storeLastKnownLocation(new Date(), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    groupRide = rideSyncher.openRides(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                }

                @Override
                public void afterPostExecute() {
                    if (groupRide != null) {
                        adapter = new GroupRideDetailsAdapter(getApplicationContext(), groupRide);
                        groupRides.setAdapter(adapter);
                        if (groupRide.size() == 0) {
                            ToastHelper.blueToast(GroupRideDetailsActivity.this, R.string.message_no_open_rides);
                        }
                    }
                }
            }.execute();
        }
    }
}
