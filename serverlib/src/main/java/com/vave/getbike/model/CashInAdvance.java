package com.vave.getbike.model;

import java.util.Date;

/**
 * Created by Ram on 25/4/17.
 */

public class CashInAdvance {

    Long id;
    String riderMobileNumber;
    String riderName;
    Boolean requestStatus;
    String riderDescription;
    String adminDescription;
    Double amount;
    Date requestedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiderMobileNumber() {
        return riderMobileNumber;
    }

    public void setRiderMobileNumber(String riderMobileNumber) {
        this.riderMobileNumber = riderMobileNumber;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public Boolean getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Boolean requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRiderDescription() {
        return riderDescription;
    }

    public void setRiderDescription(String riderDescription) {
        this.riderDescription = riderDescription;
    }

    public String getAdminDescription() {
        return adminDescription;
    }

    public void setAdminDescription(String adminDescription) {
        this.adminDescription = adminDescription;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }
}
