package com.vave.getbike.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.CircleTransform;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.GetBikeTextWatcher;
import com.vave.getbike.helpers.LocationDetails;
import com.vave.getbike.helpers.LocationSyncher;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.ScheduleRide;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.ScheduleRideSyncher;
import com.vave.getbike.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adarsht on 07/12/16.
 */

public class ScheduleRideActivity extends BaseActivity implements View.OnClickListener {
    AutoCompleteTextView fromAddress, toAddress;
    Button selectTime, schedule;
    RadioButton giveRide, takeRide;
    TextView time;
    LatLng fromLatLong, toLatLong;
    ToggleButton reverseRide;
    private int mHour, mMinute;
    Calendar calendar;
    List<String> locations = new ArrayList<String>();
    ScheduleRideSyncher scheduleRideSyncher = new ScheduleRideSyncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_rides_screen);
        addToolbarView();
        fromAddress = (AutoCompleteTextView) findViewById(R.id.fromAddress);
        toAddress = (AutoCompleteTextView) findViewById(R.id.toAddress);
        selectTime = (Button) findViewById(R.id.selectTime);
        schedule = (Button) findViewById(R.id.schedule);
        giveRide = (RadioButton) findViewById(R.id.giveRide);
        takeRide = (RadioButton) findViewById(R.id.takeRide);
        reverseRide = (ToggleButton) findViewById(R.id.reverseRide);
        time = (TextView) findViewById(R.id.selectedTime);
        selectTime.setOnClickListener(this);
        schedule.setOnClickListener(this);
        calendar = Calendar.getInstance();
        addTextChangedListener();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectTime:
                // Get Current Time
                launchTimpickerDialog();
                break;
            case R.id.schedule:
                ScheduleRide scheduleRide = colletRideinformation();
                SaveResult dataValid = scheduleRide.isDataValid();
                if (dataValid.isValid()) {
                    createScheduledRide(scheduleRide);
                } else {
                    ToastHelper.redToast(getApplicationContext(), dataValid.getErrorMessage());
                }
                break;
        }
    }

    private void createScheduledRide(final ScheduleRide scheduleRide) {
        new GetBikeAsyncTask(ScheduleRideActivity.this) {
            SaveResult result;

            @Override
            public void process() {
                result = scheduleRideSyncher.createScheduledRide(scheduleRide);
            }

            @Override
            public void afterPostExecute() {
                if (result.isValid()) {
                    ToastHelper.redToast(getApplicationContext(), "Ride successfully scheduled.");
                } else {
                    ToastHelper.redToast(getApplicationContext(), result.getErrorMessage());
                }
            }
        }.execute();
    }

    private ScheduleRide colletRideinformation() {
        ScheduleRide scheduleRide = new ScheduleRide();
        scheduleRide.setScheduleTime(time.getText().toString());
        scheduleRide.setDays(collectSelectedDaysList(R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat, R.id.sun));
        scheduleRide.setFromAddress(fromAddress.getText().toString());
        if (fromLatLong != null) {
            scheduleRide.setFromLatitude(fromLatLong.latitude);
            scheduleRide.setFromLongitude(fromLatLong.longitude);

        }
        scheduleRide.setToAddress(toAddress.getText().toString());
        if (toLatLong != null) {
            scheduleRide.setToLatitude(toLatLong.latitude);
            scheduleRide.setToLongitude(toLatLong.longitude);

        }
        if (giveRide.isChecked()) {
            scheduleRide.setGivenRide(true);
        }
        if (reverseRide.isChecked()) {
            scheduleRide.setScheduledReverseRide(true);
        }
        return scheduleRide;
    }

    private List<String> collectSelectedDaysList(int... days) {
        List<String> selectedDays = new ArrayList<String>();
        for (int dayId : days) {
            CheckBox checkBox = (CheckBox) findViewById(dayId);
            if (checkBox.isChecked()) {
                switch (dayId) {
                    case R.id.mon:
                        selectedDays.add("Mon");
                        break;
                    case R.id.tue:
                        selectedDays.add("Tue");
                        break;
                    case R.id.wed:
                        selectedDays.add("Wed");
                        break;
                    case R.id.thu:
                        selectedDays.add("Thu");
                        break;
                    case R.id.fri:
                        selectedDays.add("Fri");
                        break;
                    case R.id.sat:
                        selectedDays.add("Sat");
                        break;
                    case R.id.sun:
                        selectedDays.add("Sun");
                        break;
                }
            }
        }
        Log.d("Days", "" + selectedDays.size());
        return selectedDays;
    }

    private void launchTimpickerDialog() {
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                int i = calendar.get(Calendar.AM_PM);
                String ampmStr = (i == 0) ? "AM" : "PM";
                time.setText(StringUtils.getTwodigitString(hourOfDay) + ":" + StringUtils.getTwodigitString(minute) + " " + ampmStr);
                mHour = hourOfDay;
                mMinute = minute;
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void addTextChangedListener() {
        fromAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                new GetBikeAsyncTask(ScheduleRideActivity.this) {

                    LocationDetails fromLocationDetailsByName = null;

                    @Override
                    public void process() {
                        LocationSyncher locationSyncher = new LocationSyncher();
                        fromLocationDetailsByName = locationSyncher
                                .getLocationDetailsByNameRecursive(fromAddress
                                        .getText().toString());
                    }

                    @Override
                    public void afterPostExecute() {
                        if (fromLocationDetailsByName != null) {
                            fromLatLong = new LatLng(fromLocationDetailsByName.getLatitude(), fromLocationDetailsByName.getLongitude());
                        } else {
                            ToastHelper.redToast(getApplicationContext(),
                                    "Location details not found");
                        }
                    }
                }.execute();
            }
        });
        fromAddress.addTextChangedListener(new GetBikeTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                GetBikeAsyncTask asyncTask = new GetBikeAsyncTask(ScheduleRideActivity.this) {
                    @Override
                    public void process() {

                        locations.clear();
                        String key = fromAddress.getText().toString();
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
                        fromAddress.setAdapter(arrayAdapter);
                    }

                };
                asyncTask.setShowProgress(false);
                asyncTask.execute();

            }
        });

        toAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                new GetBikeAsyncTask(ScheduleRideActivity.this) {

                    LocationDetails fromLocationDetailsByName = null;

                    @Override
                    public void process() {
                        LocationSyncher locationSyncher = new LocationSyncher();
                        fromLocationDetailsByName = locationSyncher
                                .getLocationDetailsByNameRecursive(toAddress
                                        .getText().toString());
                    }

                    @Override
                    public void afterPostExecute() {
                        if (fromLocationDetailsByName != null) {
                            toLatLong = new LatLng(fromLocationDetailsByName.getLatitude(), fromLocationDetailsByName.getLongitude());
                        } else {
                            ToastHelper.redToast(getApplicationContext(),
                                    "Location details not found");
                        }
                    }
                }.execute();
            }
        });
        toAddress.addTextChangedListener(new GetBikeTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                GetBikeAsyncTask asyncTask = new GetBikeAsyncTask(ScheduleRideActivity.this) {
                    @Override
                    public void process() {
                        locations.clear();
                        String key = toAddress.getText().toString();
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
                        toAddress.setAdapter(arrayAdapter);
                    }

                };
                asyncTask.setShowProgress(false);
                asyncTask.execute();

            }
        });
    }


}
