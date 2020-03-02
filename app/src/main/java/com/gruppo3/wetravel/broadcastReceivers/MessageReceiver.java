package com.gruppo3.wetravel.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.eis.smslibrary.SMSPeer;

import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {

    private static ArrayList<MessageListener> listeners;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isAtLeastVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");

        String format = bundle.getString("format");

        for (int i = 0; i < pdus.length; i++) {

            SmsMessage smsMessage;

            if(isAtLeastVersionM)
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i],format);
            else
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String message = smsMessage.getMessageBody();
            String number = smsMessage.getOriginatingAddress();
            SMSPeer peerNumber = new SMSPeer(number);


            if(listeners !=null)
                for(MessageListener listener: listeners)
                    listener.messageReceived(message, peerNumber);
        }
    }

    public static void bindListener(MessageListener listener) {
        if(listeners == null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }
}