package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.karkhanaapp.models.Farm;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.utils.NotificationUtils;
import com.google.firebase.auth.FirebaseAuth;

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
        EditText etSurvey = findViewById(R.id.etSurvey);
        EditText etFarmArea = findViewById(R.id.etFarmArea);
        ImageButton backBtn = findViewById(R.id.backBtn);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);
        WebView farmMapWebView = findViewById(R.id.farmMapWebView);
        FarmRepository farmRepository = new FarmRepository();

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
            String uid = FirebaseAuth.getInstance().getUid();
            String district = String.valueOf(districtSpinner.getSelectedItem());
            String village = String.valueOf(villageSpinner.getSelectedItem());
            String cropType = String.valueOf(cropTypeSpinner.getSelectedItem());
            String surveyNumber = etSurvey.getText().toString().trim();
            String areaText = etFarmArea.getText().toString().trim();

            if (uid == null) {
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_LONG).show();
                return;
            }
            if (district.startsWith("Select") || village.startsWith("Select") || cropType.startsWith("Select")) {
                Toast.makeText(this, "Select district, village and crop type", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(surveyNumber)) {
                etSurvey.setError("Enter survey number");
                etSurvey.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(areaText)) {
                etFarmArea.setError("Enter farm area");
                etFarmArea.requestFocus();
                return;
            }

            double area;
            try {
                area = Double.parseDouble(areaText);
            } catch (NumberFormatException e) {
                etFarmArea.setError("Enter valid area");
                etFarmArea.requestFocus();
                return;
            }

            btnNext.setEnabled(false);
            Farm farm = new Farm(uid, surveyNumber, district, village, area, cropType);
            farm.setpLocation("Pinned on map");
            farmRepository.createFarm(farm, new FarmRepository.OnCompleteListener() {
                @Override
                public void onSuccess(Farm createdFarm) {
                    btnNext.setEnabled(true);
                    Toast.makeText(FarmLocationActivity.this, "Farm saved", Toast.LENGTH_SHORT).show();
                    NotificationUtils.showNotification(
                            FarmLocationActivity.this,
                            "Farm added successfully",
                            "Your farm " + village + " - Survey " + surveyNumber + " is now saved.",
                            (int) (System.currentTimeMillis() & 0x0FFFFFFF)
                    );
                    Intent intent = new Intent(FarmLocationActivity.this, MainAppActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    btnNext.setEnabled(true);
                    Toast.makeText(FarmLocationActivity.this, "Failed to save farm: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
