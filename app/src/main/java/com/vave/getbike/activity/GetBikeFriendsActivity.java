package com.vave.getbike.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.vave.getbike.R;

import java.util.ArrayList;

public class GetBikeFriendsActivity extends BaseActivity {

    public static final int RequestPermissionCode = 1;
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_bike_friends);
        addToolbarView();
        listView = (ListView) findViewById(R.id.listview1);
        button = (Button) findViewById(R.id.button1);
        StoreContacts = new ArrayList<String>();
        EnableRuntimePermission();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContactsIntoArrayList();
                arrayAdapter = new ArrayAdapter<String>(
                        GetBikeFriendsActivity.this,
                        R.layout.contact_items_listview,
                        R.id.textView, StoreContacts
                );
                listView.setAdapter(arrayAdapter);
            }
        });
    }

    public void GetContactsIntoArrayList() {
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            StoreContacts.add(name + " " + ":" + " " + phonenumber);
        }
        cursor.close();
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                GetBikeFriendsActivity.this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(GetBikeFriendsActivity.this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(GetBikeFriendsActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(GetBikeFriendsActivity.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                    button.performClick();
                    button.setVisibility(View.GONE);
                } else {
                    Toast.makeText(GetBikeFriendsActivity.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
