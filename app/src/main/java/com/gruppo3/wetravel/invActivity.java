package com.gruppo3.wetravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eis.communication.network.Invitation;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;

public class invActivity extends AppCompatActivity {

    private TextView inviter;
    private Button acceptButton;
    private static final String INVITED_YOU = "invited you to join his network";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv);

        Intent incomingIntent = getIntent();
        final String incomingName = incomingIntent.getStringExtra("inviter");

        inviter = (TextView) findViewById(R.id.inviter);
        inviter.setText(incomingName + INVITED_YOU);

        acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invitation invitationReceived = new com.eis.smsnetwork.SMSInvitation(new SMSPeer(incomingName));
                SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitationReceived);
            }
        });
    }
}
