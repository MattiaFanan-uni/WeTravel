package com.gruppo3.wetravel.Activities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.eis.smslibrary.SMSManager;
import com.eis.smslibrary.SMSMessage;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.eis.smsnetwork.smsnetcommands.SMSInvitePeer;

public class customSMSInvitePeer extends SMSInvitePeer {

    /**
     * Constructor for the SMSInvitePeer command, requires data to work
     *
     * @param peerToInvite The SMSPeer to invite to the network
     */
    public customSMSInvitePeer(@NonNull SMSPeer peerToInvite) {
        super(peerToInvite);
    }

    /**
     * Execute the SMSInvitePeer logic: sends a request to join a network
     */
    protected void execute() {
        String message = RequestType.Invite.asString();
        SMSMessage messageToSend = new SMSMessage(peerToInvite, message);
        SMSManager.getInstance().sendMessage(messageToSend);
        Log.d("SMSINVITE_COMMAND", "Invitation Sent to: " + peerToInvite);
        SMSJoinableNetManager.getInstance().getInvitedPeers().addSubscriber(peerToInvite);
    }
}
