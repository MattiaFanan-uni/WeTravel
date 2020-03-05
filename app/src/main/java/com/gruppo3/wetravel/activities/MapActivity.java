package com.gruppo3.wetravel.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapManager.DirectionsManager;
import com.gruppo3.wetravel.mapManager.types.DestinationMarker;
import com.gruppo3.wetravel.mapManager.types.ViewMap;

/**
 * This activity shows a map with device current position and markers for available "missions".<br>
 * To correctly implement a Map view, this class extends {@link FragmentActivity}.<br>
 * <p>
 * This activity needs {@link Manifest.permission#ACCESS_FINE_LOCATION} (or {@link Manifest.permission#ACCESS_COARSE_LOCATION}) permission.
 * It also needs Google Play Services installed and this is automatically checked before {@link #onMapReady(GoogleMap)} is being called.<br>
 * <p>
 * When an instance of this class is created, a default {@link ViewMap} object is registered.
 * User can register a custom one calling {@link #registerViewMap(ViewMap)}.
 *
 * @author Giovanni Barca
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1; // Request code for ACCESS_FINE_LOCATION permission

    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates

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
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .zoom(viewMap.getMapZoom())
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (NullPointerException e) {
            Log.d("MapActivity", Log.getStackTraceString(e));
            finish(); // Closing activity if the map has not been found
        }

        // Gets the Location Provider Client for requesting location updates to
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * When this activity isn't displayed, suspends location updates to preserve battery and avoid useless operations.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Stop location updates when this activity is no longer active
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Checks if needed permissions by this activity are granted and eventually asks them.
     * If permissions are granted then requests current location updates and shows it on the map with a blue dot.
     *
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions already granted
                enableMyLocationAndLocationUpdates();  // Enabling and displaying current location (blue dot on map)
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
        } else {
            enableMyLocationAndLocationUpdates(); // Enabling and displaying current location (blue dot on map)
        }
    }

    /**
     * {@inheritDoc}
     * Method invoked when the user chooses to grant a permission or not.
     *
     * @see FragmentActivity#onRequestPermissionsResult(int, String[], int[])
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
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

        checkPermissions(); // Checks (and eventually asks for) permissions needed by this activity

        locationRequest = viewMap.getLocationRequest(); // Gets ViewMap LocationRequest

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
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                return false;
            }
        });
    }

    /**
     * Requests location updates and enables MyLocation (blue dot on the map).
     *
     * @throws NullPointerException if no {@link FusedLocationProviderClient} is registered nor a map is initialized.
     */
    private void enableMyLocationAndLocationUpdates() throws NullPointerException {
        Preconditions.checkNotNull(mMap, "Can't enable MyLocation. Map is set to null.");
        Preconditions.checkNotNull(fusedLocationProviderClient, "Can't request location updates. FusedLocationProviderClient is set to null.");

        // Enabling "MyLocation" function
        mMap.setMyLocationEnabled(true);

        // Enabling location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * Register a custom {@link ViewMap} object to this activity.
     *
     * @param viewMap Add a {@link ViewMap} to this activity for UI and location customization. Never null.
     */
    public void registerViewMap(@NonNull ViewMap viewMap) {
        this.viewMap = viewMap;
    }

    /**
     * Add a marker on the map with the parameters included in the given {@link DestinationMarker}.
     *
     * @param destinationMarker {@link DestinationMarker} object containing information about the marker to be added. Never null.
     * @throws NullPointerException if map has not been yet initialised.
     */
    public void addMarker(@NonNull DestinationMarker destinationMarker) throws NullPointerException {
        Preconditions.checkNotNull(mMap, "Can't add markers. Map is null.");

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
    public void showRoute(@NonNull LatLng origin, @NonNull LatLng dest, @NonNull DirectionsManager.DirectionModes directionMode) throws NullPointerException {
        Preconditions.checkNotNull(mMap, "Can't show route. Map is null");

        DirectionsManager.getInstance().computeRoute(mMap, origin, dest, directionMode);
    }


}
