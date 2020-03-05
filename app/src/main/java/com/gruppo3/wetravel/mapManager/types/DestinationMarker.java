package com.gruppo3.wetravel.mapManager.types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * DestinationMarker object containing the information on a map-displayable marker.
 *
 * @author Giovanni Barca
 */
public class DestinationMarker {
    private LatLng latLng = null;
    private String title = null;
    private float color;
    private Object object = null;

    /**
     * @param latLng Coordinates where to place the marker. Never null.
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title) {
        this.latLng = latLng;
        this.title = title;
    }

    /**
     * @param latLng Coordinates where to place the marker. Never null.
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param color Color of the marker. Should be a float value between 0 and 360, representing points on a color wheel, or can be a BitmapDescriptorFactory predefined color
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, float color) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
    }

    /**
     * @param latLng Coordinates where to place the marker. Never null.
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param o Generic object to be associated with the marker.
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.object = o;
    }

    /**
     * @param latLng Coordinates where to place the marker. Never null.
     * @param title String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param color Color of the marker. Should be a float value between 0 and 360, representing points on a color wheel, or can be a BitmapDescriptorFactory predefined color
     * @param o Generic object to be associated with the marker
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, float color, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
        this.object = o;
    }

    /**
     * Gets a LatLng object indicating coordinates of this marker.
     * @return LatLng object indicating coordinates of this marker. Never null.
     */
    @NonNull
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * Gets the title of this marker.
     * @return Title of this marker. Never null.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Gets the color of this marker.
     * @return Color of this marker.
     *
     * @see <a href="https://developer.android.com/reference/android/graphics/Color">Color</a>
     */
    public float getColor() {
        return color;
    }

    /**
     * Gets the object associated to this marker.
     * @return An object instance associated to this marker. Can return null.
     */
    @Nullable
    public Object getObject() {
        return object;
    }
}
