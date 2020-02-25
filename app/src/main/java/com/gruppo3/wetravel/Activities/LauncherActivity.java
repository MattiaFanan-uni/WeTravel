package com.gruppo3.wetravel.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.smsnetwork.SMSJoinableNetManager;
import com.gruppo3.wetravel.Persistence.DBDictionary;
import com.gruppo3.wetravel.Persistence.DBDictionaryHelper;
import com.gruppo3.wetravel.R;

/**
 * If the user is already subscribed it will be opened the  MapActivty,
 * else it will be opened the activity which allows to invite or get invited to a network
 * DBDictionary adds persistence to dictionaries
 * DBDictionaryHelper manages the operations on the DB
 *
 * @author Riccardo Crociani
 */

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


        if (isSubscribed()) {
            finish();//close this activity
            Intent callMapActivity = new Intent(this, MapActivity.class);
            startActivity(callMapActivity);
        } else {
            finish();//close this activity
            Intent callNotSubscribedActivity = new Intent(this, NotSubscribedActivity.class);
            startActivity(callNotSubscribedActivity);
        }
    }

    /**
     *
     * @return boolean True if the user is subscribed to a network
     */
    private boolean isSubscribed(){
        return SMSJoinableNetManager.getInstance().getNetSubscriberList().getSubscribers().size() > 1;
    }
}
