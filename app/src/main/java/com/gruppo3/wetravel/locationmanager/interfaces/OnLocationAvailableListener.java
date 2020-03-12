package com.gruppo3.wetravel.locationmanager.interfaces;

import android.location.Location;

import androidx.annotation.NonNull;

/**
 * Interface for alerting a listener when a location update is received.
 *
 * @author Giovanni Barca
 */
public interface OnLocationAvailableListener {
    void onLocationAvailable(@NonNull Location location);
}
