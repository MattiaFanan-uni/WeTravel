package com.gruppo3.wetravel.Activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.gruppo3.wetravel.Types.DestinationMarker;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.Types.DownloadTask;
import com.gruppo3.wetravel.Types.ParserTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This activity needs ACCESS_FINE_LOCATION (or ACCESS_COURSE_LOCATION) permission.<br>
 * Because of using FusedLocationProviderClient, it needs Google Play Services installed too.<br>
 * No checks are made for this condition because they're already done by Google Maps fragment.<br>
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

    private GoogleMap mMap = null; // Main map obj reference
    private FusedLocationProviderClient fusedLocationProviderClient; // Needed for acquiring location updates
    private LocationRequest locationRequest; // Needed for requesting location updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop location updates when this activity is no longer active
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Gets map parameters (if given), initialize the map fragment and the location service.
     */
    private void init() {
        // Getting extras for map configuration
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

    /**
     * Checks if needed permissions by this activity are granted and eventually asks them.
     * If permissions are granted then request location updates and enable MyLocation layer.
     * @see <a href="https://developer.android.com/guide/topics/permissions/overview">Permissions overview</a>
     */
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permissions already granted
                enableMyLocationAndLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
        }
        else {
            enableMyLocationAndLocationUpdates();
        }
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
                        enableMyLocationAndLocationUpdates();
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
        mMap = googleMap;

        checkPermissions(); // Checks (and eventually asks for) permissions needed by this activity

        locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestInterval);
        locationRequest.setFastestInterval(locationRequestFastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // This parameter affects battery consumption and location accuracy

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
        if (mMap != null)
            mMap.setMyLocationEnabled(true);
        else
            throw new RuntimeException("Can't enable MyLocation. Map is set to null.");

        computeRoute(new LatLng(45.603826, 11.995471), new LatLng(45.609519, 12.014606));
    }

    /**
     * Computes and shows on the map the requested route from origin to destination.
     * @param origin Route's origin coordinates
     * @param dest Route's destination coordinates
     */
    private void computeRoute(LatLng origin, LatLng dest) {
        String googleDirectionsApiUrl = buildDirectionsUrl(origin, dest); // Builds the Google Maps Directions API request url

        // Downloads the JSON from Google Maps Directions Web Service and returns the result to asyncResponse(String) callback
        new DownloadTask(new DownloadTask.AsyncResponse() {
            @Override
            public void onFinishResult(String result) {
                Log.e("Attenzione", "DownloadTask()");
                new ParserTask(new ParserTask.AsyncResponse() {
                    @Override
                    public void onFinishResult(List<List<HashMap<String, String>>> result) {
                        Log.e("Attenzione", "ParserTask()");
                        showRouteFromParsedJSON(result);
                    }
                }).execute(result);
            }
        }).execute(googleDirectionsApiUrl);
    }

    private void showRouteFromParsedJSON(List<List<HashMap<String, String>>> result) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

        }

        // Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(lineOptions);
    }

    /**
     * Builds an url with parameters for requesting route to Google Maps Directions API from origin to destination.
     * @param origin Route's origin coordinates
     * @param dest Route's destination coordinates
     * @return A string containing the url to the Google Maps Directions API for requesting the route from origin to dest
     */
    private String buildDirectionsUrl(LatLng origin, LatLng dest) {
        String parameters = "";
        parameters += "origin=" + origin.latitude + "," + origin.longitude; // Route origin
        parameters += "&" + "destination=" + dest.latitude + "," + dest.longitude; // Destination of route
        parameters += "&" + "sensor=false"; // Sensor disabled
        parameters += "&" + "mode=driving"; // Mode driving. Other modes are: walking, bicyling, transit.

        String key = "AIzaSyBAqE4yh01-eD6Tv2nQ7lxIMsFik807yIY"; // TODO: Modify key when apk is released

        // Returning the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + key;
    }

    /**
     * Add a marker on the map with the parameters included in the given DestinationMarker.
     * @param destinationMarker DestinationMarker object containing information about the marker to be added
     * @throws RuntimeException if map has not been yet initialised.
     */
    public void addMarker(DestinationMarker destinationMarker) throws RuntimeException {
        if (mMap != null)
            throw new RuntimeException("Can't add markers. Map is null.");

        mMap.addMarker(new MarkerOptions()
                .position(destinationMarker.getLatLng())
                .title(destinationMarker.getTitle())
                .icon(BitmapDescriptorFactory.defaultMarker(destinationMarker.getColor()))
        ).setTag(destinationMarker.getObject());
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
