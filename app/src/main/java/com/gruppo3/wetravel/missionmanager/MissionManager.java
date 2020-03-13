package com.gruppo3.wetravel.missionmanager;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.eis.communication.network.listeners.RemoveResourceListener;
import com.eis.communication.network.listeners.SetResourceListener;
import com.eis.smslibrary.SMSMessage;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.mapmanager.MapManager;
import com.gruppo3.wetravel.types.DestinationMarker;
import com.gruppo3.wetravel.util.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class MissionManager implements BroadcastReceiver.OnMessageReceivedListener {
    private MapManager mapManager;
    private Activity activity;
    private int resourceCounter = 0;
    private HashMap<String, Marker> markers = new HashMap<>();

    public MissionManager(MapManager mapManager, Activity activity) {
        this.mapManager = mapManager;
        this.activity = activity;
        BroadcastReceiver.setDelegate(this);
    }

    @Override
    public void onMessageReceived(SMSMessage message, RequestType requestType, @Nullable String[] keys, @Nullable String[] values) {
        if (requestType == RequestType.AddResource && keys != null && values != null) {
            for (int i = 0; i < keys.length; i++) {
                String[] parameters = values[i].split(Const.PARAMETER_SEPARATOR);
                resourceCounter = Integer.parseInt(parameters[0]);

                DestinationMarker destinationMarker = new DestinationMarker(new LatLng(Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2])), parameters[3], parameters[4]);
                destinationMarker.setIndex(resourceCounter);
                destinationMarker.owned = false;
                activity.runOnUiThread(() -> markers.put(String.valueOf(destinationMarker.getIndex()), mapManager.addMarker(destinationMarker)));
            }
        }
        else if (requestType == RequestType.RemoveResource && keys != null) {
            for (String key : keys) {
                String markerIndex = key.replace(Const.MISSION_IDENTIFIER, "");
                Marker marker = markers.get(markerIndex);
                if (marker != null)
                    activity.runOnUiThread(marker::remove);
            }
        }
    }

    public void addMission(DestinationMarker destinationMarker) {
        resourceCounter++;
        destinationMarker.setIndex(resourceCounter);

        String value = new StringBuilder()
                .append(destinationMarker.getIndex())
                .append(Const.PARAMETER_SEPARATOR)
                .append(destinationMarker.getLatLng().latitude)
                .append(Const.PARAMETER_SEPARATOR)
                .append(destinationMarker.getLatLng().longitude)
                .append(Const.PARAMETER_SEPARATOR)
                .append(destinationMarker.getTitle())
                .append(Const.PARAMETER_SEPARATOR)
                .append(destinationMarker.getObject())
                .toString();

        SMSJoinableNetManager.getInstance().setResource(String.valueOf(destinationMarker.getIndex()), value, new SetResourceListener<String, String, SMSFailReason>() {
            @Override
            public void onResourceSet(String key, String value) {
                mapManager.addMarker(destinationMarker);
            }

            @Override
            public void onResourceSetFail(String key, String value, SMSFailReason reason) {

            }
        });
    }

    public void removeMission(DestinationMarker destinationMarker) {
        SMSJoinableNetManager.getInstance().removeResource(String.valueOf(destinationMarker.getIndex()), new RemoveResourceListener<String, SMSFailReason>() {
            @Override
            public void onResourceRemoved(String key) {

            }

            @Override
            public void onResourceRemoveFail(String key, SMSFailReason reason) {

            }
        });
    }
}
