package com.gruppo3.wetravel;

import com.eis.communication.network.NetDictionary;
import com.eis.communication.network.NetSubscriberList;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.eis.smsnetwork.SMSNetSubscriberList;

import java.util.Set;

public class DataBaseDictionary implements NetDictionary<String,Integer>{


    public DataBaseDictionary(){

    }

    /**
     * Adds a resource to the network dictionary
     *
     * @param key      The key which defines the resource to be added
     * @param resource The resource to add
     */
    @Override
    public void addResource(String key, Integer resource) {

    }

    /**
     * Removes a resource from the dictionary
     *
     * @param key The key which defines the resource to be removed
     */
    @Override
    public void removeResource(String key) {

    }

    /**
     * Returns a resource in the dictionary
     *
     * @param key The key which defines the resource to get
     * @return Returns a resource corresponding to the key if present in the dictionary,
     * else returns null
     */
    @Override
    public Integer getResource(String key) {
        return null;
    }
}
