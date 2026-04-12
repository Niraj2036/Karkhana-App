package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.karkhanaapp.models.Farmer;
import com.example.karkhanaapp.models.Profile;
import com.example.karkhanaapp.repositories.ProfileRepository;
import com.example.karkhanaapp.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText etFullName, etAadhaar, etMobile;
    private Button btnNextStep;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        etFullName = findViewById(R.id.etFullName);
        etAadhaar = findViewById(R.id.etAadhaar);
        etMobile = findViewById(R.id.etMobile);
        btnNextStep = findViewById(R.id.btnNextStep);
        ImageButton backBtn = findViewById(R.id.backBtn);
        profileRepository = new ProfileRepository();
        userRepository = new UserRepository();

        String contact = getIntent().getStringExtra("contact");
        if (!TextUtils.isEmpty(contact) && contact.matches("^\\+?\\d[\\d ]{8,}$")) {
            etMobile.setText(contact);
        }

        backBtn.setOnClickListener(v -> finish());

        btnNextStep.setOnClickListener(v -> saveProfileAndContinue());
    }

    private void saveProfileAndContinue() {
        String fullName = etFullName.getText().toString().trim();
        String aadhaarRaw = etAadhaar.getText().toString().replaceAll("\\s+", "").trim();
        String mobile = etMobile.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Enter full name");
            etFullName.requestFocus();
            return;
        }
        if (aadhaarRaw.length() < 4) {
            etAadhaar.setError("Enter valid Aadhaar number");
            etAadhaar.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Enter mobile number");
            etMobile.requestFocus();
            return;
        }

        btnNextStep.setEnabled(false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            FirebaseAuth.getInstance().signInAnonymously()
                    .addOnSuccessListener(result -> persistProfile(result.getUser(), fullName, aadhaarRaw, mobile))
                    .addOnFailureListener(e -> {
                        btnNextStep.setEnabled(true);
                        Toast.makeText(this, "Unable to continue: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            return;
        }

        persistProfile(currentUser, fullName, aadhaarRaw, mobile);
    }

    private void persistProfile(FirebaseUser user, String fullName, String aadhaarRaw, String mobile) {
        if (user == null) {
            btnNextStep.setEnabled(true);
            Toast.makeText(this, "User session missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();
        String aadhaarMasked = "XXXXXXXX" + aadhaarRaw.substring(aadhaarRaw.length() - 4);

        Farmer farmer = new Farmer();
        farmer.setFarmerId(uid);
        farmer.setName(fullName);
        farmer.setEmail(email != null ? email : "");
        farmer.setPhone(mobile);
        farmer.setAadhaarMasked(aadhaarMasked);
        farmer.setDistrict("Not set");
        farmer.setVillage("Not set");
        farmer.setTotalArea(0.0);
        farmer.setLastYield(0.0);
        userRepository.updateFarmer(uid, farmer);

        Profile profile = new Profile(uid, aadhaarMasked, "Not set", "Not set");
        profileRepository.saveProfile(profile, new ProfileRepository.OnCompleteListener() {
            @Override
            public void onSuccess(Profile savedProfile) {
                btnNextStep.setEnabled(true);
                Intent intent = new Intent(PersonalDetailsActivity.this, FarmLocationActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                btnNextStep.setEnabled(true);
                Toast.makeText(PersonalDetailsActivity.this, "Failed to save profile: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
