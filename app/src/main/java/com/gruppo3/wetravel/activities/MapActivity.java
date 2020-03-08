package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Coordination;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.eis.communication.network.listeners.GetResourceListener;
import com.eis.communication.network.listeners.SetResourceListener;
import com.eis.smslibrary.SMSMessage;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapmanager.deprecated.DirectionsManager;
import com.gruppo3.wetravel.mapmanager.types.DestinationMarker;
import com.gruppo3.wetravel.mapmanager.types.ViewMap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This activity shows a {@link GoogleMap} with device current position and {@link DestinationMarker} for available "missions".<br>
 * To correctly implement a map view, this class extends {@link FragmentActivity}.<br>
 * <p>
 * This activity needs {@link Manifest.permission#ACCESS_FINE_LOCATION} or {@link Manifest.permission#ACCESS_COARSE_LOCATION} permission.
 * It also needs Google Play Services installed and this is automatically checked before {@link #onMapReady(GoogleMap)} is being called.<br>
 * <p>
 * When an instance of this class is created, a default {@link ViewMap} object is registered.
 * User can register a custom one calling {@link #registerViewMap(ViewMap)}.
 *
 * @author Giovanni Barca
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates
    private boolean updateCamera = true;
    private Location location;

    /**
     * When a new {@link MapActivity} object is created, a default {@link ViewMap} object is registered to the created instance.
     */
    @NonNull
    private ViewMap viewMap = new ViewMap(); // Class with map display and manipulation operations

    /**
     * This callback is triggered when a new location update is received.<br>
     * It centers the camera to the last location received with an app-defined zoom constant.
     */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) throws NullPointerException {
            if (mMap == null)
                throw new NullPointerException("Can't follow current position. Map is set to null.");

            location = locationResult.getLastLocation();
            if (updateCamera)
                updateCameraOnCurrentLocation();
        }
    };

    /**
     * When this activity is created, a {@link SupportMapFragment} associates to the map view
     * and a {@link FusedLocationProviderClient} is taken, to provide current device position, using {@link LocationServices#getFusedLocationProviderClient(Context)}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Gets the Location Provider Client for requesting location updates to
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Button friendButton = (Button) findViewById(R.id.friendButton);
        Button getInvited = (Button) findViewById(R.id.getInvitedButton);
        friendButton.setOnClickListener(v -> {
            Intent openFriendsActivity = new Intent(getApplicationContext(), FriendsActivity.class);
            startActivity(openFriendsActivity);
        });
        getInvited.setOnClickListener(v -> {
            Intent openNotSubscribedActivity = new Intent(getApplicationContext(), NotSubscribedActivity.class);
            startActivity(openNotSubscribedActivity);
        });

    }

    /**
     * When this activity isn't displayed, suspends location updates to preserve battery and avoid useless operations.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Stop location updates when this activity is no longer active
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            updateCamera = false;
        }
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services are not installed on the device, the user will be prompted to install
     * them inside the {@link SupportMapFragment}.
     * This method will only be triggered once the user has installed Google Play services and returned to the app.
     *
     * @param googleMap A {@link GoogleMap} instance.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Getting the map ready to work (main map on the activity layout)

        locationRequest = viewMap.getLocationRequest(); // Gets ViewMap LocationRequest
        enableMyLocationAndLocationUpdates(); // Enabling MyLocation button and starting following current location

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            /**
             * {@inheritDoc}
             * Listen to map camera movement.
             * If the user moves the map, this method stops location updates.
             *
             * @param reason The reason for the camera change. Possible values:
             *               <ul>
             *                  <li>REASON_GESTURE: User gestures on the map.</li>
             *                  <li>REASON_API_ANIMATION: Default animations resulting from user interaction.</li>
             *                  <li>REASON_DEVELOPER_ANIMATION: Developer animations.</li>
             *               </ul>
             *
             * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnCameraMoveStartedListener">GoogleMap.OnCameraMoveStartedListener()</a>
             * @see FusedLocationProviderClient#removeLocationUpdates(LocationCallback)
             */
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                    updateCamera = false;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            /**
             * {@inheritDoc}
             * Listen to map marker's clicks.
             * To avoid auto-moving map's camera while user is interacting with a marker, this method stops location updates.
             *
             * @param marker The marker the user clicked on.
             * @return false because the listener hasn't consumed the event and the default behavior should occur. The default behavior is for the camera to move to the marker and an info window to appear.
             *
             * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMarkerClickListener">GoogleMap.OnMarkerClickListener()</a>
             */
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    updateCamera = false;
                    if (location != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showRoute(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition(), TransportMode.DRIVING);
                            }
                        });
                    }
                }
                return false;
            }
        });

        // If user clicks on MyLocation button, we resume following current location
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            /**
             * {@inheritDoc}
             * Listen to MyLocation button clicks.
             * If the user clicks MyLocation button, we resume following current location.
             *
             * @return false because the listener hasn't consumed the event and the default behavior should occur. The default behavior is for the camera move such that it is centered on the user location.
             *
             * {@link GoogleMap.OnMyLocationButtonClickListener}
             * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMyLocationButtonClickListener">GoogleMap.OnMyLocationButtonClickListener()</a>
             */
            @Override
            public boolean onMyLocationButtonClick() {
                updateCamera = true;
                return false;
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSJoinableNetManager.getInstance().setResource("ciao", "45.586760,11.959212,testMarker", new SetResourceListener<String, String, SMSFailReason>() {
                    @Override
                    public void onResourceSet(String key, String value) {
                        Log.d("MAP_DEMO", "Resource set");
                    }

                    @Override
                    public void onResourceSetFail(String key, String value, SMSFailReason reason) {
                        Log.d("MAP_DEMO", "An error has occured while setting a resource");
                    }
                });
            }
        });

        BroadcastReceiver.setDelegate(new BroadcastReceiver.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(SMSMessage message, RequestType requestType, @Nullable String[] keys, @Nullable String[] values) {
                if (requestType == RequestType.AddResource && keys != null && values != null) {
                    for (int i = 0; i < keys.length; i++) {
                        String[] parameters = values[i].split(",");
                        DestinationMarker destinationMarker = new DestinationMarker(new LatLng(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1])), parameters[2]);
                        if (mMap != null)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addMarker(destinationMarker);
                                }
                            });
                    }
                }
            }
        });
    }

    /**
     * Requests location updates and enables MyLocation (blue dot on the map).
     *
     * @throws NullPointerException if no {@link FusedLocationProviderClient} is registered nor a {@link #mMap} is initialized.
     */
    private void enableMyLocationAndLocationUpdates() throws NullPointerException {
        if (mMap == null)
            throw new NullPointerException("Can't enable MyLocation. Map is set to null.");

        if (fusedLocationProviderClient == null)
            throw new NullPointerException("Can't request location updates. FusedLocationProviderClient is set to null.");

        // Enabling "MyLocation" function
        mMap.setMyLocationEnabled(true);

        // Enabling location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        updateCamera = true;
    }

    /**
     * Register a custom {@link ViewMap} object to this activity.
     *
     * @param viewMap Add a {@link ViewMap} to this activity for UI and location customization. Never null.
     */
    public void registerViewMap(@NonNull ViewMap viewMap) {
        this.viewMap = viewMap;
    }

    private void updateCameraOnCurrentLocation() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(viewMap.getMapZoom())
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Add a marker on the map with the parameters included in the given {@link DestinationMarker}.
     *
     * @param destinationMarker {@link DestinationMarker} object containing information about the marker to be added. Never null.
     * @throws NullPointerException if map has not been yet initialised.
     */
    public void addMarker(@NonNull DestinationMarker destinationMarker) throws NullPointerException {
        if (mMap == null)
            throw new NullPointerException("Can't add markers. Map is null.");

        mMap.addMarker(new MarkerOptions()
                .position(destinationMarker.getLatLng())
                .title(destinationMarker.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(destinationMarker.getColor()))
        ).setTag(destinationMarker.getObject());
    }

    /**
     * Shows the shortest route from origin to dest.
     *
     * @param origin        Object of type {@link LatLng} referring to the route's origin coordinates. Never null.
     * @param dest          Object of type {@link LatLng} referring to the route's destination coordinates. Never null.
     * @param directionMode Specifies in which {@link DirectionsManager.DirectionModes direction mode} the route has to be calculated (driving, walking, bicycle or transit mode). Never null.
     * @throws NullPointerException if map has not been yet initialised.
     */
    public void showRoute(@NonNull LatLng origin, @NonNull LatLng dest, @NonNull String directionMode) throws NullPointerException {
        if (mMap == null)
            throw new NullPointerException("Can't show route. Map is null.");

        GoogleDirection.withServerKey("AIzaSyBAqE4yh01-eD6Tv2nQ7lxIMsFik807yIY") // TODO: Change API key on release
                .from(origin)
                .to(dest)
                .transportMode(directionMode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            ArrayList<LatLng> directionPoint = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LatLng southwestCoordination = route.getBound().getSouthwestCoordination().getCoordination();
                                    LatLng northeastCoordination = route.getBound().getNortheastCoordination().getCoordination();
                                    LatLngBounds bounds = new LatLngBounds(southwestCoordination, northeastCoordination);
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPoint, 5, Color.BLUE));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                                }
                            });
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
    }
}
