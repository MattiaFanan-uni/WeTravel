package com.gruppo3.wetravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.eis.smslibrary.SMSPeer;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String message = smsMessage.getMessageBody();
            String number = smsMessage.getOriginatingAddress();
            SMSPeer peerNumber = new SMSPeer(number);
            mListener.messageReceived(message, peerNumber);
        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}