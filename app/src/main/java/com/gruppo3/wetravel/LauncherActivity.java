package com.gruppo3.wetravel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Programatically UI initialization
        initUI();
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
        TextView[] textViewArray = new TextView[] { findViewById(R.id.textViewtUsername), findViewById(R.id.textViewPassword) };
        int[] iconsResId = new int[] { R.drawable.user, R.drawable.password };
        for (int i = 0; i < textViewArray.length; i++) {
            textViewArray[i].setCompoundDrawablesWithIntrinsicBounds(iconsResId[i], 0, 0, 0);
            textViewArray[i].setCompoundDrawablePadding(32);
        }
    }

    /**
     * Checks user input and logs into the system if there isn't any issue.
     * @param v Clicked view
     */
    public void buttonLogin_onClick(View v) {
        // TODO: Manage LauncherActivity closing
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
