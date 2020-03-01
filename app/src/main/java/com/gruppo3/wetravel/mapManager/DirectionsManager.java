package com.gruppo3.wetravel.mapManager;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gruppo3.wetravel.mapManager.asyncTasks.DownloadTask;
import com.gruppo3.wetravel.mapManager.asyncTasks.ParserTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton class that downloads JSON data from Google Maps Directions Web Service and shows in a map fragment
 *
 * @author Giovanni Barca
 */
public class DirectionsManager {

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
     * Computes and shows on the map the requested route from origin to destination.
     * @param origin Route's origin coordinates
     * @param dest Route's destination coordinates
     */
    public void computeRoute(final GoogleMap googleMap, LatLng origin, LatLng dest, DirectionModes directionMode) {
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
                                 createPolylineFromParsedJSON(googleMap, result);
                        }
                    }).execute(result);
                }
            }
        }).execute(googleDirectionsApiUrl);
    }

    /**
     * Builds an url with parameters for requesting route to Google Maps Directions API from origin to destination.
     * @param origin Route's origin coordinates
     * @param dest Route's destination coordinates
     * @return A string containing the url to the Google Maps Directions API for requesting the route from origin to dest
     */
    private String buildDirectionsUrl(LatLng origin, LatLng dest, DirectionModes directionMode) {
        final String key = "AIzaSyBAqE4yh01-eD6Tv2nQ7lxIMsFik807yIY"; // TODO: This key will be removed once the apk is released and will be used the restriction-less key inserted in a resource file
        final String URL_SPACER = "&";

        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin=").append(origin.latitude).append(",").append(origin.longitude);
        url.append(URL_SPACER);
        url.append("destination=").append(dest.latitude).append(",").append(dest.longitude);
        url.append(URL_SPACER);
        url.append("mode=").append(directionMode);
        url.append(URL_SPACER);
        url.append("sensor=").append("false");
        url.append(URL_SPACER);
        url.append("key=").append(key);

        return url.toString();
    }

    /**
     * Creates a PolyLine from the parsed JSON and shows it to the passed GoogleMap object.
     * @param result Parsed JSON from Google Maps API Web Service
     */
    private void createPolylineFromParsedJSON(GoogleMap googleMap, List<List<HashMap<String, String>>> result) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);
        }

        googleMap.addPolyline(lineOptions);
    }
}
