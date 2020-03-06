package com.gruppo3.wetravel.mapmanager.types;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

/**
 * Class containing main parameters for building a ViewMap for working with {@link com.gruppo3.wetravel.activities.MapActivity} map fragment.<br>
 * <p>
 * If a default {@link LocationRequest} is used, the default parameters are:
 * <ul>
 * <li>locationRequestInterval is set to {@value DEFAULT_INTERVAL} that is the right compromise between precision and battery saving;</li>
 * <li>locationRequestFastestInterval is set to {@value DEFAULT_FASTEST_INTERVAL} to avoid annoying fast movements of the map;</li>
 * <li>locationRequest priority is set to {@value DEFAULT_PRIORITY}.</li>
 * </ul>
 * <p>
 * The default zoom level is {@value DEFAULT_MAP_ZOOM}.
 *
 * @author Giovanni Barca
 */
public class ViewMap {
    private final static int DEFAULT_MAP_ZOOM = 17;
    private final static int DEFAULT_INTERVAL = 10000;
    private final static int DEFAULT_FASTEST_INTERVAL = 1000;
    private final static int DEFAULT_PRIORITY = PRIORITY_BALANCED_POWER_ACCURACY;

    @NonNull
    private LocationRequest locationRequest; // Needed for requesting location updates
    private int mapZoom;

    /**
     * Creates a new instance of {@link ViewMap} with default parameters.
     */
    public ViewMap() {
        this.mapZoom = DEFAULT_MAP_ZOOM;
        this.locationRequest = new LocationRequest();
        this.locationRequest
                .setInterval(DEFAULT_INTERVAL)
                .setFastestInterval(DEFAULT_FASTEST_INTERVAL)
                .setPriority(DEFAULT_PRIORITY);
    }

    /**
     * Creates a new instance of {@link ViewMap} with given {@link LocationRequest} and map zoom level parameters.
     *
     * @param locationRequest Object of type {@link LocationRequest} with custom request options.
     * @param mapZoom         Map zoom level.
     */
    public ViewMap(@NonNull LocationRequest locationRequest, int mapZoom) {
        this.locationRequest = locationRequest;
        this.mapZoom = mapZoom;
    }

    /**
     * Creates a new instance of ViewMap with given {@link LocationRequest} parameter and default zoom level.
     *
     * @param locationRequest Object of type {@link LocationRequest} with custom parameters.
     */
    public ViewMap(@NonNull LocationRequest locationRequest) {
        this();
        this.locationRequest = locationRequest;
    }

    /**
     * Creates a new instance of ViewMap with given zoom level and a default {@link LocationRequest} instance.
     *
     * @param mapZoom Map zoom level.
     */
    public ViewMap(int mapZoom) {
        this();
        this.mapZoom = mapZoom;
    }

    /**
     * Returns the current {@link ViewMap}'s assigned {@link LocationRequest}.
     *
     * @return Object of type {@link LocationRequest} currently associated with this {@link ViewMap}. Never null.
     */
    @NonNull
    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    /**
     * Returns the current ViewMap assigned zoom level.
     *
     * @return Integer of the default zoom level currently associated with this ViewMap.
     */
    public int getMapZoom() {
        return mapZoom;
    }
}
