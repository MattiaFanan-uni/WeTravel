package com.gruppo3.wetravel.missionmanager;

import android.app.Activity;
import android.util.Log;

import com.eis.communication.network.listeners.RemoveResourceListener;
import com.eis.communication.network.listeners.SetResourceListener;
import com.eis.smslibrary.SMSMessage;
import com.eis.smsnetwork.RequestType;
import com.eis.smsnetwork.SMSFailReason;
import com.eis.smsnetwork.SMSJoinableNetManager;
import com.google.android.gms.maps.model.LatLng;
import com.gruppo3.wetravel.BroadcastReceiver;
import com.gruppo3.wetravel.mapmanager.MapManager;
import com.gruppo3.wetravel.types.DestinationMarker;
import com.gruppo3.wetravel.util.Const;

public class MissionManager implements BroadcastReceiver.OnMessageReceivedListener {
    private MapManager mapManager;
    private Activity activity;
    private int resourceCounter = 0;

    public MissionManager(MapManager mapManager, Activity activity) {
        this.mapManager = mapManager;
        this.activity = activity;
        BroadcastReceiver.setDelegate(this);
    }

    @Override
    public void onMessageReceived(SMSMessage message, RequestType requestType, String[] keys, String[] values) {
        if (requestType == RequestType.AddResource && keys != null && values != null) {
            for (int i = 0; i < keys.length; i++) {
                String[] parameters = values[i].split(Const.PARAMETER_SEPARATOR);
                resourceCounter = Integer.parseInt(parameters[0]);

                Log.d("MAP_DEMO", "Resource received. Index: " + parameters[0] + ". Resource counter: " + resourceCounter);

                DestinationMarker destinationMarker = new DestinationMarker(new LatLng(Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2])), parameters[3], parameters[4]);
                destinationMarker.setIndex(resourceCounter);
                activity.runOnUiThread(() -> mapManager.addMarker(destinationMarker));
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

        SMSJoinableNetManager.getInstance().setResource(Const.MISSION_IDENTIFIER + String.valueOf(destinationMarker.getIndex()), value, new SetResourceListener<String, String, SMSFailReason>() {
            @Override
            public void onResourceSet(String key, String value) {
                mapManager.addMarker(destinationMarker);
                Log.d("MAP_DEMO", "Resource set. Index: " + String.valueOf(destinationMarker.getIndex()) + ". Resource counter: " + resourceCounter);
            }

            @Override
            public void onResourceSetFail(String key, String value, SMSFailReason reason) {

            }
        });
    }

    public void removeMission(DestinationMarker destinationMarker) {
        SMSJoinableNetManager.getInstance().removeResource(Const.MISSION_IDENTIFIER + String.valueOf(destinationMarker.getIndex()), new RemoveResourceListener<String, SMSFailReason>() {
            @Override
            public void onResourceRemoved(String key) {

            }

            @Override
            public void onResourceRemoveFail(String key, SMSFailReason reason) {

            }
        });
    }
}
