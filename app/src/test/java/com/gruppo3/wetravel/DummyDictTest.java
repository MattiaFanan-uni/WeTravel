package com.gruppo3.wetravel;


import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DummyDictTest {

    User center=new User("123456",new LatLng(0,0));

    double radius=4000;//4km

    User[] closest={

            new User("23456",new LatLng(0.01,0.01)),
            new User("12345",new LatLng(0.02,0.01)),
            new User("13456",new LatLng(0.01,0.02)),
            new User("12456",new LatLng(0.02,0.02))

    };

    User[] notClose={

            new User("523456",new LatLng(0.05,0.05)),
            new User("512345",new LatLng(0.2,0.1)),
            new User("513456",new LatLng(0.3,0.2)),
            new User("512456",new LatLng(0.2,0.02))

    };

    @Test
    public void testReturnClosest(){

        DummyResDict dummyResDict=new DummyResDict();

        for(User u : closest)
            dummyResDict.addResource(u.phoneNumber,Helper.convertLatLngToString(u.position));

        for(User u : notClose)
            dummyResDict.addResource(u.phoneNumber,Helper.convertLatLngToString(u.position));

        ArrayList<User> retirievedClosest=dummyResDict.getUsers(center.position,radius);

        for (User u:closest)
            if(!retirievedClosest.contains(u))
                Assert.fail("not all closest returned");

        for (User u:notClose)
            if(retirievedClosest.contains(u))
                Assert.fail("a notClose returned");
    }

}
