package com.gruppo3.wetravel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eis.communication.network.Invitation;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.eis.smslibrary.SMSPeer;
import com.gruppo3.wetravel.Invitation.RequestType;
import com.gruppo3.wetravel.Invitation.SMSInvitation;
import com.gruppo3.wetravel.Invitation.SMSSendInvitation;
import com.gruppo3.wetravel.BroadcastReceivers.MessageListener;
import com.gruppo3.wetravel.R;

import java.util.ArrayList;


public class FriendsActivity extends AppCompatActivity {

    private EditText friendNumber;
    private Button inviteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        /**
         * Enter the number you want to send an invitation to
         */
        friendNumber = findViewById(R.id.friendNumber);
        inviteButton = findViewById(R.id.inviteButton);
        final SMSPeer friendToInvite = new SMSPeer(friendNumber.getText().toString());
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSInvitation invitation = new SMSInvitation(friendToInvite);
                SMSSendInvitation sendInvitation = new SMSSendInvitation(invitation, SMSJoinableNetManager.getInstance());
                sendInvitation.execute();
            }
        });
    }
}
