package com.gruppo3.wetravel.location.interfaces;

import android.location.Location;

import androidx.annotation.NonNull;

public interface OnLocationAvailableListener {
    void onLocationAvailable(@NonNull Location location);
}
