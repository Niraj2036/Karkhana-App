package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class FarmLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        Spinner districtSpinner = findViewById(R.id.districtSpinner);
        Spinner villageSpinner = findViewById(R.id.villageSpinner);
        ImageButton backBtn = findViewById(R.id.backBtn);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);

        // District spinner data
        String[] districts = {"Select District", "Belagavi", "Bagalkot", "Vijayapura", "Nashik", "Pune", "Kolhapur"};
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, districts);
        districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        districtSpinner.setAdapter(districtAdapter);

        // Village spinner data
        String[] villages = {"Select Village", "Athani", "Kagwad", "Shedbal", "Raibag", "Chikkodi"};
        ArrayAdapter<String> villageAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, villages);
        villageAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        villageSpinner.setAdapter(villageAdapter);

        backBtn.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(FarmLocationActivity.this, MainAppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
