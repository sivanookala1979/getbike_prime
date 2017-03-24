package com.vave.getbike.gcm;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.vave.getbike.R;
import com.vave.getbike.activity.AcceptRejectRideActivity;
import com.vave.getbike.activity.LocationActivity;
import com.vave.getbike.activity.ShowCompletedRideActivity;
import com.vave.getbike.activity.SplashScreenActivity;
import com.vave.getbike.activity.WaitForRiderAfterAcceptanceActivity;
import com.vave.getbike.activity.WaitForRiderAllocationActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "Message: " + data.getString("message"));
        Log.d(TAG, "Message Type: " + data.getString("messageType"));
        Log.d(TAG, "Ride Id: " + data.getString("rideId"));
        String message = data.getString("message");
        String messageType = data.getString("messageType");
        Long rideId = null;
        if (data.getString("rideId") != null) {
            rideId = Long.parseLong(data.getString("rideId"));
        }
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, messageType, rideId);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String messageType, Long rideId) {

        Intent intent = null;
        String title = "getbike Alert";
        if ("newRide".equals(messageType)) {
            intent = new Intent(this, AcceptRejectRideActivity.class);
            intent.putExtra("rideId", rideId);
            title = "New Ride Alert";
        } else if ("rideAccepted".equals(messageType)) {
            if (WaitForRiderAllocationActivity.instance() != null) {
                WaitForRiderAllocationActivity.instance().rideAccepted(rideId);
            } else {
                intent = new Intent(this, WaitForRiderAfterAcceptanceActivity.class);
                intent.putExtra("rideId", rideId);
            }
        } else if ("rideCancelled".equals(messageType)) {
            if (LocationActivity.instance() != null) {
                LocationActivity.instance().rideCancelled(rideId);
            }
        } else if ("rideClosed".equals(messageType)) {
            if (WaitForRiderAfterAcceptanceActivity.instance() != null) {
                WaitForRiderAfterAcceptanceActivity.instance().rideCompleted(rideId);
            } else {
                intent = new Intent(this, ShowCompletedRideActivity.class);
                intent.putExtra("rideId", rideId);
            }
        } else if ("riderLocation".equals(messageType)) {
            if (WaitForRiderAfterAcceptanceActivity.instance() != null) {
                String tokens[] = message.split(",");
                WaitForRiderAfterAcceptanceActivity.instance().processRiderLocation(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), Boolean.parseBoolean(tokens[2]), rideId);
            }
        } else {
            intent = new Intent(this, SplashScreenActivity.class);
        }
        if (intent != null) {
            int notificationId = rideId != null ? rideId.intValue() : 0;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/notification_sound");
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.getbike_logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = notificationBuilder.build();
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(notificationId, notification);
        }
    }
}
