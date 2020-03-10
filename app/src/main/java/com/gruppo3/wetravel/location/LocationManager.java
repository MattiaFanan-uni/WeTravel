package com.gruppo3.wetravel.location;

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
import com.gruppo3.wetravel.location.interfaces.LocationManagerInterface;
import com.gruppo3.wetravel.location.interfaces.OnLocationAvailableListener;

public class LocationManager extends LocationCallback implements LocationManagerInterface {

    @NonNull private FusedLocationProviderClient fusedLocationProviderClient;
    @NonNull private LocationRequest locationRequest;
    @Nullable private OnLocationAvailableListener onLocationAvailableListener;

    public LocationManager(@NonNull Context appContext, @Nullable LocationRequest locationRequest) {
        if (locationRequest == null)
            this.locationRequest = new LocationRequest()
                    .setInterval(10000)
                    .setFastestInterval(1000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        else
            this.locationRequest = locationRequest;

        // Gets the Location Provider Client for requesting location updates to.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext);
    }

    @CallSuper
    @Override
    public void setOnLocationAvailableListener(@Nullable OnLocationAvailableListener onLocationAvailableListener) {
        this.onLocationAvailableListener = onLocationAvailableListener;

        // If no one is listening to location updates, then we stop them to preserve battery
        // otherwise we start them.
        if (this.onLocationAvailableListener == null)
            stopLocationUpdates();
        else
            startLocationUpdates();
    }

    @CallSuper
    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        if (onLocationAvailableListener != null)
            onLocationAvailableListener.onLocationAvailable(locationResult.getLastLocation());
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }
}
