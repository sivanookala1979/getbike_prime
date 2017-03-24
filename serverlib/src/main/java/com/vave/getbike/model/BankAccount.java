package com.vave.getbike.model;

import com.vave.getbike.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adarsht on 06/12/16.
 */

public class BankAccount {
    String accountHolderName;
    String accountNumber;
    String ifscCode;
    String bankName;
    String branchName;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public SaveResult isDataValid() {
        SaveResult saveResult = new SaveResult();
        if (StringUtils.isStringValid(getAccountHolderName())) {
            if (StringUtils.isStringValid(getAccountNumber())) {
                if (StringUtils.isStringValid(getIfscCode())) {
                    if (StringUtils.isStringValid(getBankName())) {
                        if (StringUtils.isStringValid(getBranchName())) {
                            saveResult.setValid(true);
                        } else {
                            saveResult.setErrorMessage("branch name");
                        }
                    } else {
                        saveResult.setErrorMessage("bank name");
                    }
                } else {
                    saveResult.setErrorMessage("IFSC code");
                }
            } else {
                saveResult.setErrorMessage("account number");
            }
        } else {
            saveResult.setErrorMessage("account holder name");
        }
        return saveResult;
    }
    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountHolderName", getAccountHolderName());
            jsonObject.put("accountNumber", getAccountNumber());
            jsonObject.put("ifscCode", getIfscCode());
            jsonObject.put("bankName", getBankName());
            jsonObject.put("branchName", getBranchName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
