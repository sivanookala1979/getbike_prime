package com.vave.getbike.syncher;

import android.util.Log;

import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.ScheduleRide;

import org.json.JSONObject;

/**
 * Created by adarsht on 08/12/16.
 */

public class ScheduleRideSyncher extends BaseSyncher {

    public SaveResult createScheduledRide(ScheduleRide scheduleRide) {
        SaveResult result = new SaveResult();
        JSONObject json = scheduleRide.getJson();
        Log.d("ScheduleRide",json.toString());
        result.setValid(true);
        return result;
    }
}
