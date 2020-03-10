package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.gruppo3.wetravel.R;

public class MarkerDetailsActivity extends AppCompatActivity {

    private Button refuseButton;
    private Button acceptButton;
    private TextView peerTextView;
    private TextView coordinatesTextView;
    private TextView detailsTextView;
    private Button sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        //TODO Get the details and the location of a marker
    }
}
