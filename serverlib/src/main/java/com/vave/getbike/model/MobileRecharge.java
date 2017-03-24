package com.vave.getbike.model;

import android.util.Log;

import com.vave.getbike.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adarsht on 06/12/16.
 */

public class MobileRecharge extends Recharge {
    String operator;
    String circle;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public SaveResult isValid() {
        SaveResult result = new SaveResult();
        if (StringUtils.isStringValid(getMobileNumber())) {
            if (getAmount() > 0) {
                if (StringUtils.isStringValid(getOperator())) {
                    if (StringUtils.isStringValid(getCircle())) {
                        result.setValid(true);
                    } else {
                        result.setErrorMessage("circle");
                    }
                } else {
                    result.setErrorMessage("operator");
                }
            } else {
                result.setErrorMessage("amount");
            }
        } else {
            result.setErrorMessage("mobile number");
        }
        return result;
    }

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobileNumber", getMobileNumber());
            jsonObject.put("amount", getAmount());
            jsonObject.put("operator", getMobileNumber());
            jsonObject.put("circle", getCircle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
