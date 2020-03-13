package com.gruppo3.wetravel.types;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * DestinationMarker object containing the information on a map-displayable marker.
 * <p>
 * For marker colors see <a href="https://developer.android.com/reference/android/graphics/Color">Color</a>
 *
 * @author Giovanni Barca
 */
public class DestinationMarker {
    @NonNull
    private LatLng latLng;
    @NonNull
    private String title;
    @Nullable
    private Object object = null;
    @FloatRange(from = 0, to = 360, toInclusive = false)
    private float color;
    private int index = -1;

    public boolean owned;

    /**
     * @param latLng {@link LatLng} object containing coordinates where to place the marker. Never null.
     * @param title  String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title) {
        this.latLng = latLng;
        this.title = title;
    }

    /**
     * @param latLng {@link LatLng} object containing coordinates where to place the marker. Never null.
     * @param title  String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param color  Color of the marker. Should be a float value greater or equal to 0 and less than360.
     *               Can be a {@link com.google.android.gms.maps.model.BitmapDescriptorFactory} predefined color.
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, @FloatRange(from = 0, to = 360, toInclusive = false) float color) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
    }

    /**
     * @param latLng {@link LatLng} object containing coordinates where to place the marker. Never null.
     * @param title  String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param o      Generic object to be associated with the marker. Can be null (in this case this constructor acts in the same way as {@link #DestinationMarker(LatLng, String)}).
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.object = o;
    }

    /**
     * @param latLng {@link LatLng} object containing coordinates where to place the marker. Never null.
     * @param title  String to show when user clicks on the marker (it's showed in a popup over the marker). Never null.
     * @param color  Color of the marker. Should be a float value greater or equal to 0 and less than360.
     *               Can be a {@link com.google.android.gms.maps.model.BitmapDescriptorFactory} predefined color.
     * @param o      Generic object to be associated with the marker. Can be null (in this case this constructor acts in the same way as {@link #DestinationMarker(LatLng, String, float)}.
     */
    public DestinationMarker(@NonNull LatLng latLng, @NonNull String title, @FloatRange(from = 0, to = 360, toInclusive = false) float color, @Nullable Object o) {
        this.latLng = latLng;
        this.title = title;
        this.color = color;
        this.object = o;
    }

    /**
     * Sets an index for this marker.
     *
     *  @param index Integer value representing an index. Should be unique.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets a {@link LatLng} object indicating coordinates of this marker.
     *
     * @return {@link LatLng} object indicating coordinates of this marker. Never null.
     */
    @NonNull
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * Gets the title of this marker.
     *
     * @return Title of this marker. Never null.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Gets the color of this marker.
     *
     * @return Color of this marker. The returned values is greater or equal to 0 and less than 360.
     */
    @FloatRange(from = 0, to = 360, toInclusive = false)
    public float getColor() {
        return color;
    }

    /**
     * Gets the object associated to this marker.
     *
     * @return An object instance associated to this marker. Can return null.
     */
    @Nullable
    public Object getObject() {
        return object;
    }

    /**
     * Gets the index associated to this marker.
     *
     * @return An integer value representing the index associated to this marker. -1 if no index is set.
     */
    public int getIndex() {
        return index;
    }
}
