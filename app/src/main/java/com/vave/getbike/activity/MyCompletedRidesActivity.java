package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.RideAdapter2;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.RideSyncher;

import java.util.List;

public class MyCompletedRidesActivity extends BaseActivity {

    // UI Widgets
    ListView myCompletedRidesListView;
    List<Ride> result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_completed_rides);
        addToolbarView();
        myCompletedRidesListView = (ListView) findViewById(R.id.myCompletedRides);
        new GetBikeAsyncTask(MyCompletedRidesActivity.this) {

            @Override
            public void process() {
                RideSyncher rideSyncher = new RideSyncher();
                result = rideSyncher.getMyCompletedRides();
            }

            @Override
            public void afterPostExecute() {
                if (result != null) {
                    myCompletedRidesListView.setAdapter(new RideAdapter2(MyCompletedRidesActivity.this, result, false));
                }
            }
        }.execute();
        myCompletedRidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (result != null) {
                    Intent intent = new Intent(MyCompletedRidesActivity.this, ShowCompletedRideActivity.class);
                    intent.putExtra("rideId", result.get(position).getId());
                    startActivity(intent);
                }
            }
        });
    }
}
