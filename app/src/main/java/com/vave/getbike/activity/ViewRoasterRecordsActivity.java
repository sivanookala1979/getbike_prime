package com.vave.getbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.model.RoasterRecord;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.List;

public class ViewRoasterRecordsActivity extends BaseActivity {

    ListView recordsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_roaster_records);
        addToolbarView();
        recordsListView = (ListView) findViewById(R.id.roasterRecordsListView);
        Button addNewRecordButton = (Button) findViewById(R.id.addNewRecordButton);
        addNewRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewRoasterRecordsActivity.this, AddRoasterRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetBikeAsyncTask(ViewRoasterRecordsActivity.this) {
            List<RoasterRecord> recordList = new ArrayList<RoasterRecord>();

            @Override
            public void process() {
                recordList = new LoginSyncher().getRoasterRecord();
            }

            @Override
            public void afterPostExecute() {
                List<String> recordStrings = new ArrayList<String>();
                for (RoasterRecord record : recordList) {
                    recordStrings.add(record.toString());
                }
                recordsListView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.package_spinner_item, recordStrings));
            }
        }.execute();
    }
}
