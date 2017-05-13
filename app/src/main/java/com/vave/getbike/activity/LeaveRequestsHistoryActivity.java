package com.vave.getbike.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.model.LeaveInAdvance;
import com.vave.getbike.syncher.LoginSyncher;

import java.util.ArrayList;
import java.util.List;

public class LeaveRequestsHistoryActivity extends BaseActivity {

    ListView leaveInAdvanceListView;
    Button addLeaveRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_requests_history);
        addToolbarView();

        addLeaveRequestButton = (Button) findViewById(R.id.addLeaveRequestButton);
        leaveInAdvanceListView = (ListView) findViewById(R.id.leaveInAdvanceListView);

        addLeaveRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaveRequestsHistoryActivity.this,LeaveInAdvanceActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetBikeAsyncTask(LeaveRequestsHistoryActivity.this) {
            List<LeaveInAdvance> recordList = new ArrayList<LeaveInAdvance>();
            @Override
            public void process() {
                recordList = new LoginSyncher().getLeaveRequests();
            }

            @Override
            public void afterPostExecute() {
                System.out.println("date is : "+recordList);
                List<String> recordStrings = new ArrayList<String>();
                for (LeaveInAdvance record : recordList) {
                    recordStrings.add(record.toString());
                }
                leaveInAdvanceListView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.package_spinner_item, recordStrings));

            }
        }.execute();

    }
}
