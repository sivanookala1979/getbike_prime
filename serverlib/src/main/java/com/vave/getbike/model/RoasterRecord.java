package com.vave.getbike.model;

import java.util.Date;

/**
 * Created by sivanookala on 31/03/17.
 */

public class RoasterRecord {

    Long id;
    Long riderId;
    String customerOrderNumber;
    String sourceAddress;
    String destinationAddress;
    Double distance;
    Double amountCollected;
    Date deliveryDate;
    Long rideId;

    public String getCustomerOrderNumber() {
        return customerOrderNumber;
    }

    public void setCustomerOrderNumber(String customerOrderNumber) {
        this.customerOrderNumber = customerOrderNumber;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getAmountCollected() {
        return amountCollected;
    }

    public void setAmountCollected(Double amountCollected) {
        this.amountCollected = amountCollected;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    @Override
    public String toString() {
        return riderId +
                "~" + customerOrderNumber +
                "~" + sourceAddress +
                "~" + destinationAddress +
                "~" + distance +
                "~" + amountCollected +
                "~" + rideId;
    }
}
