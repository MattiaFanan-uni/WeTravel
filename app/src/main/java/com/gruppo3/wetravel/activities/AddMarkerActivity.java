package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.gruppo3.wetravel.R;
import com.gruppo3.wetravel.util.Const;

public class AddMarkerActivity extends AppCompatActivity {

    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        Bundle extras = getIntent().getExtras();
        if (extras == null)
            throw new RuntimeException("Can't get new mission coordinates. Extras bundle is null");

        String latitude = String.valueOf(getIntent().getExtras().getDouble(Const.EXTRA_LATITUDE));
        String longitude = String.valueOf(getIntent().getExtras().getDouble(Const.EXTRA_LONGITUDE));

        TextView location = findViewById(R.id.locationTextView);
        location.setText("Coordinates: " + latitude + " " + longitude);

        findViewById(R.id.cancelButton).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        findViewById(R.id.sendMissionButton).setOnClickListener(v -> {
            TextView detailsTextView = findViewById(R.id.insertDetailsEditText);

            Intent data = new Intent();
            data.putExtra(Const.EXTRA_LATITUDE, latitude);
            data.putExtra(Const.EXTRA_LONGITUDE, longitude);
            data.putExtra(Const.EXTRA_TITLE, "title");
            data.putExtra(Const.EXTRA_DETAILS, detailsTextView.getText());
            setResult(RESULT_OK, data);
            finish();
        });
    }
}
