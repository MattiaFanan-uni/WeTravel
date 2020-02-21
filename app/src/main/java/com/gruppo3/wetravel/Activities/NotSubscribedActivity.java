package com.gruppo3.wetravel.Activities;

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

public class NotSubscribedActivity extends AppCompatActivity implements JoinInvitationListener<Invitation<SMSPeer>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_subscribed);

    }

    /**
     * change the activity to the one that manage the invitations
     *
     * @param v Clicked view
     */
    public void buttonInvite_onClick(View v) {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    /**
     * Callback for received invitation to join a network from another user.
     *
     * @param invitation The received invitation.
     */
    @Override
    public void onJoinInvitationReceived(Invitation<SMSPeer> invitation) {
        String inviterAddress = invitation.getInviterPeer().getAddress();

    }

    private void createDialog(final Invitation<SMSPeer> invitation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        builder.setMessage(invitation.getInviterPeer().getAddress()+" ti ha invitato")
                .setTitle("SEI STATO INVITATO AD UNIRTI AD UNA RETE")
                .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        replyInvitation(invitation);
                    }
                })
                .setNegativeButton("decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();//TODO maybe this is redundant
                    }
                });

        AlertDialog dialog = builder.create();
    }

    private void replyInvitation(Invitation<SMSPeer> invitation){
        SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation);
    }
}
