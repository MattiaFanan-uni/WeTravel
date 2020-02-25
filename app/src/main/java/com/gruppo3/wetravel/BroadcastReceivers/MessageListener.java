package com.gruppo3.wetravel.BroadcastReceivers;

import com.eis.smslibrary.SMSPeer;

public interface MessageListener {
    /**
     * @param message Message
     */
    void messageReceived(String message, SMSPeer peer);
}