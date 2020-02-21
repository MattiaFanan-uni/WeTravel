package com.gruppo3.wetravel.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.Persistence.DBDictionary;
import com.gruppo3.wetravel.Persistence.DBDictionaryHelper;
import com.gruppo3.wetravel.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //Net Setup
        DBDictionary netDictionary = new DBDictionary(new DBDictionaryHelper(getApplicationContext()));
        SMSJoinableNetManager.getInstance().setup(getApplicationContext());
        SMSJoinableNetManager.getInstance().setNetSubscriberList(netDictionary);
        SMSJoinableNetManager.getInstance().setNetDictionary(netDictionary);

        //TODO check not subscribed condition
        if (SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 1) {
            finish();//close this activity
            Intent callMapActivity = new Intent(this, MapActivity.class);
            startActivity(callMapActivity);
        } else {
            finish();//close this activity
            Intent callNotSubscribedActivity = new Intent(this, NotSubscribedActivity.class);
            startActivity(callNotSubscribedActivity);
        }
    }


}
