package com.gruppo3.wetravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.smsnetwork.SMSJoinableNetManager;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LauncherActivity extends AppCompatActivity {

    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Programatically UI initialization
        initUI();

        //initialize Net
        SMSJoinableNetManager.getInstance().setup(this);

        //TODO nomefileDict and nomfileList go on res/values/strings.xml
        //try to retrieve the saved dict
        try {
            ObjectInputStream streamReader = new ObjectInputStream(this.openFileInput("nomefileDict.extension"));
            SMSJoinableNetManager.getInstance().setNetDictionary(new DummyResDict(streamReader));
        } catch (IOException | ClassNotFoundException e) {
            SMSJoinableNetManager.getInstance().setNetDictionary(new DummyResDict());
        }

        //try to retrieve the saved subscList
        try {
            ObjectInputStream streamReader = new ObjectInputStream(this.openFileInput("nomefileList.extension"));
            SMSJoinableNetManager.getInstance().setNetSubscriberList(new DummySubscriberList(streamReader));
        } catch (IOException | ClassNotFoundException e) {
            SMSJoinableNetManager.getInstance().setNetSubscriberList(new DummySubscriberList());
        }

        //info-----
        //TODO use ((DummyResDict)SMSJoinableNetManager.getInstance().getNetDictionary()).save(ObjectOutputStream); to save
        //throws IOException

        //TODO use SMSJoinableNetManager.getInstance().setNetSubscriberList(new DummySubscriberList(ObjectInputStream)); to retrieve
        //throws IOException, ClassNotFoundException

        //TODO to build an output stream // new ObjectStreamWriter(context.openFileOutput("nomefile", Context.MODE_PRIVATE));

        //TODO same for NetSubscriberList

        //TODO the ResDict is <String,String> so we can use phonenumber(key), LatLng parsed to string (Value)


        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        }));
    }

    /**
     * Open the activity to let the user sign up
     */
    public void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Initialize UI elements that can't be set in activity_launcher.xml.
     */
    private void initUI() {
        // Hiding system status bar
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        // Adding icon in each TextView
        TextView[] textViewArray = new TextView[]{findViewById(R.id.textViewtUsername), findViewById(R.id.textViewPassword)};
        int[] iconsResId = new int[]{R.drawable.user, R.drawable.password};
        for (int i = 0; i < textViewArray.length; i++) {
            textViewArray[i].setCompoundDrawablesWithIntrinsicBounds(iconsResId[i], 0, 0, 0);
            textViewArray[i].setCompoundDrawablePadding(32);
        }
    }

    /**
     * Checks user input and logs into the system if there isn't any issue.
     *
     * @param v Clicked view
     */
    public void buttonLogin_onClick(View v) {
        // TODO: Manage LauncherActivity closing
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


}
