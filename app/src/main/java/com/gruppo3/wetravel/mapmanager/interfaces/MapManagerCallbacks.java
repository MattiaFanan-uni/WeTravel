package com.gruppo3.wetravel.mapmanager.interfaces;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.gruppo3.wetravel.location.interfaces.OnLocationAvailableListener;

public interface MapManagerCallbacks extends
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        OnLocationAvailableListener
{
    void startLocationUpdates();
    void stopLocationUpdates();
}
