package com.gruppo3.wetravel;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * This activity needs ACCESS_FINE_LOCATION (or ACCESS_COURSE_LOCATION) permission.<br>
 * Because of using FusedLocationProviderClient, it needs Google Play Services installed too.<br>
 * No checks for this condition are made because are already done by Google Maps fragment.<br>
 * When Google Play Services are ready to work, {@link #onMapReady(GoogleMap) onMapReady} is called and the activity can start doing its job.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Request code for ACCESS_FINE_LOCATION permission.
     */
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private int locationRequestInterval = 10000;
    private int locationRequestFastestInterval = 1000;
    private int zoomLevel = 17;

    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
     * Set interval for active location updates, in milliseconds.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#setInterval(long)">LocationRequest.setInterval</a>
     */
    public void setLocationRequestInterval(int locationRequestInterval) {
        this.locationRequestInterval = locationRequestInterval;
    }

    /**
     * Explicitly set the fastest interval for location updates, in milliseconds.<br>
     * This controls the fastest rate at which your application will receive location updates, which might be faster than setInterval(long) in some situations (for example, if other applications are triggering location updates).
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#setFastestInterval(long)">LocationRequest.setFastestInterval</a>
     */
    public void setLocationRequestFastestInterval(int locationRequestFastestInterval) {
        this.locationRequestFastestInterval = locationRequestFastestInterval;
    }

    /**
     * Set zoom level when centering the camera on current location.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/model/CameraPosition.Builder.html#zoom(float)">CameraPosition.Builder</a>
     */
    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
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
        locationRequest.setInterval(locationRequestInterval); // Maximum waiting time
        locationRequest.setFastestInterval(locationRequestFastestInterval); // Minimum waiting time - if a location is retrieved before then expected
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // This parameter affect battery consumption and location accuracy

        checkPermissions(); // Checks (and eventually asks for) permissions needed by this activity
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
                    .zoom(zoomLevel)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    };
}
