package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.CircleTransform;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;

public class ProfileAndSettingsActivity extends BaseActivity implements View.OnClickListener {

    Button personalDetailsButton;
    UserProfile userProfile = new UserProfile();
    LoginSyncher loginSyncher = new LoginSyncher();
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetBikePreferences.setPreferences(getApplicationContext());
        setContentView(R.layout.activity_profile_and_settings);
        addToolbarView();
        profileImage = (ImageView) findViewById(R.id.profileIcon);
        setOnclickListeners(R.id.personalDetails, R.id.ridersProfile, R.id.bankAccountDetails, R.id.settings);
        getProfileDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileDetails();
    }

    private void getProfileDetails() {
        new GetBikeAsyncTask(ProfileAndSettingsActivity.this) {

            @Override
            public void process() {
                userProfile = loginSyncher.getUserProfile();
            }

            @Override
            public void afterPostExecute() {
                if (userProfile != null) {
                    Picasso.with(getApplicationContext()).load(BaseSyncher.BASE_URL + "/" + userProfile.getProfileImage()).transform(new CircleTransform()).placeholder(R.drawable.male_profile_icon).into(profileImage);
                } else {
                    userProfile = new UserProfile();
                }
                GetBikePreferences.setUserProfile(userProfile);
            }
        }.execute();
    }

    private void setOnclickListeners(int... ids) {
        for (int id : ids) {
            ImageView layout = (ImageView) findViewById(id);
            layout.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personalDetails:
                Intent intent = new Intent(ProfileAndSettingsActivity.this, PersonalDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.ridersProfile:
                startActivity(new Intent(this, RiderProfileActivity.class));
                break;
            case R.id.bankAccountDetails:
                startActivity(new Intent(this, BankAccountDetailsActivity.class));
                break;
            case R.id.settings:
                break;
        }
    }

}
