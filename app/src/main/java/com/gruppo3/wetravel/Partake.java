package com.gruppo3.wetravel;

import android.location.Location;

import com.eis.smslibrary.SMSPeer;
import com.google.android.gms.maps.model.LatLng;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Partake {

    @Setter(AccessLevel.NONE)
    @NonNull
    private SMSPeer owner;

    @NonNull
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private LatLng position;

    public Partake(String phoneNumber, LatLng position){
        this(new SMSPeer(phoneNumber),position);
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
