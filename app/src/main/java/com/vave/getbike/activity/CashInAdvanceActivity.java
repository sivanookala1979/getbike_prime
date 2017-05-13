package com.vave.getbike.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.adapter.CashInAdvanceItemAdapter;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.CashInAdvance;
import com.vave.getbike.model.RoasterRecord;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.List;

public class CashInAdvanceActivity extends BaseActivity {

    ListView cashInAdvanceListViewListView;
    Button addNewCashInAdvanceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in_advance);
        addToolbarView();

        cashInAdvanceListViewListView = (ListView) findViewById(R.id.cashInAdvanceListViewListView);
        addNewCashInAdvanceButton = (Button) findViewById(R.id.addNewCashInAdvanceButton);

        addNewCashInAdvanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CashInAdvanceActivity.this,AddCashRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetBikeAsyncTask(CashInAdvanceActivity.this) {
            List<CashInAdvance> recordList = new ArrayList<CashInAdvance>();

            @Override
            public void process() {
                recordList = new LoginSyncher().getCashRequests();
            }

            @Override
            public void afterPostExecute() {
                System.out.println("date is : "+recordList);
                /*List<String> recordStrings = new ArrayList<String>();
                for (CashInAdvance record : recordList) {
                    recordStrings.add(record.toString());
                }
                cashInAdvanceListViewListView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.package_spinner_item, recordStrings));*/

                if (recordList != null) {
                    cashInAdvanceListViewListView.setAdapter(new CashInAdvanceItemAdapter(CashInAdvanceActivity.this,recordList));
                    if (recordList.size() == 0) {
                        ToastHelper.blueToast(CashInAdvanceActivity.this, "No Requests");
                    }
                }

            }
        }.execute();
    }
}
