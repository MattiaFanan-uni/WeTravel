package com.gruppo3.wetravel.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.smslibrary.SMSManager;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapmanager.MapManager;
import com.gruppo3.wetravel.types.DestinationMarker;
import com.gruppo3.wetravel.util.Const;

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
public class MapActivity extends FragmentActivity implements JoinInvitationListener<Invitation<SMSPeer>> {
    /**
     * Manages all map UI and location retrieving operations.
     */
    private MapManager mapManager;
    private static final String NOT_SUBSCRIBED = "You are not subscribed to any network";

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

        // UI operations
        findViewById(R.id.friendButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), InviteUserActivity.class)));
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
     *
     * @return boolean True if the user is subscribed to a network
     */
    public boolean isSubscribed() {
        return SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 0;
    }

    public void setTextViewNotSubscribed() {
        TextView notSubscribedTextView = (TextView) findViewById(R.id.notSubscribedTextView);
        if (!isSubscribed()) {
            notSubscribedTextView.setText(NOT_SUBSCRIBED);
        }
        else notSubscribedTextView.setText("");
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

        if (requestCode == Const.INSTRUCTION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                checkAndRequestPermissions();
            }
        }

        if (mapManager != null)
            mapManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onJoinInvitationReceived(Invitation invitation) {
        runOnUiThread(() -> displayNewInvitationDialog(invitation));
    }

    /**
     * Checks permissions calling {@link #checkPermissions()} and if are not granted, starts an {@link InstructionActivity}.
     */
    private void checkAndRequestPermissions() {
        if (!checkPermissions())
            startActivityForResult(new Intent(getApplicationContext(), InstructionActivity.class), Const.INSTRUCTION_ACTIVITY);
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
        SMSJoinableNetManager.getInstance().setJoinInvitationListener(this);

        SMSManager.getInstance().setReceivedListener(BroadcastReceiver.class, getApplicationContext()); // Setting up a received message listener.

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapManager = new MapManager(this, mapFragment);
            mapManager.startLocationUpdates();
        }
    }

    /**
     * Shows an {@link AlertDialog} requesting the user to accept or decline the received invitation.
     *
     * @param invitation A received {@link Invitation<SMSPeer>}.
     */
    private void displayNewInvitationDialog(final Invitation<SMSPeer> invitation) {
        final char SPACE_SEPARATOR = ' ';
        new AlertDialog.Builder(this)
                .setTitle(invitation.getInviterPeer().getAddress() + SPACE_SEPARATOR + getString(R.string.invited_you))
                .setMessage(getString(R.string.join_network_question))
                .setPositiveButton(getString(R.string.accept), (dialog, id) -> {
                    SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation); // Accepting the invitation
                })
                .setNegativeButton(getString(R.string.decline), (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }
}
