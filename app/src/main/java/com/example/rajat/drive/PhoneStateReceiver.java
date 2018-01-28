package com.example.rajat.drive;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by RAJAT on 23/10/17.
 */


public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String MY_TAG = "the_custom_message";
    TelephonyManager tm;
    String incomingNumber = "";
    AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MY_TAG, "phone state");
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Get incoming number
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                rejectCall();
                startApp(context, incomingNumber);

            }
        }


        switch (tm.getCallState()) {

            case TelephonyManager.CALL_STATE_RINGING:
                String phoneNr = intent.getStringExtra("incoming_number");
                Toast.makeText(context, phoneNr, Toast.LENGTH_LONG).show();
                String phoneNumber = phoneNr;
                String smsBody = "I am Currently driving call me later.\n *message from drive-S";
                // Get the default instance of SmsManager
                SmsManager smsManager = SmsManager.getDefault();
                // Send a text based SMS
                smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
                rejectCall();

                break;
        }

    }

    private void startApp(Context context, String number) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("number", "Rejected incoming number:" + number);
        context.startActivity(intent);
    }

    private void rejectCall() {


        try {

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(tm.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(tm);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}

