package com.gruppo3.wetravel.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.R;

/**
 * This activity is opened when the user runs the app and is not subscribed yet
 * It allows the user to send an invitation to a friend or to accept or decline an incoming invitation
 *
 * @author Riccardo Crociani
 */

public class NotSubscribedActivity extends AppCompatActivity {

    private static final String INVITED_YOU = "Invited you";
    private static final String DO_YOU_WANT_TO_JOIN = "Do you want to join its network?";
    private static final String ACCEPT = "Accept";
    private static final String DECLINE = "Decline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_subscribed);
    }

    /**
     * It opens the activity that manages the invitations
     *
     * @param v Clicked view
     */
    public void buttonInvite_onClick(View v) {
        Intent openFriendsActivityIntent = new Intent(this, FriendsActivity.class);
        startActivity(openFriendsActivityIntent);
    }

    /**
     * Callback for received invitation to join a network from another user.
     *
     * @param invitation The received invitation.
     */
    /*@Override
    public void onJoinInvitationReceived(Invitation<SMSPeer> invitation) {
        createDialog(invitation);
    }*/

    /**
     * It is create a dialog to accept or decline the invitation received
     *
     * @param invitation The invitation Received
     */
    private void createDialog(final Invitation<SMSPeer> invitation) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());

        dialog.setMessage(invitation.getInviterPeer().getAddress() + INVITED_YOU)
                .setTitle(DO_YOU_WANT_TO_JOIN)
                .setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        replyInvitation(invitation);
                    }
                })
                .setNegativeButton(DECLINE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog.create().show();
    }

    /**
     * Accept the invitation and join the network
     *
     * @param invitation The invitation received
     */
    private void replyInvitation(Invitation<SMSPeer> invitation) {
        SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation);
    }
}