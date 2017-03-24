package com.vave.getbike.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sivanookala on 19/01/17.
 */

public class GetBikeUtils {

    public static <T> List<T> trimList(List<T> input, int size) {
        List<T> result = new ArrayList<>();
        int factor = input.size() / size;
        for (int i = 0; i < input.size(); i++) {
            if (factor == 0 || i % factor == 0) {
                result.add(input.get(i));
            }
        }
        return result;
    }

    public static <T> List<T> createList(T... input) {
        List<T> result = new ArrayList<>();
        for (T item : input) {
            result.add(item);
        }
        return result;
    }

    public static boolean isTimePassed(Date oldDate, Date newDate, int timeInSeconds) {
        return (newDate.getTime() - oldDate.getTime()) > timeInSeconds * 1000;
    }

    public static double distanceInKms(double lat1, double lon1, double lat2, double lon2) {
        return distance(lat1, lon1, lat2, lon2, "K");
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double round2(double result) {
        return (Math.round(result * 100.0)) / 100.0;
    }

}
