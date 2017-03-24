package com.vave.getbike.datasource;

/**
 * Created by sivanookala on 01/11/16.
 */

public class CallStatus {

    Long id;
    String errorMessage;
    int errorCode = -999;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return errorCode == 0;
    }

    public void setSuccess() {
        errorCode = 0;
    }
}
