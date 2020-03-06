package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eis.communication.network.listeners.InviteListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.R;


/**
 * Called when the user is not subscribed to any network and he wants to invite a friend or accept an invitation.<br>
 *
 * @author Riccardo Crociani
 */
public class FriendsActivity extends AppCompatActivity {

    private EditText editTextFriendNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        editTextFriendNumber = findViewById(R.id.friendNumber); // EditText containing phone number of the user to invite
        findViewById(R.id.mapButton).setOnClickListener(v -> {
            if (SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 0) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "You must join a network", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sends an invitation to the phone number inserted in {@link #editTextFriendNumber}.<br>
     * A {@link Toast} will be shown with the result of the operation.
     */
    public void inviteButton_onClick(View v) {
        try {
            SMSPeer peerToInvite = new SMSPeer(editTextFriendNumber.getText().toString());
            SMSJoinableNetManager.getInstance().invite(peerToInvite, new InviteListener<SMSPeer, SMSFailReason>() {
                @Override
                public void onInvitationSent(SMSPeer invitedPeer) {
                    Toast.makeText(getApplicationContext(), "Invitation sent to " + invitedPeer.getAddress(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onInvitationNotSent(SMSPeer notInvitedPeer, SMSFailReason failReason) {
                    Toast.makeText(getApplicationContext(), "An error has occurred while sending invite", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Enter a number to invite", Toast.LENGTH_LONG).show();
        }
    }
}