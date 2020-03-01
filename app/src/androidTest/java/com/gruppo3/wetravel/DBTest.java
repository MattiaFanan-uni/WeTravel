package com.gruppo3.wetravel;

import androidx.test.core.app.ApplicationProvider;

import com.eis.smslibrary.SMSPeer;
import com.google.android.gms.maps.model.LatLng;
import com.gruppo3.wetravel.persistence.DBDictionary;
import com.gruppo3.wetravel.persistence.DBDictionaryHelper;
import com.gruppo3.wetravel.persistence.Partake;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

@RunWith(JUnit4.class)
public class DBTest {
    private String resourceKey = "key";
    private String resourceValue = "value";

    private SMSPeer[] subscribers = {
            new SMSPeer("+12027621401"),
            new SMSPeer("+12136210002"),
            new SMSPeer("+19142329901")};

    DBDictionary dictionary;

    @Before
    public void init() {
        DBDictionaryHelper inMemoryHelper=new DBDictionaryHelper(ApplicationProvider.getApplicationContext(),null,1);
        dictionary = new DBDictionary(inMemoryHelper);
    }

    @After
    public void finish() {
        dictionary.close();
    }

    @Test
    public void StoreAndRemoveResource() {
        Assert.assertNull(dictionary.getResource(resourceKey));
        dictionary.addResource(resourceKey, resourceValue);
        Assert.assertEquals(dictionary.getResource(resourceKey), resourceValue);
        dictionary.removeResource(resourceKey);
        Assert.assertNull(dictionary.getResource(resourceKey));
    }

    @Test
    public void SaveAndReturnAndRemoveSubscribers() {

        for (SMSPeer sub : subscribers)
            dictionary.addSubscriber(sub);

        for (SMSPeer sub : subscribers)
            Assert.assertTrue(dictionary.getSubscribers().contains(sub));

        for (SMSPeer sub : subscribers)
            dictionary.removeSubscriber(sub);

        for (SMSPeer sub : subscribers)
            Assert.assertFalse(dictionary.getSubscribers().contains(sub));
    }


    private Partake center=new Partake(subscribers[0], new LatLng(0, 0));

    private double radius=4000;//4km

    private Partake[] closest={

            new Partake("+12027621401",new LatLng(0.01,0.01)),
            new Partake("+12027621501",new LatLng(0.02,0.01)),
            new Partake("+12027621601",new LatLng(0.01,0.02)),
            new Partake("+12027621701",new LatLng(0.02,0.02))

    };

    private Partake[] notClose={

            new Partake("+12027621801",new LatLng(0.05,0.05)),
            new Partake("+12027621901",new LatLng(0.2,0.1)),
            new Partake("+12027622001",new LatLng(0.3,0.2)),
            new Partake("+12027622101",new LatLng(0.2,0.02))

    };

    @Test
    public void testReturnClosest(){

        for(Partake partake : closest)
            dictionary.addResource(partake.getOwner().getAddress(),DBDictionary.convertLatLngToString(partake.getPosition()));

        for(Partake partake : notClose)
            dictionary.addResource(partake.getOwner().getAddress(),DBDictionary.convertLatLngToString(partake.getPosition()));

        ArrayList<Partake> retrievedClosest=dictionary.getClosestPartakes(center.getPosition(),radius);

        for (Partake u:closest)
            if(!retrievedClosest.contains(u))
                Assert.fail("not all closest returned");

        for (Partake u:notClose)
            if(retrievedClosest.contains(u))
                Assert.fail("a notClose returned");
    }

}
