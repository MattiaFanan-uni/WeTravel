package com.gruppo3.wetravel;

import android.location.Location;

import com.eis.smslibrary.SMSPeer;
import com.google.android.gms.maps.model.LatLng;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Partake {
    public abstract SMSPeer getOwner();

    public abstract LatLng getPosition();

    public static Partake create(SMSPeer owner, LatLng position) {
        return new AutoValue_Partake(owner, position);
    }

    public static Partake create(String owner, LatLng position) {
        return new AutoValue_Partake(new SMSPeer(owner), position);
    }

    public double meterDistance(LatLng position) {

        float[] results = new float[1];
        //if result.length =1 distanceBetween return the distance
        //if >=3 return also the shortest path between in [1] and [2]
        Location.distanceBetween(getPosition().latitude, getPosition().longitude,
                position.latitude, position.longitude, results);
        return results[0];
    }

    public double meterDistance(Partake partake) {
        return meterDistance(partake.getPosition());
    }
}
