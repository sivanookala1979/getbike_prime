package com.vave.getbike.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.GroupRideRouteAdapter;
import com.vave.getbike.adapter.RideAdapter2;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.GroupRider;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.util.ArrayList;
import java.util.Date;

public class GroupRideRouteMapActivity extends BaseActivity implements View.OnClickListener {
    private long groupRideId;
    Button startgroupRide;
    ListView groupRideRoute;
    GroupRideRouteAdapter adapter;
    GroupRider riderLocations = new GroupRider();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_ride_route_map);
        addToolbarView();
        groupRideId = getIntent().getLongExtra("rideId", 0L);
        startgroupRide = (Button) findViewById(R.id.start_group_ride);
        groupRideRoute = (ListView) findViewById(R.id.groupRideRoute);
        startgroupRide.setOnClickListener(this);

        getRiderLocations();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.start_group_ride){
            Intent intent = new Intent(GroupRideRouteMapActivity.this, GroupRideLocationActivity.class);
            intent.putExtra("groupId", riderLocations.getGroupId());
            intent.putExtra("groupRideId", riderLocations.getGroupRiderId());
            startActivity(intent);
            finish();
        }
    }

    private void getRiderLocations() {

            new GetBikeAsyncTask(GroupRideRouteMapActivity.this) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    riderLocations = rideSyncher.getRiderLocations(groupRideId);
                }

                @Override
                public void afterPostExecute() {
                    if (riderLocations != null) {
                        adapter = new GroupRideRouteAdapter(getApplicationContext(), riderLocations.getGroupRiderLocations());
                        groupRideRoute.setAdapter(adapter);
                    } else{
                        ToastHelper.blueToast(GroupRideRouteMapActivity.this, R.string.message_no_open_rides);
                    }
                }
            }.execute();
        }
    }
