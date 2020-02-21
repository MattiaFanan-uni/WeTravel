package com.gruppo3.wetravel;

import com.eis.communication.network.NetSubscriberList;
import com.eis.smslibrary.SMSPeer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DummySubscriberList implements NetSubscriberList<SMSPeer> {

    HashSet<SMSPeer> list;

    public DummySubscriberList() {
        list = new HashSet<>();
    }

    public DummySubscriberList(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        list = (HashSet<SMSPeer>) inputStream.readObject();
        inputStream.close();
    }


    /**
     * Adds a subscriber to this network
     *
     * @param subscriber The subscriber to add to the net
     */
    @Override
    public void addSubscriber(SMSPeer subscriber) {
        list.add(subscriber);
    }

    /**
     * @return Returns the set of all the current subscribers to the net
     */
    @Override
    public Set<SMSPeer> getSubscribers() {
        return list;
    }

    /**
     * Removes a given subscriber from the subscribers
     *
     * @param subscriber The subscriber to remove
     */
    @Override
    public void removeSubscriber(SMSPeer subscriber) {
        list.remove(subscriber);
    }

    public void save(ObjectOutputStream streamWriter) throws IOException {
        streamWriter.writeObject(list);
        streamWriter.close();
    }
}
