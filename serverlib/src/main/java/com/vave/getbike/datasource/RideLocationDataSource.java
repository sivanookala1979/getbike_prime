package com.vave.getbike.datasource;

import android.content.Context;

import com.vave.getbike.android.AndroidStubsFactory;
import com.vave.getbike.android.IContentValues;
import com.vave.getbike.android.ICursor;
import com.vave.getbike.model.RideLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sivanookala on 26/10/16.
 */

public class RideLocationDataSource extends BaseDataSource {

    public static final String RIDE_LOCATION_TABLE = "RideLocation";
    public static final String RIDE_LOCATION_ID = "id";
    public static final String RIDE_ID = "ride_id";
    public static final String LOCATION_TIME = "location_time";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String POSTED = "posted";

    public static final String[] ALL_COLUMNS = new String[]{
            RIDE_ID,
            LATITUDE,
            LOCATION_TIME,
            LONGITUDE,
            POSTED};

    public RideLocationDataSource(Context context) {
        super(context);
    }

    @Override
    public String[] getAllColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public String getTableName() {
        return RIDE_LOCATION_TABLE;
    }

    @Override
    public String getUniqueId() {
        return RIDE_LOCATION_ID;
    }

    public Long insert(Long rideId, Date locationTime, Double latitude, Double longitude, Boolean posted) {
        IContentValues contentValues = AndroidStubsFactory.createContentValues();
        contentValues.put(RIDE_ID, rideId + "");
        contentValues.put(LOCATION_TIME, to_s(locationTime));
        contentValues.put(LATITUDE, latitude + "");
        contentValues.put(LONGITUDE, longitude + "");
        contentValues.put(POSTED, posted + "");
        return insertEntry(contentValues);
    }

    public List<RideLocation> getRideLocations(Long rideId) {
        List<RideLocation> result = new ArrayList<>();
        ICursor query = database.query(RIDE_LOCATION_TABLE, ALL_COLUMNS, RIDE_ID + " = '" + rideId + "'", null, null, null, null);
        for (int i = 0; i < query.getCount(); i++) {
            RideLocation rideLocation = mapRideLocation(query, i);
            result.add(rideLocation);
        }
        return result;
    }

    public List<RideLocation> getPendingRideLocations(Long rideId) {
        List<RideLocation> result = new ArrayList<>();
        ICursor query = database.query(RIDE_LOCATION_TABLE, ALL_COLUMNS, RIDE_ID + " = '" + rideId + "' and posted = 'false'", null, null, null, null);
        for (int i = 0; i < query.getCount(); i++) {
            RideLocation rideLocation = mapRideLocation(query, i);
            result.add(rideLocation);
        }
        return result;
    }

    public void markAsPosted(Long rideLocationId) {
        IContentValues contentValues = AndroidStubsFactory.createContentValues();
        contentValues.put(RIDE_LOCATION_ID, rideLocationId + "");
        contentValues.put(POSTED, true + "");
        database.update(RIDE_LOCATION_TABLE, contentValues, "id = " + rideLocationId, null);
    }

    @Override
    public RideLocationDataSource createNew(Context context) {
        return new RideLocationDataSource(context);
    }

    private RideLocation mapRideLocation(ICursor query, int i) {
        RideLocation rideLocation = new RideLocation();
        HashMap<String, Object> row = query.getItemAt(i, this);
        rideLocation.setId((Long) row.get(RIDE_LOCATION_ID));
        rideLocation.setRideId(Long.parseLong((String) row.get(RIDE_ID)));
        rideLocation.setLocationTime(to_d((String) row.get(LOCATION_TIME)));
        rideLocation.setLatitude(Double.parseDouble((String) row.get(LATITUDE)));
        rideLocation.setLongitude(Double.parseDouble((String) row.get(LONGITUDE)));
        rideLocation.setPosted(Boolean.parseBoolean((String) row.get(POSTED)));
        return rideLocation;
    }
}


