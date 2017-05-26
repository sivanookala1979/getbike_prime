package com.vave.getbike.model;

import java.util.ArrayList;

/**
 * Created by chackri on 5/25/17.
 */

public class GroupRider {

    Long groupId;
    Long groupRiderId;
    Integer numberOfRides;
    ArrayList<RideLocation> groupRiderLocations = new ArrayList<>();

    public Integer getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(Integer numberOfRides) {
        this.numberOfRides = numberOfRides;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getGroupRiderId() {
        return groupRiderId;
    }

    public void setGroupRiderId(Long groupRiderId) {
        this.groupRiderId = groupRiderId;
    }

    public ArrayList<RideLocation> getGroupRiderLocations() {
        return groupRiderLocations;
    }

    public void setGroupRiderLocations(ArrayList<RideLocation> groupRiderLocations) {
        this.groupRiderLocations = groupRiderLocations;
    }
}
