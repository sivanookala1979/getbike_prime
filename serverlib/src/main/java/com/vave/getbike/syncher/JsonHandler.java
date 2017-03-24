package com.vave.getbike.syncher;

import com.vave.getbike.exception.GetBikeException;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sivanookala on 26/10/16.
 */
public abstract class JsonHandler {

    JSONObject jsonRequest = new JSONObject();
    JSONObject jsonResult = null;
    String url;

    public JsonHandler(String url) {
        this.url = url;
    }

    protected abstract void prepareRequest();

    protected abstract void processResult(JSONObject jsonResult) throws Exception;

    protected void put(String key, String value) {
        try {
            jsonRequest.put(key, value);
        } catch (Exception e) {
            throw new GetBikeException(e);
        }
    }

    protected void put(String key, char value) {
        try {
            jsonRequest.put(key, value);
        } catch (Exception e) {
            throw new GetBikeException(e);
        }
    }

    protected void put(String key, double value) {
        try {
            jsonRequest.put(key, value);
        } catch (Exception e) {
            throw new GetBikeException(e);
        }
    }
    protected void put(String key, Date value) {
        try {
            jsonRequest.put(key, value);
        } catch (Exception e) {
            throw new GetBikeException(e);
        }
    }
}
