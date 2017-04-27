package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikeTextWatcher;
import com.vave.getbike.helpers.LocationSyncher;
import com.vave.getbike.model.RoasterRecord;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddRoasterRecordActivity extends BaseActivity {

    AutoCompleteTextView sourceAddress;
    AutoCompleteTextView destinationAddress;
    List<String> locations = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_roaster_record);
        addToolbarView();

        final EditText orderIdEditText = (EditText) findViewById(R.id.orderIdEditText);
        final EditText tripIdEditText = (EditText) findViewById(R.id.tripIdEditText);
        final EditText distanceEditText = (EditText) findViewById(R.id.distanceEditText);
        final EditText amountCollectedEditText = (EditText) findViewById(R.id.amountCollectedEditText);
        sourceAddress = (AutoCompleteTextView) findViewById(R.id.sourceAddress);
        destinationAddress = (AutoCompleteTextView) findViewById(R.id.destinationAddress);
        Button submitButton = (Button) findViewById(R.id.submitRoasterRecord);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderIdEditText.setError(null);
                tripIdEditText.setError(null);
                distanceEditText.setError(null);
                amountCollectedEditText.setError(null);
                sourceAddress.setError(null);
                destinationAddress.setError(null);
                if (orderIdEditText.getText().toString().length() == 0) {
                    orderIdEditText.setError("Required");
                    orderIdEditText.requestFocus();
                } else if (tripIdEditText.getText().toString().length() == 0) {
                    tripIdEditText.setError("Required");
                    tripIdEditText.requestFocus();
                } else if (distanceEditText.getText().toString().length() == 0) {
                    distanceEditText.setError("Required");
                    distanceEditText.requestFocus();
                } else if (amountCollectedEditText.getText().toString().length() == 0) {
                    amountCollectedEditText.setError("Required");
                    amountCollectedEditText.requestFocus();
                } else if (sourceAddress.getText().toString().length() == 0) {
                    sourceAddress.setError("Required");
                    sourceAddress.requestFocus();
                } else if (destinationAddress.getText().toString().length() == 0) {
                    destinationAddress.setError("Required");
                    destinationAddress.requestFocus();
                } else {
                    //save the details in data base;
                    final RoasterRecord roasterRecord = new RoasterRecord();
                    roasterRecord.setCustomerOrderNumber(orderIdEditText.getText().toString());
                    roasterRecord.setRideId(Long.parseLong(tripIdEditText.getText().toString()));
                    roasterRecord.setAmountCollected(Double.parseDouble(amountCollectedEditText.getText().toString()));
                    roasterRecord.setDistance(Double.parseDouble(distanceEditText.getText().toString()));
                    roasterRecord.setSourceAddress(sourceAddress.getText().toString());
                    roasterRecord.setDestinationAddress(destinationAddress.getText().toString());
                    new GetBikeAsyncTask(AddRoasterRecordActivity.this) {

                        boolean result = false;

                        @Override
                        public void process() {
                            result = new LoginSyncher().saveRoasterRecord(roasterRecord);
                        }

                        @Override
                        public void afterPostExecute() {
                            if (result) {
                                finish();
                                Toast.makeText(AddRoasterRecordActivity.this, "record added correctly!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddRoasterRecordActivity.this, "no record saved!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                }
            }
        });
        addTextChangedListener();

    }

    private void addTextChangedListener() {
        sourceAddress.addTextChangedListener(new GetBikeTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                GetBikeAsyncTask asyncTask = new GetBikeAsyncTask(AddRoasterRecordActivity.this) {
                    @Override
                    public void process() {
                        locations.clear();
                        String key = sourceAddress.getText().toString();
                        if (key.length() >= 3) {
                            LocationSyncher locationSyncher = new LocationSyncher(
                                    key);
                            locations = locationSyncher.getLocations();
                        }
                    }

                    @Override
                    public void afterPostExecute() {
                        String[] countries = locations
                                .toArray(new String[locations.size()]);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                R.layout.package_spinner_item, countries);
                        sourceAddress.setAdapter(arrayAdapter);
                    }

                };
                asyncTask.setShowProgress(false);
                asyncTask.execute();

            }
        });

        destinationAddress.addTextChangedListener(new GetBikeTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                GetBikeAsyncTask asyncTask = new GetBikeAsyncTask(AddRoasterRecordActivity.this) {
                    @Override
                    public void process() {
                        locations.clear();
                        String key = destinationAddress.getText().toString();
                        if (key.length() >= 3) {
                            LocationSyncher locationSyncher = new LocationSyncher(
                                    key);
                            locations = locationSyncher.getLocations();
                        }
                    }

                    @Override
                    public void afterPostExecute() {
                        String[] countries = locations
                                .toArray(new String[locations.size()]);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                R.layout.package_spinner_item, countries);
                        destinationAddress.setAdapter(arrayAdapter);
                    }

                };
                asyncTask.setShowProgress(false);
                asyncTask.execute();

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, ViewRoasterRecordsActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
