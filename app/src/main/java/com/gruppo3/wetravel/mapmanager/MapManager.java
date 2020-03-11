package com.gruppo3.wetravel.mapmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.activities.AddMarkerActivity;
import com.gruppo3.wetravel.activities.MapActivity;
import com.gruppo3.wetravel.activities.MarkerDetailsActivity;
import com.gruppo3.wetravel.location.LocationManager;
import com.gruppo3.wetravel.mapmanager.interfaces.MapManagerCallbacks;
import com.gruppo3.wetravel.mapmanager.types.DestinationMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.core.content.ContextCompat.getSystemServiceName;
import static androidx.core.content.ContextCompat.startActivity;

public class MapManager implements MapManagerCallbacks {
    private final int DEFAULT_MAP_ZOOM = 17;
    public boolean updateCamera = true;
    @NonNull
    private Activity activity;
    @NonNull
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Location currentLocation;
    private Polyline lastRoute;

    public MapManager(@NonNull Activity activity) {
        this.activity = activity;
        this.locationManager = new LocationManager(activity.getApplicationContext(), null);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraMoveStartedListener(this); // Called when camera is moved
        mMap.setOnMyLocationButtonClickListener(this); // Called when MyLocation button is clicked
        mMap.setOnMarkerClickListener(this); // Called when a marker is clicked
        mMap.setOnMapLongClickListener(this); // Called when a long click on the map is made
        mMap.setOnInfoWindowClickListener(this); // Called when the info windows is clicked
        mMap.setOnInfoWindowLongClickListener(this); // Called when a long click on an info window is made
    }

    @Override
    public void startLocationUpdates() {
        this.locationManager.setOnLocationAvailableListener(this);
    }

    @Override
    public void stopLocationUpdates() {
        this.locationManager.setOnLocationAvailableListener(null);
    }

    @Override
    public void onLocationAvailable(@NonNull Location location) {
        this.currentLocation = location;
        if (updateCamera)
            animateCamera(location);
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
            updateCamera = false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        updateCamera = true;

        return false;
    }

    /**
     * When a spot on the map is long pressed it is added a marker on that spot and it is started AddMarkerActivity
     *
     * @param latLng
     */
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        DestinationMarker marker = new DestinationMarker(latLng, activity.getString(R.string.new_mission), BitmapDescriptorFactory.HUE_YELLOW);
        addMarker(marker);
        Intent startAddMarkerActivity = new Intent(activity.getApplicationContext(), AddMarkerActivity.class);
        startAddMarkerActivity.putExtra("latitude", latLng.latitude);
        startAddMarkerActivity.putExtra("longitude", latLng.longitude);
        activity.startActivity(startAddMarkerActivity);
    }

    /**
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (lastRoute != null)
            lastRoute.remove();

        updateCamera = false;
        if (currentLocation != null) {
            activity.runOnUiThread(() -> showRoute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), marker, TransportMode.DRIVING));
        }

        return false;
    }

    /**
     * When the info window is clicked it is started MarkerDetailsActivity which shows the details of that marker
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        activity.startActivity(new Intent(activity.getApplicationContext(), MarkerDetailsActivity.class));
    }

    /**
     * When the info window above a marker is long pressed the marker it is called the dialog to delete the marker
     *
     * @param marker
     */
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        if (lastRoute != null)
            lastRoute.remove();
        createDialogToCancelMarker(marker);
    }

    /**
     * It creates a dialog to cancel the marker
     *
     * @param marker
     */
    public void createDialogToCancelMarker(Marker marker) {
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
                    .zoom(DEFAULT_MAP_ZOOM)
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
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
