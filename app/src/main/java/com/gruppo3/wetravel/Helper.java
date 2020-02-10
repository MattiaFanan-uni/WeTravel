package com.gruppo3.wetravel;

import com.google.android.gms.maps.model.LatLng;

public class Helper {

    public static String convertLatLngToString(LatLng latLng) {
        return latLng.latitude + "#" + latLng.longitude;
    }

    public static LatLng convertStringToLatLng(String string) {
        String[] splitted = string.split("#");
        return new LatLng(Double.parseDouble(splitted[0]), Double.parseDouble(splitted[1]));
    }

    public static double meterDistance (LatLng a, LatLng b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(b.latitude-a.latitude);
        double lngDiff = Math.toRadians(b.longitude-a.longitude);
        double e = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(e), Math.sqrt(1-e));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }
}
