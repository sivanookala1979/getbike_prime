package com.vave.getbike.model;

/**
 * Created by adarsht on 05/12/16.
 */

public class SaveResult {


    String errorMessage;
    boolean valid;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
