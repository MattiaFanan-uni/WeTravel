package com.gruppo3.wetravel.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.eis.smslibrary.SMSManager;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.location.LocationManager;
import com.gruppo3.wetravel.mapmanager.MapManager;

public class MapActivity extends FragmentActivity {

    private MapManager mapManager;
    private LocationManager locationManager;

    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!checkPermissions())
            startActivity(new Intent(getApplicationContext(), InstructionActivity.class));

        firstRun = false;

        // Associating activity opening to each button
        findViewById(R.id.friendButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FriendsActivity.class)));
        findViewById(R.id.getInvitedButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NotSubscribedActivity.class)));
        findViewById(R.id.friendButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FriendsActivity.class)));
        findViewById(R.id.getInvitedButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NotSubscribedActivity.class)));
        findViewById(R.id.newMissionButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddMarkerActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Doesn't execute following code if the activity is on the first run (should manage permissions before)
        if (firstRun)
            return;

        SMSJoinableNetManager.getInstance().setup(this); // Setting up SMSJoinableNetManager.
        SMSManager.getInstance().setReceivedListener(BroadcastReceiver.class, getApplicationContext()); // Setting up a received message listener.

        if (mapManager == null)
            mapManager = new MapManager(this);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(mapManager);

        mapManager.startLocationUpdates();
        mapManager.updateCamera = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapManager.stopLocationUpdates();
    }

    /**
     * Checks if needed permissions by this activity are granted.
     *
     * @return True if all permissions are granted, false if one or more permissions aren't granted yet.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private boolean checkPermissions() {
        for (String permission : InstructionActivity.PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        return true;
    }
}
