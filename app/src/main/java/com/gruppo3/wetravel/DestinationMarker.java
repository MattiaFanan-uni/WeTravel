package com.gruppo3.wetravel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class DestinationMarker {
    private LatLng latLng = null;
    private String title = null;
    private float color;
    private Object object = null;

    /**
     * @param latLng Coordinates where to place the marker
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker)
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title) {
        this.latLng = latLng;
        this.title = title;
    }

    /**
     * @param latLng Coordinates where to place the marker
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker)
     * @param color Color of the marker. Should be a float value between 0 and 360, representing points on a color wheel, or can be a BitmapDescriptorFactory predefined color
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, float color) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
    }

    /**
     * @param latLng Coordinates where to place the marker
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker)
     * @param o Generic object to be associated with the marker
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.object = o;
    }

    /**
     * @param latLng Coordinates where to place the marker
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker)
     * @param color Color of the marker. Should be a float value between 0 and 360, representing points on a color wheel, or can be a BitmapDescriptorFactory predefined color
     * @param o Generic object to be associated with the marker
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, float color, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
        this.object = o;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public float getColor() {
        return color;
    }

    public Object getObject() {
        return object;
    }
}
