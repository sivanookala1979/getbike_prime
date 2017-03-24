package com.vave.getbike.syncher;

import com.vave.getbike.utils.HTTPUtils;

import org.json.JSONObject;

import static com.vave.getbike.syncher.BaseSyncher.BASE_URL;
import static com.vave.getbike.syncher.BaseSyncher.handleException;

/**
 * Created by sivanookala on 26/10/16.
 */

public abstract class JsonGetHandler extends JsonHandler {

    JsonGetHandler(String url) {
        super(url);
    }

    public void handle() {
        Integer id = null;
        try {
            String resultString = HTTPUtils.getDataFromServer(BASE_URL + url, "GET");
            jsonResult = new JSONObject(resultString);
            processResult(jsonResult);

        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void prepareRequest() {
    }

}
