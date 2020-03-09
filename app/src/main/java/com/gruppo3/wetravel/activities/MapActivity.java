package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.eis.communication.network.listeners.SetResourceListener;
import com.eis.smslibrary.SMSManager;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapmanager.types.DestinationMarker;
import com.gruppo3.wetravel.mapmanager.types.ViewMap;

import java.util.ArrayList;

/**
 * This activity shows a {@link GoogleMap} with device current position and {@link DestinationMarker} for available "missions".
 * When a {@link DestinationMarker} is clicked, the corresponding title and the fastest driving route will be shown.
 * <p>
 * To correctly implement a map view, this class extends {@link FragmentActivity}.
 * <p>
 * This activity needs {@link Manifest.permission#ACCESS_FINE_LOCATION} or {@link Manifest.permission#ACCESS_COARSE_LOCATION} permission.
 * It also needs Google Play Services installed and this is automatically checked before {@link #onMapReady(GoogleMap)} is being called.
 * <p>
 * When an instance of this class is created, a default {@link ViewMap} object is registered.
 * User can register a custom one calling {@link #registerViewMap(ViewMap)}.
 *
 * @author Giovanni Barca
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    /**
     * A default {@link ViewMap} object is registered to this class.
     * If the user wants to use a custom {@link ViewMap}, he can call {@link #registerViewMap(ViewMap)}. Never null.
     */
    @NonNull
    private static ViewMap viewMap = new ViewMap(); // Class with map display and manipulation operations

    /**
     * String used to separate marker parameters when it's set as a network resource.
     */
    final private String PARAMETER_SEPARATOR = ",";

    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates
    private Location location; // Latest acquired location
    private boolean updateCamera = true; // If true enables camera animation to center the map to current location

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
     * Register a custom {@link ViewMap} object to this activity.
     *
     * @param viewMap Add a {@link ViewMap} to this activity for UI and location customization. Never null.
     */
    public static void registerViewMap(@NonNull ViewMap viewMap) {
        MapActivity.viewMap = viewMap;
    }

    /**
     * When this activity is created, we immediately check for permissions calling {@link #checkPermissions()}.
     * If permissions are granted, the map will be shown and user can start interacting with it.
     * Otherwise a {@link InstructionActivity} is launched to explain and request permissions.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!checkPermissions())
            startActivity(new Intent(getApplicationContext(), InstructionActivity.class));
    }

    /**
     * When this activity is resumed, a {@link SupportMapFragment} associates to the map view
     * and a {@link FusedLocationProviderClient} is taken, to provide current device position, using {@link LocationServices#getFusedLocationProviderClient(Context)}.
     * Then {@link BroadcastReceiver#setDelegate(BroadcastReceiver.OnMessageReceivedListener)} is called to know when a new resource is set (usually it's a mission resource).
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Checking again permissions to avoid exceptions.
        if (!checkPermissions())
            return;

        SMSJoinableNetManager.getInstance().setup(this); // Setting up SMSJoinableNetManager.
        SMSManager.getInstance().setReceivedListener(BroadcastReceiver.class, getApplicationContext()); // Setting up a received message listener.

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Gets the Location Provider Client for requesting location updates to.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Instantiating a new OnMessageReceivedListener to know when a new resource is set in the network.
        // In this app implementation, a resource is always a mission marker containing coordinates and a title.
        BroadcastReceiver.setDelegate((message, requestType, keys, values) -> {
            if (requestType == RequestType.AddResource && keys != null && values != null) {
                for (int i = 0; i < keys.length; i++) {
                    String[] parameters = values[i].split(PARAMETER_SEPARATOR);
                    DestinationMarker destinationMarker = new DestinationMarker(new LatLng(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1])), parameters[2]);
                    if (mMap != null)
                        runOnUiThread(() -> addMarker(destinationMarker));
                }
            }
        });

        // UI operations
        findViewById(R.id.friendButton).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
        });

        findViewById(R.id.getInvitedButton).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NotSubscribedActivity.class));
        });

        findViewById(R.id.friendButton).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
        });

        findViewById(R.id.getInvitedButton).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NotSubscribedActivity.class));
        });

        findViewById(R.id.newMissionButton).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddMarkerActivity.class));
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
     * <p>
     * In addition to enabling location requests, this method setups all listeners, which are:
     * <ul>
     *     <li>OnCameraMoveStartedListener</li>
     *     <li>OnMarkerClickListener</li>
     *     <li>OnMyLocationButtonClickListener</li>
     *     <li>OnMapClickListener</li>
     * </ul>
     * What each listener accomplish is explained in the corresponding documentation.
     *
     * @param googleMap A {@link GoogleMap} instance.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Getting the map ready to work (main map on the activity layout)

        locationRequest = viewMap.getLocationRequest(); // Gets ViewMap LocationRequest
        enableMyLocationAndLocationUpdates(); // Enabling MyLocation button and starting following current location

        // Listen to map camera movement. If the user moves the map, this method stops location updates.
        // To continue executing default behavior, we return false.
        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                updateCamera = false;
        });

        // Listen to map marker's clicks. To avoid auto-moving map's camera while user is interacting with a marker, this method stops location updates.
        // To continue executing default behavior, we return false.
        mMap.setOnMarkerClickListener(marker -> {
            if (marker != null) {
                updateCamera = false;
                if (location != null) {
                    runOnUiThread(() -> showRoute(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition(), TransportMode.DRIVING));
                }
            }
            return false;
        });

        // Listen to MyLocation button clicks. If the user clicks MyLocation button, we resume following current location.
        // To continue executing default behavior, we return false.
        mMap.setOnMyLocationButtonClickListener(() -> {
            updateCamera = true;
            return false;
        });

        mMap.setOnMapClickListener(latLng -> {
            // TODO: start AddMarkerActivity
            setMission(latLng);
        });
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

    private void setMission(LatLng latLng) {
        SMSJoinableNetManager.getInstance().setResource(
                "ciao",
                latLng.latitude + PARAMETER_SEPARATOR + latLng.longitude + PARAMETER_SEPARATOR + "title",
                new SetResourceListener<String, String, SMSFailReason>() {
                    @Override
                    public void onResourceSet(String key, String value) {
                        Log.d("MAP_DEMO", "Resource set. Value: " + value);
                    }

                    @Override
                    public void onResourceSetFail(String key, String value, SMSFailReason reason) {
                        Log.d("MAP_DEMO", "An error has occured while setting a resource");
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
     * If {@link #updateCamera} is true, this method is called and the map moved to have always the current location (blue dot) at center of the screen.
     */
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
     * @param directionMode Specifies in which {@link TransportMode transport mode} the route has to be calculated (driving, walking, bicycle or transit mode). Never null.
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
                        throw new RuntimeException("Something went wrong while computing route");
                    }
                });
    }
}
