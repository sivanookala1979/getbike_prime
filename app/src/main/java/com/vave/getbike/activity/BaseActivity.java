package com.vave.getbike.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.CircleTransform;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;

/**
 * Created by adarsht on 30/11/16.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ImageView userProfileImage;
    public TextView mobileNumber;
    public TextView userName;
    LoginSyncher loginSyncher = new LoginSyncher();
    UserProfile userProfile = new UserProfile();

    public void addToolbarView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getProfileDetails() {
        new GetBikeAsyncTask(BaseActivity.this) {

            @Override
            public void process() {
                userProfile = loginSyncher.getUserProfile();
            }

            @Override
            public void afterPostExecute() {
                if (userProfile != null) {
                    userName.setText(userProfile.getName());
                    mobileNumber.setText(userProfile.getPhoneNumber());
                    if (userProfile.getProfileImage() != null) {
                        Picasso.with(getApplicationContext()).load(BaseSyncher.BASE_URL + "/" + userProfile.getProfileImage()).transform(new CircleTransform()).placeholder(R.drawable.male_profile_icon).into(userProfileImage);
                    }
                } else {
                    userProfile = new UserProfile();
                }
                GetBikePreferences.setUserProfile(userProfile);
            }
        }.execute();
    }

    public String getCurrencySymbol() {
        return getApplicationContext().getResources().getString(R.string.Rs);
    }

    public void addNavigationMenu(AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        GetBikePreferences.setPreferences(activity);
        userProfile = GetBikePreferences.getUserProfile();
        userProfileImage = (ImageView) headerView.findViewById(R.id.menu_profile_image);
        userName = (TextView) headerView.findViewById(R.id.menu_user_name);
        mobileNumber = (TextView) headerView.findViewById(R.id.menu_mobile_number);
        getProfileDetails();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.wallet:
                startActivity(new Intent(this, GetBikeWalletHome.class));
                break;
            case R.id.earnings:
                startActivity(new Intent(this,EarningsActivity.class));
                break;
            case R.id.history:
                startActivity(new Intent(this, ScheduledRidesAndHistoryActivity.class));
                break;
            case R.id.profilAndSettings:
                startActivity(new Intent(this, ProfileAndSettingsActivity.class));
                break;
            case R.id.bills:
                startActivity(new Intent(this, ViewRoasterRecordsActivity.class));
                break;
            case R.id.cashInAdvance:
                startActivity(new Intent(this,CashInAdvanceActivity.class));
                break;
            case R.id.leaveRequests:
                startActivity(new Intent(this,LeaveRequestsHistoryActivity.class));
                break;
            case R.id.groupRides:
                startActivity(new Intent(this,GroupRideDetailsActivity.class));
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
