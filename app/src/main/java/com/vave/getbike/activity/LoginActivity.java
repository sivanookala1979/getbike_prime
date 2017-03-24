package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.SMSListener;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static LoginActivity inst;
    EditText receivedOtp;
    SMSListener smsListener;
    EditText mobileNumberTextView;
    EditText otpTextView;
    TextView signUpTextView;

    public static LoginActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inst == this) {
            inst = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Button sendOtp = (Button) findViewById(R.id.send_otp);
        assert sendOtp != null;
        sendOtp.setOnClickListener(this);
        mobileNumberTextView = (EditText) findViewById(R.id.mobile);
        otpTextView = (EditText) findViewById(R.id.received_otp);
        try {
            //TODO This should be placed in logout as well.
            InstanceID.getInstance(getApplicationContext()).deleteInstanceID();
        } catch (Exception ex) {
            Log.e("InstanceID", ex.getMessage(), ex);
        }
        Button login = (Button) findViewById(R.id.login);
        assert login != null;
        login.setOnClickListener(this);

        signUpTextView = (TextView) findViewById(R.id.sign_up_text_view);
        signUpTextView.setOnClickListener(this);
        receivedOtp = (EditText) findViewById(R.id.received_otp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_otp: {
                final String mobileNumber = ((TextView) findViewById(R.id.mobile)).getText() + "";
                final Pattern pattern1 = Pattern.compile("[^0-9]");
                boolean patternCheck = pattern1.matcher(mobileNumber).find();
                if ((mobileNumber.length() != 10) || (patternCheck)) {
                    mobileNumberTextView.setError("Required 10 digits");
                    mobileNumberTextView.requestFocus();
                } else {
                    new GetBikeAsyncTask(LoginActivity.this) {
                        boolean result = false;

                        @Override
                        public void process() {
                            LoginSyncher loginSyncher = new LoginSyncher();
                            result = loginSyncher.login(mobileNumber);
                        }

                        @Override
                        public void afterPostExecute() {
                            Log.d("TAG", "SEND otp button clicked result is:" + result);
                            if (!result) {
                                ToastHelper.redToast(getApplicationContext(), "unregistered mobile number!");
                            }
                        }
                    }.execute();
                }

            }
            break;
            case R.id.login: {
                final String mobileNumber = ((TextView) findViewById(R.id.mobile)).getText() + "";
                final String otp = ((TextView) findViewById(R.id.received_otp)).getText() + "";
                final Pattern pattern1 = Pattern.compile("[^0-9]");
                boolean patternCheck1 = pattern1.matcher(mobileNumber).find();
                boolean patternCheck2 = pattern1.matcher(otp).find();
                if ((mobileNumber.length() != 10) || (patternCheck1)) {
                    mobileNumberTextView.setError("Required 10 digits");
                    mobileNumberTextView.requestFocus();
                } else if ((otp.length() == 0) || (patternCheck2)) {
                    otpTextView.setError("Required");
                    otpTextView.requestFocus();
                } else {
                    new GetBikeAsyncTask(LoginActivity.this) {
                        boolean result = false;

                        @Override
                        public void process() {
                            final LoginSyncher loginSyncher = new LoginSyncher();
                            result = loginSyncher.loginWithOtp(mobileNumber, otp);
                            if (result) {
                                GetBikePreferences.setPreferences(LoginActivity.this);
                                GetBikePreferences.setAccessToken(BaseSyncher.getAccessToken());
                                GetBikePreferences.setLoggedIn(true);
                                try {
                                    InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                    Log.i("TAG", "GCM Registration Token: " + token);
                                    loginSyncher.storeGcmCode(token);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void afterPostExecute() {
                            if (result) {
                                Intent intent = new Intent(getApplicationContext(), GiveRideTakeRideActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastHelper.redToast(getApplicationContext(), "Failed to login.");
                            }
                        }
                    }.execute();
                }
            }
            break;
            case R.id.sign_up_text_view:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
                break;
        }
    }

    public void updateOtp(String receivedOtp) {
        this.receivedOtp.setText(receivedOtp);
        if (smsListener != null) {
            smsListener.smsReceived();
        }
    }

    public void setSmsListener(SMSListener smsListener) {
        this.smsListener = smsListener;
    }
}
