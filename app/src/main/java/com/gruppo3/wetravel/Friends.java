package com.gruppo3.wetravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eis.communication.network.Invitation;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.eis.smslibrary.SMSPeer;
import com.gruppo3.wetravel.Invitation.RequestType;
import com.gruppo3.wetravel.Invitation.SMSInvitation;
import com.gruppo3.wetravel.Invitation.SMSSendInvitation;

import java.util.ArrayList;


public class Friends extends AppCompatActivity implements MessageListener {

    private EditText friendNumber;
    private Button inviteButton;
    private ListView invitationList;
    private ArrayList<String> invList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        /**
         * Enter the number you want to send an invitation to
         */
        friendNumber = (EditText) findViewById(R.id.friendNumber);
        inviteButton = (Button) findViewById(R.id.inviteButton);
        final SMSPeer friendToInvite = new SMSPeer(friendNumber.getText().toString());
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSInvitation invitation = new SMSInvitation(friendToInvite);
                SMSSendInvitation sendInvitation = new SMSSendInvitation(invitation, SMSJoinableNetManager.getInstance());
                sendInvitation.execute();
            }
        });


        /**
         * When an invitation is received
         */
        MessageReceiver.bindListener(this);
        invitationList = (ListView) findViewById(R.id.invitationList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, invList);    //R.layout.activity_friends, R.id.invitationTextView, invList
        invitationList.setAdapter(arrayAdapter);
        invitationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openInvitatioIntent = new Intent(Friends.this, invActivity.class);
                openInvitatioIntent.putExtra("inviter", invList.get(position));
                startActivity(openInvitatioIntent);
            }
        });
    }


    /**
     * If the first symbol is "RequestType.Invite -> @" it means that we received an invitation to join the network
     * The invitation received is added to invList and it waits to be accepted
     *
     * @param message Message received
     * @param peer    who sent the message
     */
    @Override
    public void messageReceived(String message, SMSPeer peer) {
        if (message.substring(0, 1).equals(RequestType.Invite.asString())) {
            Invitation invitationReceived = new com.eis.smsnetwork.SMSInvitation(peer);
            invList.add(peer.getAddress());
        }
    }
    //TODO add a button that opens the activity Friends
}
