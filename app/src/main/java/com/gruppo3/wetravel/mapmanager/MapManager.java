package com.gruppo3.wetravel.mapmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;

import androidx.annotation.NonNull;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.gruppo3.wetravel.activities.AddMarkerActivity;
import com.gruppo3.wetravel.activities.MarkerDetailsActivity;
import com.gruppo3.wetravel.locationmanager.LocationManager;
import com.gruppo3.wetravel.locationmanager.interfaces.OnLocationAvailableListener;
import com.gruppo3.wetravel.mapmanager.interfaces.MapManagerCallbacks;
import com.gruppo3.wetravel.missionmanager.MissionManager;
import com.gruppo3.wetravel.types.DestinationMarker;
import com.gruppo3.wetravel.util.Const;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * This class manages map UI with relative listeners:
 * <ul>
 *     <li>OnCameraMoveStartedListener stops automatic map camera centering on current location if the user moved the map;</li>
 *     <li>OnMyLocationButtonClickListener resume automatic map camera centering on current location;</li>
 *     <li>OnMarkerClickListener stops automatic map camera centering on current location if the user clicks on a marker. In advance it'll show the fastest route from current location to the clicked marker;</li>
 *     <li>OnMapLongClickListener opens a new {@link AddMarkerActivity} to let the user request a new mission to the network;</li>
 *     <li>OnInfoWindowClickListener opens a new {@link MarkerDetailsActivity} to display selected mission details and let the user accept or decline it;</li>
 *     <li>OnInfoWindowLongClickListener deletes the selected mission (only if it's owned by the user).</li>
 * </ul>
 * <p>
 * This class uses {@link LocationManager} to get current location, which is received by {@link #onLocationAvailable(Location)}.
 * Note that every marker on the map corresponds to an available mission.
 *
 * @author Giovanni Barca
 */
public class MapManager implements MapManagerCallbacks {
    /**
     * {@link GoogleMap} zoom level used by default.
     */
    private static final int DEFAULT_MAP_ZOOM_LEVEL = 17;

    /**
     * Default color when a {@link DestinationMarker} is just inserted and not yet confirmed.
     */
    private static final float DEFAULT_TEMP_MARKER = BitmapDescriptorFactory.HUE_YELLOW;

    @NonNull
    private Activity activity;
    @NonNull
    private LocationManager locationManager;
    private MissionManager missionManager;
    private GoogleMap mMap;
    private Location currentLocation;
    private Polyline lastRoute;
    private boolean updateCamera;

    /**
     * When a new instance of this class is created, gets the {@link GoogleMap} associated to the {@link SupportMapFragment} argument
     * and creates a new {@link LocationManager} instance.
     *
     * @param activity    Activity in which the {@link GoogleMap} view is created. Never null.
     * @param mapFragment {@link SupportMapFragment} associated to the {@link GoogleMap} that will implement this class. Never null.
     */
    public MapManager(@NonNull Activity activity, @NonNull SupportMapFragment mapFragment) {
        this.activity = activity;
        this.locationManager = new LocationManager(activity.getApplicationContext(), null);
        this.missionManager = new MissionManager(this, activity);

        mapFragment.getMapAsync(this);
    }

    /**
     * This callback is triggered when the given {@link GoogleMap} parameter is ready to be used.
     * This method will only be triggered once the user has installed Google Play services and returned to the app.
     *
     * @param googleMap A {@link GoogleMap} instance. Never null.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true); // Enables MyLocation map layer (blue dot on the map representing current location)

        mMap.setOnCameraMoveStartedListener(this); // Called when camera is moved
        mMap.setOnMyLocationButtonClickListener(this); // Called when MyLocation button is clicked
        mMap.setOnMarkerClickListener(this); // Called when a marker is clicked
        mMap.setOnMapLongClickListener(this); // Called when a long click on the map is made
        mMap.setOnInfoWindowClickListener(this); // Called when the info windows is clicked
        mMap.setOnInfoWindowLongClickListener(this); // Called when a long click on an info window is made

        this.updateCamera = true;
    }

    /**
     * This listener is called when an {@link Activity} is closed.
     */
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (Const.ADD_MARKER_ACTIVITY):
                if (resultCode == RESULT_OK && data != null) {
                    double latitude = Double.parseDouble(data.getStringExtra(Const.EXTRA_LATITUDE));
                    double longitude = Double.parseDouble(data.getStringExtra(Const.EXTRA_LONGITUDE));
                    String title = data.getStringExtra(Const.EXTRA_TITLE);
                    String details = data.getStringExtra(Const.EXTRA_DETAILS);
                    DestinationMarker missionMarker = new DestinationMarker(new LatLng(latitude, longitude), title, BitmapDescriptorFactory.HUE_BLUE, details);

                    missionManager.addMission(missionMarker);
                }
            break;
        }

    }

    /**
     * Starts receiving location updates calling {@link LocationManager#setOnLocationAvailableListener(OnLocationAvailableListener)}.
     */
    public void startLocationUpdates() {
        this.locationManager.setOnLocationAvailableListener(this);
    }

    /**
     * Stops receiving location updates passing a null parameter to {@link LocationManager#setOnLocationAvailableListener(OnLocationAvailableListener)}.
     */
    public void stopLocationUpdates() {
        this.locationManager.setOnLocationAvailableListener(null);
    }

    /**
     * This listener is called when a new {@link Location} from the instance of {@link LocationManager} is retrieved.
     * If {@link #updateCamera} is enabled, centers map's {@link CameraPosition} on current location.
     *
     * @param location Last retrieved {@link Location} from the instance of {@link LocationManager}. Never null.
     */
    @Override
    public void onLocationAvailable(@NonNull Location location) {
        this.currentLocation = location;
        if (updateCamera)
            animateCamera(location);
    }

    /**
     * This listener is called when the map's {@link CameraPosition} starts moving.
     * To improve UI experience, stops following current {@link Location}.
     *
     * @param reason The reason for the {@link CameraPosition} change. Possible values:
     *               <ul>
     *                  <li>{@link GoogleMap.OnCameraMoveStartedListener#REASON_GESTURE}: User gestures on the map.</li>
     *                  <li>{@link GoogleMap.OnCameraMoveStartedListener#REASON_API_ANIMATION}: Default animations resulting from user interaction.</li>
     *                  <li>{@link GoogleMap.OnCameraMoveStartedListener#REASON_DEVELOPER_ANIMATION}: Developer animations.</li>
     *               </ul>
     */
    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
            updateCamera = false;
    }

    /**
     * This listener is called when the MyLocation button (the one which centers the {@link CameraPosition} on current {@link Location}).
     * Resumes following current {@link Location}.
     *
     * @return true if this method implementation has consumed the event, false to execute the default behaviour.
     */
    @Override
    public boolean onMyLocationButtonClick() {
        updateCamera = true;

        return false;
    }

    /**
     * This listener is called when a long press is made on the {@link GoogleMap map}.
     * Adds a {@value DEFAULT_TEMP_MARKER} {@link DestinationMarker} on the clicked spot and starts {@link AddMarkerActivity}.
     *
     * @param latLng Coordinates of clicked spot on the {@link GoogleMap}. Never null.
     */
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        // Opening AddMarkerActivity to insert mission details
        Intent startAddMarkerActivity = new Intent(activity.getApplicationContext(), AddMarkerActivity.class);
        startAddMarkerActivity.putExtra(Const.EXTRA_LATITUDE, latLng.latitude);
        startAddMarkerActivity.putExtra(Const.EXTRA_LONGITUDE, latLng.longitude);
        activity.startActivityForResult(startAddMarkerActivity, Const.ADD_MARKER_ACTIVITY);
    }

    /**
     * This listener is called when a {@link Marker} is clicked.
     * Removes the last shown route {@link Polyline} (in this way there should always be one route displayed at most)
     * and compute and displays the fastest route from current {@link Location} to the selected {@link Marker}.
     *
     * @param marker Clicked marker. Never null.
     * @return true if this method implementation has consumed the event, false to execute the default behaviour.
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        updateCamera = false;

        if (lastRoute != null)
            lastRoute.remove();

        if (currentLocation != null) {
            activity.runOnUiThread(() -> showRoute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker, TransportMode.DRIVING));
        }

        return false;
    }

    /**
     * This listener is called when a {@link Marker} info window is clicked.
     * Starts an {@link MarkerDetailsActivity} displaying mission details.
     *
     * @param marker {@link Marker} on which the info window was clicked. Never null.
     */
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        activity.startActivity(new Intent(activity.getApplicationContext(), MarkerDetailsActivity.class));
    }

    /**
     * This listener is called when a {@link Marker} info window is long clicked.
     * Displays a dialog to confirm the delete of the clicked marker and deletes it.
     * It also deletes the last shown route {@link Polyline} that should be to the clicked marker.
     *
     * @param marker {@link Marker} on which the info window was long clicked. Never null.
     */
    @Override
    public void onInfoWindowLongClick(@NonNull Marker marker) {
        if (lastRoute != null)
            lastRoute.remove();
        createDialogToCancelMarker(marker);
    }

    /**
     * Creates a dialog to confirm or decline the deletion of a marker.
     *
     * @param marker {@link Marker} that the user wants to delete.
     */
    private void createDialogToCancelMarker(@NonNull Marker marker) {
        new AlertDialog.Builder(activity)
                .setTitle("Do you want to remove the marker?")
                .setPositiveButton("YES", (dialog, id) -> {
                    marker.remove();
                })
                .setNegativeButton("NO", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    /**
     * If {@link #updateCamera} is true, this method is called and the map moved to have always the current location (blue dot) at center of the screen.
     */
    private void animateCamera(@NonNull Location location) {
        if (mMap != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .zoom(DEFAULT_MAP_ZOOM_LEVEL)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
    private void showRoute(@NonNull LatLng origin, @NonNull Marker dest, @NonNull String directionMode) throws NullPointerException {
        if (mMap != null) {
            GoogleDirection.withServerKey("AIzaSyBAqE4yh01-eD6Tv2nQ7lxIMsFik807yIY") // TODO: Change API key on release
                    .from(origin)
                    .to(dest.getPosition())
                    .transportMode(directionMode)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction) {
                            if (direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                ArrayList<LatLng> directionPoint = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                activity.runOnUiThread(() -> {
                                    LatLng southwestCoordination = route.getBound().getSouthwestCoordination().getCoordination();
                                    LatLng northeastCoordination = route.getBound().getNortheastCoordination().getCoordination();
                                    LatLngBounds bounds = new LatLngBounds(southwestCoordination, northeastCoordination);
                                    Polyline polyline = mMap.addPolyline(DirectionConverter.createPolyline(activity.getApplicationContext(), directionPoint, 5, Color.BLUE));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                                    lastRoute = polyline;
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
}
