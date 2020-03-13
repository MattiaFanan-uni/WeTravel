package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.util.Const;

import org.w3c.dom.Text;

public class MarkerDetailsActivity extends AppCompatActivity {

    private Button refuseButton;
    private Button acceptButton;
    private TextView peerTextView;
    private TextView coordinatesTextView;
    private TextView detailsTextView;
    private Button sendMessageButton;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        Bundle extras = getIntent().getExtras();
        if (extras == null)
            return;

        latitude = extras.getString(Const.EXTRA_LATITUDE);
        longitude = extras.getString(Const.EXTRA_LONGITUDE);
        String title = extras.getString(Const.EXTRA_TITLE);
        String details = extras.getString(Const.EXTRA_DETAILS);

        if (details != null && details.equals("null"))
            details = "";

        ((TextView)findViewById(R.id.peerTextView)).setText(title);
        ((TextView)findViewById(R.id.coordinatesTextView)).setText("Coordinates: " + latitude + ", " + longitude);
        ((TextView)findViewById(R.id.detailsTextView)).setText(details);

        findViewById(R.id.refuseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
}
