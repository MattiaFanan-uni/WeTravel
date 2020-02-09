package com.gruppo3.wetravel;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity(tableName = "pertakes")
public class Partake {
    @PrimaryKey
    int id;
    LatLng position;
    String owner;
}
