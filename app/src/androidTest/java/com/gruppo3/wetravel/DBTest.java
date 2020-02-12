package com.gruppo3.wetravel;

import androidx.test.core.app.ApplicationProvider;

import com.eis.smslibrary.SMSPeer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;

@RunWith(JUnit4.class)
public class DBTest {
    private String resourceKey = "key";
    private String resourceValue = "value";

    private SMSPeer subscriber = new SMSPeer("+12027621401");

    private SMSPeer[] subscribers = {subscriber, new SMSPeer("+12136210002"), new SMSPeer("+19142329901")};

    DBDictionary dictionary;

    @Before
    public void init() {
        dictionary = new DBDictionary(ApplicationProvider.getApplicationContext(), 1);
        dictionary.removeResource(resourceKey);
        for (SMSPeer sub : subscribers)
            dictionary.removeSubscriber(sub);
    }

    @After
    public void finish() {
        dictionary.onDestroy();
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
    public void TestSubscribersNotIn() {
        for (SMSPeer sub : subscribers)
            Assert.assertFalse(dictionary.getSubscribers().contains(sub));
    }

    @Test
    public void StoreAndRemoveSubscriber() {
        dictionary.addSubscriber(subscriber);
        Set<SMSPeer> set = dictionary.getSubscribers();
        Assert.assertTrue(set.contains(subscriber));
        dictionary.removeSubscriber(subscriber);
        Assert.assertFalse(dictionary.getSubscribers().contains(subscriber));
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

}
