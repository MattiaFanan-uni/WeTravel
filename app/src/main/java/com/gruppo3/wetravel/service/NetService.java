package com.gruppo3.wetravel.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;
import com.gruppo3.wetravel.DBDictionary;
import com.gruppo3.wetravel.Partake;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NetService extends Service {

    private final IBinder binder = new DBServiceBinder();
    private DBDictionary dictionary;
    WeakReference<Context> authorizedContextReference;//the only context that is authorized to use the net

    @Nullable
    @Override
    public synchronized IBinder onBind(Intent intent) {

        if (isClientAuthorized((Context) intent.getParcelableExtra("callerContext")))
            return binder;
        return null;
    }

    private DBDictionary changeDB(Context context) {
        dictionary.close();
        return new DBDictionary(context);
    }

    public class DBServiceBinder extends Binder {
        NetService getService() {
            return NetService.this;
        }

    }

    public boolean isClientAuthorized(Context newClient) {
        if (newClient == null)//client context not found
            return false;

        Context authorizedClient = authorizedContextReference.get();
        if (authorizedClient == null) {
            authorizedContextReference = new WeakReference<>(newClient);
            //switch to the right DB
            dictionary = changeDB(authorizedContextReference.get());
            //re initializes network
            SMSJoinableNetManager.getInstance().setup(authorizedContextReference.get());
            SMSJoinableNetManager.getInstance().setNetSubscriberList(dictionary);
            SMSJoinableNetManager.getInstance().setNetDictionary(dictionary);

        } else {
            if (!newClient.equals(authorizedClient))//not authorized
                return false;
        }

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        dictionary.close();
        super.onDestroy();
    }


    /**
     * Adds a resource to the network dictionary
     */
    public void addResource(Context client, @NonNull final Partake partake) {
        if (isClientAuthorized(client))
            dictionary.addResource(partake.getOwner().getAddress(), DBDictionary.convertLatLngToString(partake.getPosition()));
    }

    /**
     * Removes a resource from the dictionary
     */
    public void removeResource(Context client, @NonNull final Partake partake) {
        if (isClientAuthorized(client))
            dictionary.removeResource(partake.getOwner().getAddress());
    }

    /**
     * Returns a resource in the dictionary
     *
     * @return Returns a resource corresponding to the key if present in the dictionary,
     * else returns null
     */
    public String getResource(Context client, @NonNull Partake partake) {
        if (isClientAuthorized(client))
            return dictionary.getResource(partake.getOwner().getAddress());
        return null;
    }

    /**
     * Adds a subscriber to this network
     *
     * @param subscriber The subscriber to add to the net
     */
    public void addSubscriber(Context client, SMSPeer subscriber) {
        if (isClientAuthorized(client))
            dictionary.addSubscriber(subscriber);
    }

    /**
     * @return Returns the set of all the current subscribers to the net
     */
    public Set<SMSPeer> getSubscribers(Context client) {
        if (isClientAuthorized(client))
            return dictionary.getSubscribers();
        return new HashSet<SMSPeer>();
    }

    /**
     * Removes a given subscriber from the subscribers
     *
     * @param subscriber The subscriber to remove
     */
    public void removeSubscriber(Context client, SMSPeer subscriber) {
        if (isClientAuthorized(client))
            dictionary.removeSubscriber(subscriber);
    }

    public ArrayList<Partake> getClosestPartakes(Context client, LatLng position, Double radius) {

        ArrayList<Partake> toReturn = new ArrayList<>();

        if (isClientAuthorized(client))
            toReturn = dictionary.getClosestPartakes(position, radius);

        return toReturn;
    }


}
