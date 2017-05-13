package com.vave.getbike.model;

import java.util.Date;

/**
 * Created by vave on 8/5/17.
 */

public class LeaveInAdvance {

    Long id;
    Long riderId;
    String riderMobileNumber;
    String riderName;
    Boolean requestStatus;
    String riderDescription;
    String adminDescription;
    String leavesRequired;
    String fromDate;
    String toDate;
    Date requestedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
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

    public String getLeavesRequired() {
        return leavesRequired;
    }

    public void setLeavesRequired(String leavesRequired) {
        this.leavesRequired = leavesRequired;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }

    @Override
    public String toString() {
        return riderId +
                "~" + riderMobileNumber +
                "~" + riderName +
                "~" + requestStatus +
                "~" + riderDescription +
                "~" + adminDescription +
                "~" + leavesRequired +
                "~" + fromDate +
                "~" + toDate +
                "~" + requestedAt;
    }

}
