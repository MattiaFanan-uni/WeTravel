package com.gruppo3.wetravel;

import com.eis.communication.network.NetDictionary;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DummyResDict implements NetDictionary<String, String> {

    public HashMap<String, String> dict;

    public DummyResDict() {

        SMSJoinableNetManager.getInstance();
        dict = new HashMap<>();
    }

    public DummyResDict(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        dict = (HashMap<String, String>) inputStream.readObject();
        inputStream.close();
    }

    public void save(ObjectOutputStream streamWriter) throws IOException {
        streamWriter.writeObject(dict);
        streamWriter.close();
    }

    /**
     * Adds a resource to the network dictionary
     *
     * @param key      The key which defines the resource to be added
     * @param resource The resource to add
     */
    @Override
    public void addResource(String key, String resource) {
        if (!dict.containsKey(key))
            dict.put(key, resource);
    }

    /**
     * Removes a resource from the dictionary
     *
     * @param key The key which defines the resource to be removed
     */
    @Override
    public void removeResource(String key) {
        dict.remove(key);
    }

    /**
     * Returns a resource in the dictionary
     *
     * @param key The key which defines the resource to get
     * @return Returns a resource corresponding to the key if present in the dictionary,
     * else returns null
     */
    @Override
    public String getResource(String key) {
        return dict.get(key);
    }

    public ArrayList<User> getUsers(LatLng position, Double radius) {

        ArrayList<User> toReturn = new ArrayList<>();


        Object[] array = dict.entrySet().toArray();

        for (Object o : array) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) o;

            LatLng toCheck=Helper.convertStringToLatLng(entry.getValue());
            double distance=Helper.meterDistance(position,toCheck);

            if(Helper.meterDistance(position,toCheck)<=radius)
                toReturn.add(new User(entry.getKey(),toCheck));
        }

        return toReturn;
    }
}
