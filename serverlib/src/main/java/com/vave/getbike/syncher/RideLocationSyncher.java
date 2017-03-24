package com.vave.getbike.syncher;

import com.vave.getbike.datasource.RideLocationDataSource;
import com.vave.getbike.model.RideLocation;
import com.vave.getbike.utils.HTTPUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by sivanookala on 01/11/16.
 */

public class RideLocationSyncher extends BaseSyncher {

    RideLocationDataSource dataSource;


    public boolean storePendingLocations(long rideId) {
        boolean result = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            List<RideLocation> pendingRideLocations = dataSource.getPendingRideLocations(rideId);
            JSONArray jsonArray = new JSONArray();
            int index = 0;
            for (RideLocation rideLocation : pendingRideLocations) {
                JSONObject rideLocationObject = new JSONObject();
                rideLocationObject.put("rideId", rideLocation.getRideId());
                rideLocationObject.put("locationTime", dateFormat.format(rideLocation.getLocationTime()));
                rideLocationObject.put("latitude", rideLocation.getLatitude());
                rideLocationObject.put("longitude", rideLocation.getLongitude());
                jsonArray.put(index++, rideLocationObject);
            }
            String response = HTTPUtils.getDataFromServer(BASE_URL + "/storeLocations", "POST", jsonArray.toString());
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.has("result") && responseJson.get("result").equals("success")) {
                result = true;
            }
            System.out.println(response);
        } catch (Exception ex) {
            handleException(ex);
        }
        return result;
    }

    public RideLocationDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(RideLocationDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
