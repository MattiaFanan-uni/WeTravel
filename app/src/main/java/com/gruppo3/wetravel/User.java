package com.gruppo3.wetravel;

import com.google.android.gms.maps.model.LatLng;

public class User {
    public LatLng position;
    public String phoneNumber;

    public User(String phoneNumber,LatLng position){
        this.phoneNumber=phoneNumber;
        this.position=position;
    }

    @Override
    public boolean equals(Object o){

        if(this==o)return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user=(User)o;
        return position.equals(user.position) && phoneNumber.equals(user.phoneNumber);
    }
}
