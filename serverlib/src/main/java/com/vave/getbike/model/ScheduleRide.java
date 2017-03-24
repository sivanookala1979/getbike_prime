package com.vave.getbike.model;

import com.vave.getbike.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsht on 08/12/16.
 */

public class ScheduleRide {
    String fromAddress = "";
    double fromLatitude;
    double fromLongitude;
    String toAddress = "";
    double toLatitude;
    double toLongitude;
    List<String> days = new ArrayList<String>();
    String scheduleTime;
    boolean isGivenRide;
    boolean scheduledReverseRide;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public boolean isGivenRide() {
        return isGivenRide;
    }

    public void setGivenRide(boolean givenRide) {
        isGivenRide = givenRide;
    }

    public boolean isScheduledReverseRide() {
        return scheduledReverseRide;
    }

    public void setScheduledReverseRide(boolean scheduledReverseRide) {
        this.scheduledReverseRide = scheduledReverseRide;
    }

    public SaveResult isDataValid() {
        SaveResult result = new SaveResult();
        if (StringUtils.isStringValid(getFromAddress()) & fromLatitude != 0 & fromLongitude != 0) {
            if (StringUtils.isStringValid(getToAddress()) & toLatitude != 0 & toLongitude != 0) {
                if (days.size() > 0) {
                    if (StringUtils.isStringValid(getScheduleTime())) {
                        result.setValid(true);
                    } else {
                        result.setErrorMessage("Please choose scheduled time.");
                    }
                } else {
                    result.setErrorMessage("please select at least one day.");
                }
            } else {
                result.setErrorMessage("please enter valid to address");
            }
        } else {
            result.setErrorMessage("please enter valid from address");
        }
        return result;
    }
    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromAddress", getFromAddress());
            jsonObject.put("fromLatitude", getFromLatitude());
            jsonObject.put("fromLongitude", getFromLongitude());
            jsonObject.put("toAddress", getToAddress());
            jsonObject.put("toLatitude", getToLatitude());
            jsonObject.put("toLongitude", getToLongitude());
            jsonObject.put("scheduleTime", getScheduleTime());
            jsonObject.put("isGivenRide", isGivenRide());
            jsonObject.put("scheduledReverseRide", isScheduledReverseRide());
            JSONArray daysArray = new JSONArray();
            for(String day:days){
                daysArray.put(day);
            }
            jsonObject.put("scheduledDays", daysArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
