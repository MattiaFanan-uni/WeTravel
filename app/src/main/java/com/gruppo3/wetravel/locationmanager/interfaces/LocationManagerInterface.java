package com.gruppo3.wetravel.locationmanager.interfaces;

import androidx.annotation.Nullable;

/**
 * Interface for alerting a listener when a location update is received.
 *
 * @author Giovanni Barca
 */
public interface LocationManagerInterface {
    void setOnLocationAvailableListener(@Nullable OnLocationAvailableListener onLocationAvailableListener);
}
