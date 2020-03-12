package com.gruppo3.wetravel.locationmanager;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.gruppo3.wetravel.locationmanager.interfaces.LocationManagerInterface;
import com.gruppo3.wetravel.locationmanager.interfaces.OnLocationAvailableListener;

/**
 * This class manages location services and sends location updates to a method delegated via {@link #setOnLocationAvailableListener(OnLocationAvailableListener)}.
 * <p>
 * If a null parameter is passed to this class constructor, a default {@link LocationRequest} instance is created which has these values:
 * <ul>
 *     <li>Interval: {@value DEFAULT_LR_INTERVAL} ms</li>
 *     <li>Fastest interval: {@value DEFAULT_LR_FASTEST_INTERVAL} ms</li>
 *     <li>Priority: {@value DEFAULT_LR_PRIORITY}</li>
 * </ul>
 * <p>
 * If a {@link OnLocationAvailableListener} is not set, stops requesting current location to location services.
 *
 * @author Giovanni Barca
 */
public class LocationManager extends LocationCallback implements LocationManagerInterface {

    /**
     * {@link LocationRequest} default interval.
     */
    private static final int DEFAULT_LR_INTERVAL = 10000;

    /**
     * {@link LocationRequest} default fastest interval.
     */
    private static final int DEFAULT_LR_FASTEST_INTERVAL = 1000;

    /**
     * {@link LocationRequest} default priority.
     */
    private static final int DEFAULT_LR_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    @NonNull
    private FusedLocationProviderClient fusedLocationProviderClient;
    @NonNull
    private LocationRequest locationRequest;
    @Nullable
    private OnLocationAvailableListener onLocationAvailableListener;

    /**
     * Creates a new instance of this class and starts {@link android.location.Location} services.
     *
     * @param appContext      {@link Context Application context} from which the class is instantiated. Never null.
     * @param locationRequest {@link LocationRequest} with custom parameters on which location updates will be based on.
     *                        A default {@link LocationRequest} will be used if a null parameter is passed.
     */
    public LocationManager(@NonNull Context appContext, @Nullable LocationRequest locationRequest) {
        if (locationRequest == null)
            this.locationRequest = new LocationRequest()
                    .setInterval(DEFAULT_LR_INTERVAL)
                    .setFastestInterval(DEFAULT_LR_FASTEST_INTERVAL)
                    .setPriority(DEFAULT_LR_PRIORITY);
        else
            this.locationRequest = locationRequest;

        // Gets the Location Provider Client for requesting location updates to.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext);
    }

    /**
     * Sets an {@link OnLocationAvailableListener} delegate method to send {@link android.location.Location} updates to.
     * If a null parameter is passed, stops requesting location updates.
     *
     * @param onLocationAvailableListener Delegate method to send {@link android.location.Location} updates to. Can be null.
     */
    @CallSuper
    @Override
    public void setOnLocationAvailableListener(@Nullable OnLocationAvailableListener onLocationAvailableListener) {
        this.onLocationAvailableListener = onLocationAvailableListener;

        if (this.onLocationAvailableListener == null) // If no one is listening to location updates, then we stop them to preserve battery
            stopLocationUpdates();
        else // otherwise we start them
            startLocationUpdates();
    }

    /**
     * When a {@link android.location.Location} update is received, we send it to the delegated method set via {@link #setOnLocationAvailableListener(OnLocationAvailableListener)}.
     *
     * @param locationResult Received {@link android.location.Location} update. Should be current device location.
     */
    @CallSuper
    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        if (onLocationAvailableListener != null)
            onLocationAvailableListener.onLocationAvailable(locationResult.getLastLocation());
    }

    /**
     * Starts requesting {@link android.location.Location} updates to the {@link FusedLocationProviderClient}.
     */
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper());
    }

    /**
     * Stops requesting {@link android.location.Location} updates to the {@link FusedLocationProviderClient}.
     */
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }
}
