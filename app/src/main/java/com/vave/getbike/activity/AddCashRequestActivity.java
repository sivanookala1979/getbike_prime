package com.vave.getbike.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.syncher.RideSyncher;

import java.util.regex.Pattern;

public class AddCashRequestActivity extends BaseActivity {

    Button submitCashRequest;
    EditText requestedAmount,riderDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash_request);
        addToolbarView();

        submitCashRequest = (Button) findViewById(R.id.submitCashRequest);
        requestedAmount = (EditText) findViewById(R.id.requestedAmount);
        riderDescription = (EditText) findViewById(R.id.riderDescription);

        submitCashRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestedAmount.setError(null);
                final Pattern pattern1 = Pattern.compile("[^0-9.]");
                boolean patternCheck = pattern1.matcher(requestedAmount.getText().toString()).find();
                if (requestedAmount.getText().toString().length() == 0 || (patternCheck)) {
                    requestedAmount.setError("Required Amount");
                    requestedAmount.requestFocus();
                } else if (riderDescription.getText().toString().length() == 0) {
                    riderDescription.setText("NA");
                } else {
                    //push the details into DB;
                    new GetBikeAsyncTask(AddCashRequestActivity.this) {

                        boolean result = false;

                        @Override
                        public void process() {
                            result = new RideSyncher().saveUserCashInAdvanceRequest(Double.parseDouble(requestedAmount.getText().toString()),riderDescription.getText().toString());
                        }

                        @Override
                        public void afterPostExecute() {
                            if (result) {
                                finish();
                                Toast.makeText(AddCashRequestActivity.this, "record added correctly!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddCashRequestActivity.this, "no record saved!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                }
            }
        });


    }
}
