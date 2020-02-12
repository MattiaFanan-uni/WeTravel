package com.gruppo3.wetravel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eis.smslibrary.SMSPeer;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText username;
    private EditText psw;
    private DummySubscriberList subList;

    public SignUpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText) findViewById(R.id.username);
        psw = (EditText) findViewById(R.id.psw);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSPeer peerUsername = new SMSPeer(username.getText().toString());
                subList.addSubscriber(peerUsername);
            }
        });
    }

}
