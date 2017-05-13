package com.vave.getbike.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.syncher.RideSyncher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LeaveInAdvanceActivity extends BaseActivity {

    int milliSecondsForADay = 86400000;
    Button submitLeaveRequest,fromDateButton,toDateButton;
    EditText leaveDaysEditText,leaveDescription;
    Calendar myCalendar;
    SimpleDateFormat format;
    String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_in_advance);
        addToolbarView();

        submitLeaveRequest = (Button) findViewById(R.id.submitLeaveRequest);
        fromDateButton = (Button) findViewById(R.id.fromDateButton);
        toDateButton = (Button) findViewById(R.id.toDateButton);
        leaveDaysEditText = (EditText) findViewById(R.id.leaveDaysEditText);
        leaveDescription = (EditText) findViewById(R.id.leaveDescription);
        myCalendar = Calendar.getInstance();
        format = new SimpleDateFormat("dd-MM-yyyy");
        fromDateButton.setText(format.format(System.currentTimeMillis()+milliSecondsForADay));
        toDateButton.setText(format.format(System.currentTimeMillis()+milliSecondsForADay));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fromDate = format.format(myCalendar.getTime());
                if ((System.currentTimeMillis() + milliSecondsForADay) > myCalendar.getTimeInMillis()) {
                    fromDateButton.setText(format.format(System.currentTimeMillis()+milliSecondsForADay));
                } else {
                    fromDateButton.setText(fromDate);
                }
            }
        };

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                toDate = format.format(myCalendar.getTime());
                if ((System.currentTimeMillis() + milliSecondsForADay) > myCalendar.getTimeInMillis()) {
                    toDateButton.setText(format.format(System.currentTimeMillis()+milliSecondsForADay));
                } else {
                    toDateButton.setText(toDate);
                }
            }
        };

        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LeaveInAdvanceActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LeaveInAdvanceActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitLeaveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveDaysEditText.setError(null);
                if (leaveDaysEditText.getText().toString().length() == 0){
                    leaveDaysEditText.setError("Required");
                    leaveDaysEditText.requestFocus();
                } else if (leaveDescription.getText().toString().length() == 0){
                    leaveDescription.setError("Required");
                    leaveDescription.requestFocus();
                } else if (fromDateButton.getText().toString().length() == 0 || toDateButton.getText().toString().length() == 0){
                    Toast.makeText(LeaveInAdvanceActivity.this,"please select from and to dates!",Toast.LENGTH_LONG).show();
                } else {
                    new GetBikeAsyncTask(LeaveInAdvanceActivity.this) {
                        boolean result = false;
                        @Override
                        public void process() {
                            result = new RideSyncher().saveLeaveRequest(leaveDaysEditText.getText().toString(),leaveDescription.getText().toString(),fromDateButton.getText().toString(),toDateButton.getText().toString());
                        }

                        @Override
                        public void afterPostExecute() {
                            if (result) {
                                finish();
                                Toast.makeText(LeaveInAdvanceActivity.this, "record added correctly!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LeaveInAdvanceActivity.this, "no record saved!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                }

            }
        });

    }
}
