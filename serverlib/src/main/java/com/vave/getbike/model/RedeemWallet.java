package com.vave.getbike.model;

import com.vave.getbike.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adarsht on 06/12/16.
 */

public class RedeemWallet extends Recharge {
    String walletName;

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public SaveResult isValid() {
        SaveResult result = new SaveResult();
        if (StringUtils.isStringValid(getMobileNumber())) {
            if (getAmount() > 0) {
                if (StringUtils.isStringValid(getWalletName())) {
                    result.setValid(true);
                } else {
                    result.setErrorMessage("wallet name");
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
            jsonObject.put("walletName", getWalletName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
