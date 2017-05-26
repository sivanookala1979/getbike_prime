package com.vave.getbike.model;

import java.util.Date;

/**
 * Created by sivanookala on 26/10/16.
 */

public class RideLocation {

    Long id;
    Long rideId;
    Date locationTime;
    Double latitude;
    Double longitude;
    Boolean posted;
    boolean sourse;
    String sourseAddress;
    String destinationAddress;


    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSourseAddress() {
        return sourseAddress;
    }

    public void setSourseAddress(String sourseAddress) {
        this.sourseAddress = sourseAddress;
    }

    public boolean isSourse() {
        return sourse;
    }

    public void setSourse(boolean sourse) {
        this.sourse = sourse;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Date getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(Date locationTime) {
        this.locationTime = locationTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getPosted() {
        return posted;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
