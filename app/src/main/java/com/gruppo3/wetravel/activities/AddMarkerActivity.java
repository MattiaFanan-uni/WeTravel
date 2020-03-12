package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.mapmanager.types.DestinationMarker;

public class AddMarkerActivity extends AppCompatActivity {

    private TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        //LatLng coordinate = getIntent().getExtras().getParcelable("COORDINATE");
        Double latitude = getIntent().getExtras().getDouble("latitude");
        Double longitude = getIntent().getExtras().getDouble("longitude");
        location = (TextView)findViewById(R.id.locationTextView);
        location.setText(String.valueOf(latitude) + " " + String.valueOf(longitude));
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button sendMission = (Button) findViewById(R.id.sendMissionButton);
        sendMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO inviare la missione
            }
        });
    }
}
