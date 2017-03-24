package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikePreferences;

public class RequestRideActivity extends BaseActivity implements View.OnClickListener {

    TextView getBikeResult;
    Button homeButton;
    Button showCurrentRideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        addNavigationMenu(this);
        getBikeResult = (TextView) findViewById(R.id.getBikeResult);
        showCurrentRideButton = (Button) findViewById(R.id.showCurrentRide);
        showCurrentRideButton.setOnClickListener(this);
        Button showOpenRides = (Button) findViewById(R.id.showOpenRides);
        showOpenRides.setOnClickListener(this);
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(this);
        Button myCompletedRides = (Button) findViewById(R.id.myCompletedRides);
        myCompletedRides.setOnClickListener(this);
        Button ridesGiven = (Button) findViewById(R.id.ridesGiven);
        ridesGiven.setOnClickListener(this);
        homeButton = (Button) findViewById(R.id.home);
        homeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showCurrentRide:

            case R.id.showOpenRides:
                startActivity(new Intent(this, OpenRidesActivity.class));
                break;
            case R.id.myCompletedRides:
                startActivity(new Intent(this, MyCompletedRidesActivity.class));
                break;
            case R.id.ridesGiven:
                startActivity(new Intent(this, RidesGivenByMeActivity.class));
                break;
            case R.id.home:
                startActivity(new Intent(this, GiveRideTakeRideActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(this, LogoScreenActivity.class));
                try {
                    InstanceID.getInstance(getApplicationContext()).deleteInstanceID();
                } catch (Exception ex) {
                    Log.e("InstanceID", ex.getMessage(), ex);
                }
                GetBikePreferences.reset();
                finish();
                break;
        }

    }

}
