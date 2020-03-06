package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.R;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the first launched activity.
 * If the user is already subscribed to a network, it will opened a {@link MapActivity},
 * otherwise will be opened a {@link NotSubscribedActivity} which allows to invite or receive invites to a network.
 *
 * @author Riccardo Crociani, Giovanni Barca
 */

public class LauncherActivity extends AppCompatActivity {

    private static final int PERMISSIONS_ALL_RC = 1; // Permissions request code

    /**
     * Application needed permissions.
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Setting up SMSJoinableNetManager
        SMSJoinableNetManager.getInstance().setup(this);

        // Setting up a TimerTask watching for new subscribers to my network
        //setupSubscribersWatcher();

        // If the user has permissions and is subscribed to at least one network launches the map activity,
        // if he has permissions but isn't subscribed to any network launches the NotSubscribedActivity,
        // otherwise requests permissions.
        boolean permissionsGranted = checkPermissions();
        boolean isSubscribed = isSubscribed();
        if (permissionsGranted) {
            if (isSubscribed) {
                Intent mapActivityIntent = new Intent(this, MapActivity.class);
                startActivity(mapActivityIntent);
            } else {
                Intent notSubscribedActivityIntent = new Intent(this, NotSubscribedActivity.class);
                startActivity(notSubscribedActivityIntent);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ALL_RC);
        }
    }

    /**
     * Checks if needed permissions by this activity are granted.
     *
     * @return True if all permissions are granted, false if one or more permissions aren't granted yet.
     *
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private boolean checkPermissions() {
        for (String permission : PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Checking again permission to avoid runtime exceptions
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationAndLocationUpdates(); // Enabling and displaying current location (blue dot on map)
                }
            } else {
                // Permission denied
                // Functionalities depending by this permission will no longer work
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }*/
        // TODO: Launch here the NotSubscribedActivity
    }

    /**
     * Checks if the user is subscribed to at least one network.
     * @return True if the user is subscribed to a network, false otherwise.
     */
    private boolean isSubscribed() {
        return SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 0;
    }

    /**
     * Setups a new TimerTask watching for new subscribers to current device network every 1000ms.
     */
    private void setupSubscribersWatcher() {
        Timer timer = new Timer();
        timer.schedule(new SubscribersWatcher(), 0, 1000);
    }


    /**
     * Gets subscribers list to current device network and prints to the logcat.
     */
    class SubscribersWatcher extends TimerTask {
        /**
         * Method executed when a new {@link Timer} using {@link SubscribersWatcher} is scheduled.
         */
        @Override
        public void run() {
            SMSPeer[] subsToNet = SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().toArray(new SMSPeer[] {});
            Log.d("NET_DEMO", Arrays.toString(subsToNet));
        }
    }
}