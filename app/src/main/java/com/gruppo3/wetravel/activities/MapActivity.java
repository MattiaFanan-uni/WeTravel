package com.gruppo3.wetravel.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.eis.smslibrary.SMSManager;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapmanager.MapManager;
import com.gruppo3.wetravel.mapmanager.types.DestinationMarker;
import com.gruppo3.wetravel.util.RequestCode;

/**
 * This activity shows a {@link GoogleMap} with device current location and {@link DestinationMarker DestinationMarkers} for available missions.
 * A mission is a request made by an user that can be executed by everyone in the network.
 * <p>
 * To correctly implement the map view, this class extends {@link FragmentActivity}.
 * <p>
 * This activity needs the permissions specified in {@link InstructionActivity#PERMISSIONS} to let the map and the network work correctly.
 * It also needs Google Play Services installed and this is automatically checked when {@link com.google.android.gms.maps.MapFragment#getMapAsync(OnMapReadyCallback)} is called.
 * <p>
 * This class, to work, implements a {@link MapManager} that manages all map UI and location retrieving operations.
 *
 * @author Giovanni Barca
 */
public class MapActivity extends FragmentActivity {
    /**
     * Manages all map UI and location retrieving operations.
     */
    private MapManager mapManager;

    /**
     * {@inheritDoc}
     * When this activity is created, we immediately check for permissions calling {@link #checkAndRequestPermissions()}.
     * If permissions are granted, the map will be shown and user can start interacting with it.
     * Otherwise a {@link InstructionActivity} is launched to explain why we need permissions and request them.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkAndRequestPermissions(); // Checking and requesting permissions

        // Associating activity opening to each button
        findViewById(R.id.friendButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FriendsActivity.class)));
        findViewById(R.id.getInvitedButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NotSubscribedActivity.class)));
        findViewById(R.id.newMissionButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddMarkerActivity.class)));
    }

    /**
     * When this activity is resumed, resumes location updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mapManager != null)
            mapManager.startLocationUpdates();
    }

    /**
     * When this activity is paused, suspends location updates to preserve battery and avoid useless operations.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mapManager != null)
            mapManager.stopLocationUpdates();
    }

    /**
     * {@inheritDoc}
     * This method is called when an activity stops executing.
     * In this case, when {@link InstructionActivity} stops executing, it initializes the system calling {@link #init()}.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.INSTRUCTION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                checkAndRequestPermissions();
            }
        }
    }

    /**
     * Checks permissions calling {@link #checkPermissions()} and if are not granted, starts an {@link InstructionActivity}.
     */
    private void checkAndRequestPermissions() {
        if (!checkPermissions())
            startActivityForResult(new Intent(getApplicationContext(), InstructionActivity.class), RequestCode.INSTRUCTION_ACTIVITY);
        else
            init();
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

    /**
     * Checks (and eventually requests) again for permissions.
     * Then initializes an instance of {@link MapManager} for managing map UI and location operations and {@link SMSJoinableNetManager} for network operations.
     * <p>
     * {@link com.google.android.gms.maps.MapFragment#getMapAsync(OnMapReadyCallback)} checks if Google PLay Services are installed. If not, let the user install them.
     * When this is done, {@link MapManager#onMapReady(GoogleMap)} and {@link MapManager#startLocationUpdates()} are called.
     */
    private void init() {
        SMSJoinableNetManager.getInstance().setup(this); // Setting up SMSJoinableNetManager.
        SMSManager.getInstance().setReceivedListener(BroadcastReceiver.class, getApplicationContext()); // Setting up a received message listener.

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapManager = new MapManager(this, mapFragment);
            mapManager.startLocationUpdates();
        }
    }
}
