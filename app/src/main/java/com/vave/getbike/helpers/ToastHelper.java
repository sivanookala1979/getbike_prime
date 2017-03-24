/*
 * (c) Copyright 2001-2014 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.helpers;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import com.vave.getbike.R;

/**
 * @author Srinath Chittela
 * @version 1.0, 12-Jan-2015
 */
public class ToastHelper {

    public static boolean delayedToasting = false;
    public static Context lastContext = null;
    public static String lastMessage = null;
    public static String toastType = null;

    public static void blueToast(Context context, String message) {
        if (delayedToasting) {
            lastContext = context;
            lastMessage = message;
            toastType = "blue";
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.getView().setBackgroundColor(Color.BLUE);
            toast.show();
        }
    }

    public static void blueToast(Context context, int id) {
        blueToast(context, context.getResources().getString(id));
    }

    public static void redToast(Context context, int id) {
        redToast(context, context.getResources().getString(id));
    }

    public static void gpsToast(Context context) {
        redToast(context, context.getResources().getString(R.string.gps_permission_missing_toast));
    }

    public static void serverToast(Context context) {
        redToast(context, context.getResources().getString(R.string.internet_or_server_error));
    }

    public static void redToast(Context context, String message) {
        if (delayedToasting) {
            lastContext = context;
            lastMessage = message;
            toastType = "red";
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(
                    android.R.id.message);
            v.setPadding(15, 0, 15, 0);
            v.setTextColor(context.getResources().getColor(R.color.yellow)); //Color.YELLOW);//
            toast.getView().setBackgroundColor(context.getResources().getColor(R.color.black));
            toast.show();
            /*Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(Color.RED);
			toast.show();*/
        }
    }

    public static void yellowToast(Context context, String message) {
        if (delayedToasting) {
            lastContext = context;
            lastMessage = message;
            toastType = "yellow";
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(
                    android.R.id.message);
            v.setPadding(15, 0, 15, 0);
            v.setTextColor(context.getResources().getColor(R.color.yellow)); //Color.YELLOW);//
            toast.getView().setBackgroundColor(context.getResources().getColor(R.color.black));
            toast.show();
        }
    }

    public static void processPendingToast() {
        delayedToasting = false;
        if (lastContext != null && lastMessage != null && toastType != null) {
            if (toastType.equals("blue")) {
                yellowToast(lastContext, lastMessage);
            }
            if (toastType.equals("red")) {
                yellowToast(lastContext, lastMessage);
            }
            if (toastType.equals("yellow")) {
                yellowToast(lastContext, lastMessage);
            }
        }
        lastContext = null;
        lastMessage = null;
        toastType = null;
    }

    public static void delayToasting() {
        delayedToasting = true;
        lastContext = null;
        lastMessage = null;
        toastType = null;
    }
}
