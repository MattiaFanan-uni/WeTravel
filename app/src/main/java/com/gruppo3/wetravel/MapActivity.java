package com.gruppo3.wetravel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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

/**
 * This activity needs ACCESS_FINE_LOCATION (or ACCESS_COURSE_LOCATION) permission.<br>
 * Because of using FusedLocationProviderClient, it needs Google Play Services installed too.<br>
 * No checks for this condition are made because are already done by Google Maps fragment.<br>
 * When Google Play Services are ready to work, {@link #onMapReady(GoogleMap) onMapReady} is called and the activity can start doing its job.
 * Can be set a custom location request interval using Intent extras (see Constant Field values)
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Tag for sending map zoom level via Intent extras.
     */
    public static final String MAP_ZOOM_TAG = "mapZoom";

    /**
     * Tag for sending location request interval via Intent extras.
     */
    public static final String LOCATION_REQUEST_INTERVAL_TAG = "lrInterval";

    /**
     * Tag for sending location request fastest interval via Intent extras.
     */
    public static final String LOCATION_REQUEST_FASTEST_INTERVAL_TAG = "lrFastestInterval";

    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1; // Request code for ACCESS_FINE_LOCATION permission

    private int locationRequestInterval = 10000; // How much time (in ms) passes between two location requests. This parameter affects device battery consumption
    private int locationRequestFastestInterval = 1000; // If a location is available sooner than locationRequestInterval, than this is the minimum rate this app will acquire this location update
    private int mapZoom = 17; // Map zoom level. 17 is about the same zoom level used by Google Maps

    private boolean mapReady = false; // Boolean value that indicates whether or not the map is ready to work

    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Map zoom level
            // See also: https://developers.google.com/android/reference/com/google/android/gms/maps/model/CameraPosition.Builder.html#zoom(float)
            if (extras.containsKey(MAP_ZOOM_TAG))
                mapZoom = extras.getInt(MAP_ZOOM_TAG);

            // locationRequestInterval
            // See also: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#setInterval(long)
            if (extras.containsKey(LOCATION_REQUEST_INTERVAL_TAG))
                locationRequestInterval = extras.getInt(LOCATION_REQUEST_INTERVAL_TAG);

            // locationRequestFastestInterval
            // See also: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#setFastestInterval(long)
            if (extras.containsKey(LOCATION_REQUEST_FASTEST_INTERVAL_TAG))
                locationRequestFastestInterval = extras.getInt(LOCATION_REQUEST_FASTEST_INTERVAL_TAG);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop location updates when this activity is no longer active
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestInterval);
        locationRequest.setFastestInterval(locationRequestFastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // This parameter affects battery consumption and location accuracy

        checkPermissions(); // Checks (and eventually asks for) permissions needed by this activity

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

        mapReady = true;

        loadDestinationsOnMap();
    }

    /**
     * Checks if main map on this activity is ready to accomplish operations.
     * @return Boolean value indicating if the map is ready to accomplish operations or not
     */
    public boolean isMapReady() {
        return mapReady;
    }

    private void loadDestinationsOnMap() {
        // TODO: get destinations with kademlia
        DestinationMarker[] destinationMarkers = new DestinationMarker[] {
                new DestinationMarker(new LatLng(45.603450,11.995468), "Ritiro pacco Amazon", BitmapDescriptorFactory.HUE_AZURE),
                new DestinationMarker(new LatLng(45.606016, 11.995734), "Ritiro posta")
        };

        for (DestinationMarker destinationMarker : destinationMarkers) {
            addMarker(destinationMarker);
        }
    }

    /**
     * Add a marker on the map.
     * @param destinationMarker Object of type DestinationMarker containing information about the marker to be added
     * @throws RuntimeException if map has not been yet initialised.
     */
    public void addMarker(DestinationMarker destinationMarker) throws RuntimeException {
        if (!mapReady)
            throw new RuntimeException("Map is not ready to work!");

        mMap.addMarker(new MarkerOptions()
                .position(destinationMarker.getLatLng())
                .title(destinationMarker.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(destinationMarker.getColor()))
        ).setTag(destinationMarker.getObject());
    }

    /**
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Checking again permission to avoid runtime exceptions
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Enabling "MyLocation" function and location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied
                    // Functionalities depending by this permission will no longer work
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Checks if needed permissions by this activity are granted and eventually asks them.
     * If permissions are granted then request location updates and enable MyLocation layer.
     * If permissions are denied then disables these functionalities.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Location permission already granted
                // Enabling "MyLocation" function and location updates
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                // Request Location permission
                requestPermission();
            }
        }
        else {
            // Enabling "MyLocation" function and location updates
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Asks for permissions needed by this activity.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * This callback is triggered when a new location update is received.<br>
     * It centers the camera to the last location received with an app-defined zoom constant.
     */
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .zoom(mapZoom)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    };
}
