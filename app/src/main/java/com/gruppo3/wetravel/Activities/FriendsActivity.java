package com.gruppo3.wetravel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eis.smslibrary.SMSPeer;

import com.eis.smsnetwork.smsnetcommands.SMSInvitePeer;
import com.gruppo3.wetravel.R;


/**
 * Called when the user is not subscribed and He wants to invite a friend or accept an invitation
 *
 * @author Riccardo Crociani
 */
public class FriendsActivity extends AppCompatActivity {

    private EditText friendNumber;
    private Button inviteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        // friendNumber: the number the user wants to send an invitation to
        friendNumber = findViewById(R.id.friendNumber);
        inviteButton = findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSPeer friendToInvite = new SMSPeer(friendNumber.getText().toString());
                customSMSInvitePeer invitePeer = new customSMSInvitePeer(friendToInvite);
                invitePeer.execute();


            }
        });
    }
}