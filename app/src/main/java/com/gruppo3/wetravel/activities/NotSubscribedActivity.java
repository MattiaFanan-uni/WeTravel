package com.gruppo3.wetravel.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.R;

/**
 * This activity is opened when the user runs the app and is not subscribed to any network yet.
 * It allows the user to send an invitation to a friend or to accept or decline an incoming invitation.
 *
 * @author Riccardo Crociani
 */

public class NotSubscribedActivity extends AppCompatActivity {

    // TODO: Should add these strings in strings resource file
    private static final String INVITED_YOU = "invited you!";
    private static final String DO_YOU_WANT_TO_JOIN = "Do you want to join its network?";
    private static final String ACCEPT = "ACCEPT";
    private static final String DECLINE = "DECLINE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_subscribed);

        // Setting up the listener for incoming invitation
        SMSJoinableNetManager.getInstance().setJoinInvitationListener(invitation -> NotSubscribedActivity.this.runOnUiThread(() -> createDialog(invitation)));
    }

    /**
     * It opens the activity that manages the invitations.
     *
     * @param v Clicked view.
     */
    public void buttonInvite_onClick(View v) {
        startActivity(new Intent(this, FriendsActivity.class));
    }

    /**
     * Shows a an {@link AlertDialog} requesting the user to accept or decline the received invitation.
     *
     * @param invitation A received {@link Invitation<SMSPeer>}.
     */
    private void createDialog(final Invitation<SMSPeer> invitation) {
        new AlertDialog.Builder(this)
                .setTitle(invitation.getInviterPeer().getAddress() + INVITED_YOU)
                .setMessage(DO_YOU_WANT_TO_JOIN)
                .setPositiveButton(ACCEPT, (dialog, id) -> {
                    replyInvitation(invitation);
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                })
                .setNegativeButton(DECLINE, (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    /**
     * Accept the invitation, joins the network and start the {@link MapActivity}.
     *
     * @param invitation An {@link Invitation<SMSPeer>} to accept.
     */
    private void replyInvitation(Invitation<SMSPeer> invitation) {
        SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation); // Accepting the invitation
        startActivity(new Intent(this, MapActivity.class)); // Starting the map activity
    }
}