package com.gruppo3.wetravel.Invitation;

import android.util.Log;

import androidx.annotation.NonNull;

import com.eis.smslibrary.SMSManager;
import com.eis.smslibrary.SMSMessage;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSNetworkManager;

public class SMSSendInvitation extends SendInvitation<SMSInvitation> {

    SMSNetworkManager netManager;

    /**
     * Constructor for the SMSSendInvitation command, requires data to work
     *
     * @param invitation The SMSPeer to invite to the network
     * @param netManager A valid SMSJoinableNetManager, used by the command
     */
    public SMSSendInvitation(@NonNull SMSInvitation invitation, @NonNull SMSNetworkManager netManager) {
        super(invitation);
        this.netManager = netManager;
    }

    /**
     * Execute the SMSSendInvitation logic: sends a request to join a network
     */
    public void execute() {
        String message = RequestType.Invite.asString();
        SMSMessage messageToSend = new SMSMessage(invitation.getInviterPeer(), message);
        SMSManager.getInstance().sendMessage(messageToSend);
        Log.d("SMSINVITE_COMMAND", "Invitation Sent to: " + invitation.getInviterPeer());
        netManager.getInvitedPeers().addSubscriber(invitation.getInviterPeer());
    }
}