package com.vave.getbike.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.vave.getbike.activity.LoginActivity;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String smsBody = smsMessage.getMessageBody().toString();
                smsMessageStr = smsBody;
            }

            //this will update the UI with message
            LoginActivity inst = LoginActivity.instance();
            if (inst != null) {
                if (smsMessageStr.contains("getbike Sign In")) {
                    inst.updateOtp(smsMessageStr.substring(0, 6));
                }
                if (smsMessageStr.contains("NETSECURE")) {
                    int index = smsMessageStr.indexOf(" is ");
                    if (index > 0) {
                        inst.updateOtp(smsMessageStr.substring(index + 4, index + 10));
                    }
                }
            }
        }
    }
}