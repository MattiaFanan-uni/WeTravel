package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.R;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * If the user is already subscribed it will be opened the  MapActivty,
 * else it will be opened the activity which allows to invite or get invited to a network.
 * DBDictionary adds persistence to dictionaries
 * DBDictionaryHelper manages the operations on the DB
 *
 * @author Riccardo Crociani
 */

public class LauncherActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Requesting permissions needed by Network-Dictionary library
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);

        // Setting up SMSJoinableNetManager
        SMSJoinableNetManager.getInstance().setup(this);
        SMSJoinableNetManager.getInstance().setJoinInvitationListener(new JoinInvitationListener<Invitation<SMSPeer>>() {
            @Override
            public void onJoinInvitationReceived(Invitation<SMSPeer> invitation) {
                SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation);
            }
        });

        // Setting up a TimerTask watching for new subscribers to my network
        setupSubscribersWatcher();

        // If the user is subscribed to at least one network launches the map activity,
        // otherwise launches the NotSubscribedActivity
        if (isSubscribed()) {
            Intent mapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(mapActivityIntent);
        } else {
            Intent notSubscribedActivityIntent = new Intent(this, NotSubscribedActivity.class);
            startActivity(notSubscribedActivityIntent);
        }
    }

    /**
     * Checks if the user is subscribed to at least one network.
     * @return True if the user is subscribed to a network, false otherwise
     */
    private boolean isSubscribed() {
        return SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 1;
    }

    /**
     * Setups a new TimerTask watching for new subscribers to my network every 1000ms
     */
    private void setupSubscribersWatcher() {
        Timer timer = new Timer();
        timer.schedule(new SubscribersWatcher(), 0, 1000);
    }


    /**
     * Gets subscriber list to my network and prints to the logcat
     */
    class SubscribersWatcher extends TimerTask {
        @Override
        public void run() {
            SMSPeer[] subsToNet = SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().toArray(new SMSPeer[] {});
            Log.d("NET_DEMO", Arrays.toString(subsToNet));
        }
    }
}