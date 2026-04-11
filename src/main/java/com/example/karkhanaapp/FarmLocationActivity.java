package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FarmLocationActivity extends AppCompatActivity {

    private static final String FARM_MAP_URL = "https://www.openstreetmap.org/export/embed.html?bbox=74.0%2C15.0%2C75.5%2C16.8&layer=mapnik";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        Spinner areaTypeSpinner = findViewById(R.id.areaTypeSpinner);
        Spinner districtSpinner = findViewById(R.id.districtSpinner);
        Spinner villageSpinner = findViewById(R.id.villageSpinner);
        Spinner cropTypeSpinner = findViewById(R.id.cropTypeSpinner);
        ImageButton backBtn = findViewById(R.id.backBtn);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);
        WebView farmMapWebView = findViewById(R.id.farmMapWebView);

        String[] areaTypes = {"Select Area Type", "Rural", "Urban"};
        ArrayAdapter<String> areaTypeAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, areaTypes);
        areaTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        areaTypeSpinner.setAdapter(areaTypeAdapter);

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

        String[] cropTypes = {"Select Crop Type", "Sugarcane", "Cotton", "Wheat", "Maize"};
        ArrayAdapter<String> cropTypeAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, cropTypes);
        cropTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        cropTypeSpinner.setAdapter(cropTypeAdapter);

        WebSettings settings = farmMapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        farmMapWebView.setWebViewClient(new WebViewClient());
        farmMapWebView.loadUrl(FARM_MAP_URL);

        backBtn.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            Toast.makeText(this, "Farm saved", Toast.LENGTH_SHORT).show();
            if (isTaskRoot()) {
                Intent intent = new Intent(FarmLocationActivity.this, MainAppActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        });
    }
}
