package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.syncher.BaseSyncher;

public class SplashScreenActivity extends AppCompatActivity {

    public static final long DELAY_MILLIS = 4000L;
    Handler timerHandler; // global instance
    Runnable getmiRunnable; // global instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetBikePreferences.setPreferences(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.white));

        timerHandler = new Handler();
        getmiRunnable = new Runnable() {
            @Override
            public void run() {
                if (GetBikePreferences.isLoggedIn()) {
                    System.out.println("Control in the splash screen activity");
                    BaseSyncher.setAccessToken(GetBikePreferences.getAccessToken());
                    startActivity(new Intent(SplashScreenActivity.this, GiveRideTakeRideActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LogoScreenActivity.class));
                    finish();
                }
            }
        };
        timerHandler.postDelayed(getmiRunnable, DELAY_MILLIS);
    }

}
