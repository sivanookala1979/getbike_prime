package com.vave.getbike.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.syncher.RideSyncher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EarningsActivity extends BaseActivity {

    Button openCalenderButton;
    TextView customerAmountTextView;
    TextView parcelAmountTextView;
    Calendar myCalendar;
    SimpleDateFormat format;
    String dateToString;
    double customerTripsAmount = 0.0;
    double parcelTripsAmount = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        addToolbarView();

        openCalenderButton = (Button) findViewById(R.id.openCalenderButton);
        customerAmountTextView = (TextView) findViewById(R.id.customerAmountTextView);
        parcelAmountTextView = (TextView) findViewById(R.id.parcelAmountTextView);
        myCalendar = Calendar.getInstance();
        format = new SimpleDateFormat("yyyy-MM-dd");
        dateToString = format.format(myCalendar.getTime());
        openCalenderButton.setText(format.format(new Date()));

        getTripsAmountForThisDate(dateToString);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateToString = format.format(myCalendar.getTime());
                openCalenderButton.setText(dateToString);
                getTripsAmountForThisDate(dateToString);
            }

        };

        openCalenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EarningsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void getTripsAmountForThisDate(final String dateToString) {
        System.out.println("Received date is......"+dateToString);

        new GetBikeAsyncTask(EarningsActivity.this) {

            @Override
            public void process() {
                customerTripsAmount = new RideSyncher().getCustomerTripsAmountForDate(dateToString);
                parcelTripsAmount = new RideSyncher().getParcelTripsAmountForDate(dateToString);
            }

            @Override
            public void afterPostExecute() {
                customerAmountTextView.setText(String.valueOf(customerTripsAmount));
                parcelAmountTextView.setText(String.valueOf(parcelTripsAmount));
            }
        }.execute();


    }
}
