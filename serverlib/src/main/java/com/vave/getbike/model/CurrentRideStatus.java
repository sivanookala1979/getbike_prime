package com.vave.getbike.model;

/**
 * Created by sivanookala on 25/01/17.
 */

public class CurrentRideStatus {

    Long rideId;
    Long requestId;
    boolean pending = false;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
