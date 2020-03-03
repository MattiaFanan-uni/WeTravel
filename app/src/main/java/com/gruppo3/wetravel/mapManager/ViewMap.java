package com.gruppo3.wetravel.mapManager;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

/**
 * Class containing main parameters for building a ViewMap for working with MapActivity's map fragment.
 *
 * @author Giovanni Barca
 */
public class ViewMap {
    @NonNull
    private LocationRequest locationRequest; // Needed for requesting location updates
    private int mapZoom;

    /**
     * Creates a new instance of ViewMap with default parameters:<br>
     * locationRequestInterval is set to 10000ms that is the right compromise between precision and battery saving;<br>
     * locationRequestFastestInterval is set to 1000ms to avoid annoying movements of the map;<br>
     * locationRequest priority is set to PRIORITY_BALANCED_POWER_ACCURACY;<br>
     * mapZoom is set to 17 that is about the same zoom level used by default by Google Maps to which the user is used to.
     */
    public ViewMap() {
        this.mapZoom = 17;

        this.locationRequest = new LocationRequest();
        this.locationRequest
                .setInterval(10000)
                .setFastestInterval(1000)
                .setPriority(PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Creates a new instance of ViewMap with given parameters.
     * @param locationRequest Object of type LocationRequest with custom request options
     * @param mapZoom Map zoom level
     */
    public ViewMap(LocationRequest locationRequest, int mapZoom) {
        this.locationRequest = locationRequest;
        this.mapZoom = mapZoom;
    }

    /**
     * Creates a new instance of ViewMap with given LocationRequest parameter and default zoom level (that is set to 17).
     * @param locationRequest Object of type LocationRequest with custom request options
     */
    public ViewMap(LocationRequest locationRequest) {
        this();
        this.locationRequest = locationRequest;
    }

    /**
     * Creates a new instance of ViewMap with given zoom level and default location request options.
     * @param mapZoom Map zoom level
     */
    public ViewMap(int mapZoom) {
        this();
        this.mapZoom = mapZoom;
    }

    /**
     * Returns the current ViewMap assigned LocationRequest.
     * @return Object of type LocationRequest currently associated with this ViewMap
     */
    @NonNull
    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    /**
     * Returns the current ViewMap assigned zoom level.
     * @return Integer of the default zoom level currently associated with this ViewMap
     */
    public int getMapZoom() {
        return mapZoom;
    }
}
