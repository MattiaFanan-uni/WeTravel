package com.gruppo3.wetravel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eis.smsnetwork.SMSInvitation;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.eis.smslibrary.SMSPeer;

import com.eis.smsnetwork.smsnetcommands.SMSSendInvitation;
import com.gruppo3.wetravel.R;


/**
 * Called when the user is not subscribed and He wants to invite a friend or accept an invitation
 *
 * @author Riccardo Crociani
 */
public class FriendsActivity extends AppCompatActivity {

    private EditText friendNumber;
    private Button inviteButton;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        checkForSmsPermission();

        // friendNumber: the number the user wants to send an invitation to
        friendNumber = findViewById(R.id.friendNumber);
        inviteButton = findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SMSPeer friendToInvite = new SMSPeer(friendNumber.getText().toString());
                    SMSInvitation invitation = new SMSInvitation(friendToInvite);
                    SMSSendInvitation sendInvitation = new SMSSendInvitation(invitation, SMSJoinableNetManager.getInstance());
                    sendInvitation.execute();
                } catch (Exception e ) {
                    Toast.makeText(getApplicationContext(), "Enter phone number", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Asks the user for the permission to send sms
     */
    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }
}