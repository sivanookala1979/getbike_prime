/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.helpers;

import com.vave.getbike.exception.GetBikeException;
import com.vave.getbike.utils.HTTPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author adarsh
 * @version 1.0, Aug 18, 2015
 */
public class LocationSyncher {

    private static final String LOCATION = "location";
    private static final String GEOMETRY = "geometry";
    private static final String STATUS = "status";
    private static final String OK = "OK";
    private static final String RESULTS = "results";
    private static final String KEY_DESCRIPTION = "description";
    private static final String PREDICTIONS = "predictions";
    String API_KEY = "AIzaSyDxqQEvtdEtl6dDIvG7vcm6QTO45Si0FZs";
    String URL_START = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    String URL_END = "&regions=(locality)&location=17.387140,78.491684&radius=100000&strictbounds&sensor=true&key=" + API_KEY;
    String LAT_LON_URL_START = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String LAT_LON_URL_END = "&key=" + API_KEY;
    String URL = "";
    private String searchLocation;

    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=munga&regions=(administrative_area_level_1)&sensor=true&key=AIzaSyDxqQEvtdEtl6dDIvG7vcm6QTO45Si0FZs

    public LocationSyncher(String searchLocation) {
        URL = URL_START + encodeData(searchLocation) + URL_END;
        setSearchLocation(searchLocation);
    }

    public LocationSyncher() {
    }

    public List<String> getLocations() {
        List<String> list = new ArrayList<String>();
        try {
            JSONObject json = HTTPUtils.getJSONFromUrl(URL);
            if (json != null) {
                JSONArray contacts;
                try {
                    contacts = json.getJSONArray(PREDICTIONS);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String description = c.getString(KEY_DESCRIPTION);
                        list.add(description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    public LocationDetails getLocationDetailsByNameRecursive(String locationName) {
        LocationDetails result = getLocationDetailsByName(locationName);
        if (result == null) {
            if (locationName.contains(",")) {
                locationName = locationName.substring(locationName.indexOf(",") + 1);
                result = getLocationDetailsByName(locationName);
            }
        }

        return result;
    }

    private LocationDetails getLocationDetailsByName(String locationName) {
        LocationDetails details = null;
        try {
            String completeUrl = LAT_LON_URL_START + URLEncoder.encode(locationName, "utf-8") + LAT_LON_URL_END;
            String response = HTTPUtils.getDataFromServer(completeUrl, "GET");
            JSONObject json = new JSONObject(response);
            if (json != null) {
                JSONArray contacts;
                try {
                    if (json.getString(STATUS).equals(OK)) {
                        details = new LocationDetails();
                        contacts = json.getJSONArray(RESULTS);
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject myLocation = contacts.getJSONObject(i);
                            if (myLocation.has(GEOMETRY)) {
                                JSONObject geometryInfo = myLocation.getJSONObject(GEOMETRY);
                                JSONObject latLong = geometryInfo.getJSONObject(LOCATION);
                                details.setLatitude(latLong.getDouble("lat"));
                                details.setLongitude(latLong.getDouble("lng"));
                                details.setAddress(myLocation.getString("formatted_address"));
                            }
                            if (myLocation.has("address_components")) {
                                JSONArray addsComponent = myLocation.getJSONArray("address_components");
                                JSONObject jsonObject = addsComponent.getJSONObject(0);
                                details.setCity(jsonObject.getString("long_name"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return details;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public String encodeData(String input) {
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GetBikeException(ex);
        }
    }
}
