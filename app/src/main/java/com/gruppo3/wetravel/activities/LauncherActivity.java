package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eis.smslibrary.SMSManager;
import com.eis.smslibrary.SMSMessage;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;

/**
 * This is the first displayed activity when the app is launched.<br>
 * It will verify and eventually request permissions and setup the {@link SMSJoinableNetManager}.<br>
 * If the user is already subscribed to a network, it will open a {@link MapActivity},
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
        SMSManager.getInstance().setReceivedListener(BroadcastReceiver.class, getApplicationContext());

        // If the user has permissions and is subscribed to at least one network launches the map activity,
        // if he has permissions but isn't subscribed to any network launches the NotSubscribedActivity,
        // otherwise requests permissions.
        if (checkPermissions()) {
            startSecondActivity();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ALL_RC);
        }
    }

    /**
     * Checks if needed permissions by this activity are granted.
     *
     * @return True if all permissions are granted, false if one or more permissions aren't granted yet.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private boolean checkPermissions() {
        for (String permission : PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        return true;
    }

    /**
     * {@inheritDoc}
     * When user finishes the permission granting "process" this method will be called.<br>
     * If one or more permissions are denied, it will show a dialog explaining why the permissions are needed
     * and that the app can't work without them.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_ALL_RC) {
            // If request is cancelled, the result arrays are empty
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    // Disable app functionalities because at least one permission was not granted
                    // TODO: Show permission "rationale" like specified in this method documentation and disable app functionalities
                    return;
                }

            // All permissions are granted, starting second activity
            startSecondActivity();
        }
    }

    /**
     * If the user is subscribed to at least one network, launches the {@link MapActivity},
     * otherwise it will launch {@link NotSubscribedActivity}.
     */
    private void startSecondActivity() {
        if (isSubscribed())
            startActivity(new Intent(this, MapActivity.class));
        else
            startActivity(new Intent(this, NotSubscribedActivity.class));

        finish(); // Closing this activity because has done its job
    }

    /**
     * Checks if the user is subscribed to at least one network.
     *
     * @return True if the user is subscribed to a network, false otherwise.
     */
    private boolean isSubscribed() {
        return SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 0;
    }
}