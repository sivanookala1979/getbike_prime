package com.vave.getbike.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vave.getbike.R;
import com.vave.getbike.datasource.CallStatus;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity implements View.OnClickListener {

    TextView loginTextView;
    RadioGroup genderGroup;
    EditText name;
    EditText mobile;
    EditText email;
    EditText promoCode;
    char gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Button signup = (Button) findViewById(R.id.signup);
        assert signup != null;
        signup.setBackgroundResource(R.mipmap.register);
        signup.setOnClickListener(this);
        //
        name = (EditText) findViewById(R.id.name);
        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.email);
        promoCode = (EditText) findViewById(R.id.promoCode);

        loginTextView = (TextView) findViewById(R.id.login_text_view);
        loginTextView.setOnClickListener(this);
        genderGroup = (RadioGroup) findViewById(R.id.gender);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    gender = 'M';
                }
                if (checkedId == R.id.female) {
                    gender = 'F';
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup:
                name.setError(null);
                email.setError(null);
                mobile.setError(null);
                promoCode.setError(null);
                final String EMAIL_REGEX = "^(.+)@(.+)$";
                final Pattern pattern1 = Pattern.compile("[^0-9]");
                final Pattern pattern2 = Pattern.compile("[^a-z A-Z]");
                final Boolean stringCheck1 = pattern2.matcher(name.getText().toString()).find();
                boolean patternCheck = pattern1.matcher(mobile.getText().toString()).find();
                if ((name.getText().toString().length() == 0) || (stringCheck1)) {
                    name.setError("Required only alphabets");
                    name.requestFocus();
                } else if ((email.getText().toString().length() <= 0) || (!(Pattern.matches(EMAIL_REGEX, email.getText().toString())))) {
                    email.setError("Required valid email id");
                    email.requestFocus();
                } else if ((mobile.getText().toString().length() != 10) || (patternCheck)) {
                    mobile.setError("Required 10 digit number");
                    mobile.requestFocus();
                } else if (genderGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignupActivity.this, "Please provide your Gender", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("selected gender is:" + gender);
                    new GetBikeAsyncTask(SignupActivity.this) {
                        CallStatus callStatus = null;

                        @Override
                        public void process() {
                            LoginSyncher sut = new LoginSyncher();
                            Log.d("TAG", "call status for signup is:" + callStatus);
                            callStatus = sut.signup(readText(R.id.name), readText(R.id.mobile), readText(R.id.email), gender, readText(R.id.promoCode));
                            Log.d("TAG", "call status for signup is:" + callStatus);
                        }

                        @Override
                        public void afterPostExecute() {
                            if (callStatus != null) {
                                Log.d("TAG", "call status for signup is:" + callStatus);
                                if (callStatus.isSuccess()) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("Success");
                                    builder.setMessage("Successfully registered, Sign in now");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    builder.show();
                                } else if (callStatus.getErrorCode() == 9901) {
                                    ToastHelper.redToast(getApplicationContext(), R.string.error_user_already_exists);
                                } else if (callStatus.getErrorCode() == 9908) {
                                    promoCode.setError("Invalid promo code.");
                                    promoCode.requestFocus();
                                }
                            } else {
                                ToastHelper.serverToast(getApplicationContext());
                            }
                        }
                    }.execute();
                }

                break;
            case R.id.login_text_view: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            break;
        }
    }

    String readText(int id) {
        return ((TextView) findViewById(id)).getText().toString();
    }
}
