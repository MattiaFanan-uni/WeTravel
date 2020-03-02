package com.gruppo3.wetravel.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

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
import com.gruppo3.wetravel.mapManager.types.DestinationMarker;
import com.gruppo3.wetravel.mapManager.DirectionsManager;
import com.gruppo3.wetravel.mapManager.ViewMap;
import com.gruppo3.wetravel.R;

/**
 * This activity shows a map with device current position and markers for available "missions".<br>
 *
 * This activity needs ACCESS_FINE_LOCATION (or ACCESS_COURSE_LOCATION) permission.<br>
 * Because of using FusedLocationProviderClient, it needs Google Play Services installed too.<br>
 * No checks are made for this condition because they're already done by Google Maps fragment.<br>
 * When Google Play Services are ready to work, {@link #onMapReady(GoogleMap) onMapReady} is called and the activity can start doing its job.
 * Can be set a custom location request interval using Intent extras (see Constant Field values)
 *
 * @author Giovanni Barca
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1; // Request code for ACCESS_FINE_LOCATION permission

    private GoogleMap mMap = null; // Main map obj reference
    private ViewMap viewMap = null; // Class with map display and manipulation operations

    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates

    /**
     * Instantiate a new object of type MapActivity with a ViewMap object containing needed methods.<br>
     * If a viewMap is not already registered, then instantiates a new one with default parameters.
     */
    public MapActivity() {
        if (viewMap == null)
            this.viewMap = new ViewMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
     * If permissions are granted then request location updates and enable MyLocation layer.
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
        }
        else {
            enableMyLocationAndLocationUpdates(); // Enabling and displaying current location (blue dot on map)
        }
    }

    /**
     * Method invoked when user choice if grant or not a permission.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
     * them inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Getting the map ready to work (main map on the activity layout)

        checkPermissions(); // Checks (and eventually asks for) permissions needed by this activity

        locationRequest = viewMap.getLocationRequest(); // Gets ViewMap LocationRequest

        // If user moves the map, we stop following current location
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        });

        // If user clicks on a marker, we stop following current location
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                return false;
            }
        });

        // If user clicks on MyLocation button, we resume following current location
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                return false;
            }
        });
    }

    /**
     * Requests location updates and enables MyLocation (blue dot on the map).
     * @throws RuntimeException if no FusedLocationProviderClient is registered nor a map is initialized
     */
    private void enableMyLocationAndLocationUpdates() throws RuntimeException {
        // Enabling location updates
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        else
            throw new RuntimeException("Can't request location updates. FusedLocationProviderClient is set to null.");

        // Enabling "MyLocation" function
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        else
            throw new RuntimeException("Can't enable MyLocation. Map is set to null.");
    }

    /**
     * Register a custom ViewMap object to this activity.
     * @param viewMap Add a viewMap to this activity for UI and location customization
     */
    public void registerViewMap(ViewMap viewMap) {
        this.viewMap = viewMap;
    }

    /**
     * Add a marker on the map with the parameters included in the given DestinationMarker.
     * @param destinationMarker DestinationMarker object containing information about the marker to be added
     * @throws RuntimeException if map has not been yet initialised.
     */
    public void addMarker(@NonNull DestinationMarker destinationMarker) throws RuntimeException {
        if (mMap == null)
            throw new RuntimeException("Can't add markers. Map is null.");

        mMap.addMarker(new MarkerOptions()
                .position(destinationMarker.getLatLng())
                .title(destinationMarker.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(destinationMarker.getColor()))
        ).setTag(destinationMarker.getObject());
    }

    /**
     * Shows the shortest route from origin to dest.
     * @param origin Object of type LatLng referring to the route's origin coordinates
     * @param dest Object of type LatLng referring to the route's destination coordinates
     * @throws RuntimeException if map has not been yet initialised.
     */
    public void showRoute(@NonNull LatLng origin, @NonNull LatLng dest, DirectionsManager.DirectionModes directionMode) throws RuntimeException {
        if (mMap == null)
            throw new RuntimeException("Can't show route. Map is null.");

        DirectionsManager.getInstance().computeRoute(mMap, origin, dest, directionMode);
    }

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


}
