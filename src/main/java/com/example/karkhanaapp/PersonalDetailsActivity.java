package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText etFullName, etAadhaar, etMobile;
    private Button btnNextStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        etFullName = findViewById(R.id.etFullName);
        etAadhaar = findViewById(R.id.etAadhaar);
        etMobile = findViewById(R.id.etMobile);
        btnNextStep = findViewById(R.id.btnNextStep);
        ImageButton backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        btnNextStep.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalDetailsActivity.this, MainAppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
