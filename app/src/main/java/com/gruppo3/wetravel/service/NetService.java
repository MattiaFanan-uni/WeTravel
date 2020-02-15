package com.gruppo3.wetravel.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.eis.communication.network.Invitation;
import com.eis.communication.network.JoinableNetworkManager;
import com.eis.communication.network.listeners.GetResourceListener;
import com.eis.communication.network.listeners.InviteListener;
import com.eis.communication.network.listeners.JoinInvitationListener;
import com.eis.communication.network.listeners.RemoveResourceListener;
import com.eis.communication.network.listeners.SetResourceListener;
import com.eis.smslibrary.SMSPeer;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;
import com.gruppo3.wetravel.DBDictionary;
import com.gruppo3.wetravel.Partake;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NetService extends Service implements JoinableNetworkManager<String, String, SMSPeer, SMSFailReason, Invitation<SMSPeer>> {

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

    /**
     * Method used to join the network after an invitation in received.
     *
     * @param invitation The invitation previously received.
     */
    @Override
    public void acceptJoinInvitation(Invitation<SMSPeer> invitation) {
        SMSJoinableNetManager.getInstance().acceptJoinInvitation(invitation);
    }

    /**
     * Sets the listener used to wait for invitations to join the network.
     *
     * @param joinInvitationListener Listener called upon invitation received.
     */
    @Override
    public void setJoinInvitationListener(JoinInvitationListener<Invitation<SMSPeer>> joinInvitationListener) {
        SMSJoinableNetManager.getInstance().setJoinInvitationListener(joinInvitationListener);
    }

    /**
     * Saves a resource value in the network for the specified key. If the save is successful
     * {@link SetResourceListener#onResourceSet(Object, Object)} is be called.
     *
     * @param key                 The key identifier for the resource.
     * @param value               The identified value of the resource.
     * @param setResourceListener Listener called on resource successfully saved or on fail.
     */
    @Override
    public void setResource(String key, String value, SetResourceListener<String, String, SMSFailReason> setResourceListener) {
        SMSJoinableNetManager.getInstance().setResource(key, value, setResourceListener);
    }

    /**
     * Retrieves a resource value from the network for the specified key. The value is returned inside
     * {@link GetResourceListener#onGetResource(Object, Object)}.
     *
     * @param key                 The key identifier for the resource.
     * @param getResourceListener Listener called on resource successfully retrieved or on fail.
     */
    @Override
    public void getResource(String key, GetResourceListener<String, String, SMSFailReason> getResourceListener) {
        SMSJoinableNetManager.getInstance().getResource(key, getResourceListener);
    }

    /**
     * Removes a resource value from the network for the specified key. If the removal is successful
     * {@link RemoveResourceListener#onResourceRemoved(Object)} is called
     *
     * @param key                    The key identifier for the resource.
     * @param removeResourceListener Listener called on resource successfully removed or on fail.
     */
    @Override
    public void removeResource(String key, RemoveResourceListener<String, SMSFailReason> removeResourceListener) {
        SMSJoinableNetManager.getInstance().removeResource(key, removeResourceListener);
    }

    /**
     * Invites another user to join the network. If the invitation is sent correctly
     *
     * @param peer           The address of the user to invite to join the network.
     * @param inviteListener Listener called on user invited or on fail.
     */
    @Override
    public void invite(SMSPeer peer, InviteListener<SMSPeer, SMSFailReason> inviteListener) {
        SMSJoinableNetManager.getInstance().invite(peer, inviteListener);
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

    public ArrayList<Partake> getClosestPartakes(Context client, LatLng position, Double radius) {

        ArrayList<Partake> toReturn = new ArrayList<>();

        if (isClientAuthorized(client))
            toReturn = dictionary.getClosestPartakes(position, radius);

        return toReturn;
    }


}
