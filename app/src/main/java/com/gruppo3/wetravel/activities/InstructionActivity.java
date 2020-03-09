package com.gruppo3.wetravel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gruppo3.wetravel.R;

public class InstructionActivity extends AppCompatActivity {
    private static final int PERMISSIONS_ALL_RC = 1; // Permissions request code

    /**
     * Application needed permissions.
     */
    public static final String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        findViewById(R.id.okInstructionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(InstructionActivity.this, PERMISSIONS, PERMISSIONS_ALL_RC);
            }
        });
    }

    /**
     * {@inheritDoc}
     * When user finishes the permission granting "process" this method will be called.<br>
     * If one or more permissions are denied, it will show a dialog explaining why the permissions are needed
     * and that the app can't work without them.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_ALL_RC) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    return; // We exit from this method because nothing can work without permissions (we still display Instruction Activity)
                }
            }

            finish(); // If all permissions are granted, we can close this activity and display again the map
        }
    }
}
