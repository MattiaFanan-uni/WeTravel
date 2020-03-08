package com.gruppo3.wetravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gruppo3.wetravel.R;

public class InstructionActivity extends AppCompatActivity {

    private TextView instructionTextView;
    private Button okInstructionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        okInstructionButton = (Button) findViewById(R.id.okInstructionButton);
        okInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMap = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(openMap);
            }
        });

    }
}
