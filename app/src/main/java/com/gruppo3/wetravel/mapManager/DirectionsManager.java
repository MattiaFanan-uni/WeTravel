package com.gruppo3.wetravel.mapManager;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gruppo3.wetravel.mapManager.asyncTasks.DownloadTask;
import com.gruppo3.wetravel.mapManager.asyncTasks.ParserTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton class that downloads JSON data from Google Maps Directions Web Service, compute it and shows the corresponding {@link com.google.android.gms.maps.model.Polyline} in a map fragment.
 *
 * @author Giovanni Barca
 */
public class DirectionsManager {
    /**
     * Possible path computing options using Google Maps web service.
     * <li>
     *     <ul>DRIVING: Uses roads suitable to car transit;</ul>
     *     <ul>WALkING: Uses roads and walk paths;</ul>
     *     <ul>BICYCLING: Uses roads, walk paths and cycle paths;</ul>
     *     <ul>TRANSIT: Uses mass transit available in a specific zone combined with walking paths.</ul>
     * </li>
     */
    public enum DirectionModes {
        DRIVING,
        WALKING,
        BICYCLING,
        TRANSIT
    }

    private static final DirectionsManager instance = new DirectionsManager();

    /**
     * Gets the instance of this singleton class.
     * @return Instance of this singleton class
     */
    public static DirectionsManager getInstance() {
        return instance;
    }

    /**
     * Computes and shows on the map the requested route from origin to destination with a 12px wide blue line.
     * @param googleMap Object of type {@link GoogleMap} where the polyline will be displayed. Never null.
     * @param origin Route's origin coordinates. Never null.
     * @param dest Route's destination coordinates. Never null.
     * @param directionMode Specifies in which mode the route has to be calculated (driving, walking, bicycle or transit mode). Never null.
     */
    public void computeRoute(@NonNull final GoogleMap googleMap, @NonNull LatLng origin, @NonNull LatLng dest, @NonNull DirectionModes directionMode) {
        String googleDirectionsApiUrl = buildDirectionsUrl(origin, dest, directionMode); // Builds the Google Maps Directions API request url

        // Downloads the JSON from Google Maps Directions Web Service, parses it and sends to showRoutFromParsedJSON(String) if there were no errors
        new DownloadTask(new DownloadTask.AsyncResponse() {
            @Override
            public void onFinishResult(String result) {
                // Continue if download successfully terminated
                if (!result.equals(DownloadTask.ERROR_MESSAGE)) {
                    new ParserTask(new ParserTask.AsyncResponse() {
                        @Override
                        public void onFinishResult(List<List<HashMap<String, String>>> result) {
                            if (result.size() > 0) // If there were errors, list size is 0
                                createPolylineFromParsedJSON(googleMap, result, 12, Color.BLUE);
                        }
                    }).execute(result);
                }
            }
        }).execute(googleDirectionsApiUrl);
    }

    /**
     * Computes and shows on the map the requested route from origin to destination with argument width and color.
     * @param googleMap Object of type {@link GoogleMap} where the polyline will be displayed. Never null.
     * @param origin Route's origin coordinates. Never null.
     * @param dest Route's destination coordinates. Never null.
     * @param directionMode Specifies in which mode the route has to be calculated (driving, walking, bicycle or transit mode). Never null.
     * @param lineWidth Width of the PolyLine that will be displayed on the map.
     * @param lineColor Color of the PolyLine that will be displayed on the map. {@link Color} class can be used (e.g. Color.BLUE)
     */
    public void computeRoute(@NonNull final GoogleMap googleMap, @NonNull LatLng origin, @NonNull LatLng dest, @NonNull DirectionModes directionMode, final int lineWidth, final int lineColor) {
        String googleDirectionsApiUrl = buildDirectionsUrl(origin, dest, directionMode); // Builds the Google Maps Directions API request url

        // Downloads the JSON from Google Maps Directions Web Service, parses it and sends to showRoutFromPrasedJSON(String) if there were no errors
        new DownloadTask(new DownloadTask.AsyncResponse() {
            @Override
            public void onFinishResult(String result) {
                // Continue if download successfully terminated
                if (!result.equals(DownloadTask.ERROR_MESSAGE)) {
                    new ParserTask(new ParserTask.AsyncResponse() {
                        @Override
                        public void onFinishResult(List<List<HashMap<String, String>>> result) {
                            if (result.size() > 0) // If there were errors, list size is 0
                                 createPolylineFromParsedJSON(googleMap, result, lineWidth, lineColor);
                        }
                    }).execute(result);
                }
            }
        }).execute(googleDirectionsApiUrl);
    }

    /**
     * Builds an url with parameters for requesting route to Google Maps Directions API from origin to destination.
     * @param origin Route's origin coordinates. Never null.
     * @param dest Route's destination coordinates. Never null.
     * @param directionMode Specifies in which mode the route has to be calculated (driving, walking, bicycle or transit mode). Never null.
     * @return A string containing the url to the Google Maps Directions API for requesting the route from origin to dest.
     */
    private String buildDirectionsUrl(@NonNull LatLng origin, @NonNull LatLng dest, @NonNull DirectionModes directionMode) {
        final String DEV_KEY = "AIzaSyBAqE4yh01-eD6Tv2nQ7lxIMsFik807yIY"; // TODO: This key will be removed once the apk is released and will be used the restriction-less key inserted in a resource file
        final String URL_SPACER = "&";
        final String COMMA_SEPARATOR = ",";

        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin=").append(origin.latitude).append(COMMA_SEPARATOR).append(origin.longitude);
        url.append(URL_SPACER);
        url.append("destination=").append(dest.latitude).append(COMMA_SEPARATOR).append(dest.longitude);
        url.append(URL_SPACER);
        url.append("mode=").append(directionMode);
        url.append(URL_SPACER);
        url.append("key=").append(DEV_KEY);

        return url.toString();
    }

    /**
     * Creates a PolyLine from the parsed JSON and shows it to the passed GoogleMap object.
     * @param googleMap Object of type {@link GoogleMap} where the polyline will be displayed. Never null.
     * @param result Parsed JSON from Google Maps API Web Service. Never null.
     * @param lineWidth Width of the PolyLine that will be displayed on the map.
     * @param lineColor Color of the PolyLine that will be displayed on the map. {@link Color} class can be used (e.g. Color.BLUE)
     */
    private void createPolylineFromParsedJSON(@NonNull GoogleMap googleMap, @NonNull List< List<HashMap<String, String>>> result, int lineWidth, int lineColor) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat")); // Ignore IDE warning. In our code, "lat" argument will be never null.
                double lng = Double.parseDouble(point.get("lng")); // Ignore IDE warning. In our code, "lng" argument will be never null.
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(lineWidth);
            lineOptions.color(lineColor);
            lineOptions.geodesic(true); // @see <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/model/Polyline">Geodesic status</a>
        }

        googleMap.addPolyline(lineOptions);
    }
}
