# Activities Integration Guide

This document shows how to integrate Firebase repositories into existing activities to replace hardcoded data.

## 1. FarmLocationActivity - Save Farm Data

**Current State:** Probably saves to SharedPreferences or doesn't persist
**Goal:** Save farm data to Firestore `farms` collection

```java
package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.karkhanaapp.models.Farm;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.utils.FirebaseHelper;

public class FarmLocationActivity extends AppCompatActivity {

    private EditText etSurveyNumber, etDistrict, etVillage, etArea, etCropType;
    private Button btnSaveFarm;
    private FarmRepository farmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_location);

        // Initialize
        farmRepository = new FarmRepository();
        initializeViews();

        btnSaveFarm.setOnClickListener(v -> saveFarmToFirestore());
    }

    private void initializeViews() {
        etSurveyNumber = findViewById(R.id.etSurveyNumber);
        etDistrict = findViewById(R.id.etDistrict);
        etVillage = findViewById(R.id.etVillage);
        etArea = findViewById(R.id.etArea);
        etCropType = findViewById(R.id.etCropType);
        btnSaveFarm = findViewById(R.id.btnSaveFarm);
    }

    private void saveFarmToFirestore() {
        String surveyNumber = etSurveyNumber.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String village = etVillage.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String cropType = etCropType.getText().toString().trim();

        // Validate inputs
        if (surveyNumber.isEmpty() || district.isEmpty() || village.isEmpty() 
                || areaStr.isEmpty() || cropType.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Double area = Double.parseDouble(areaStr);

            // Get current user ID
            String farmerId = FirebaseHelper.getInstance().getCurrentUserId();
            if (farmerId == null) {
                Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Farm object
            Farm farm = new Farm(farmerId, surveyNumber, district, village, area, cropType);

            // Save to Firestore
            farmRepository.createFarm(farm, new FarmRepository.OnCompleteListener() {
                @Override
                public void onSuccess(Farm savedFarm) {
                    Toast.makeText(FarmLocationActivity.this, 
                            "Farm saved successfully", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to next activity
                    startActivity(new Intent(FarmLocationActivity.this, MainAppActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(FarmLocationActivity.this, 
                            "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid area value", Toast.LENGTH_SHORT).show();
        }
    }
}
```

## 2. PersonalDetailsActivity - Save Profile Data

**Current State:** Probably hardcoded or saved to SharedPreferences
**Goal:** Save profile data to Firestore `profiles` collection

```java
package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.karkhanaapp.models.Profile;
import com.example.karkhanaapp.repositories.ProfileRepository;
import com.example.karkhanaapp.utils.FirebaseHelper;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText etAadhaar, etDistrict, etVillage;
    private Button btnSaveDetails;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        profileRepository = new ProfileRepository();
        initializeViews();

        btnSaveDetails.setOnClickListener(v -> saveProfileToFirestore());
    }

    private void initializeViews() {
        etAadhaar = findViewById(R.id.etAadhaar);
        etDistrict = findViewById(R.id.etDistrict);
        etVillage = findViewById(R.id.etVillage);
        btnSaveDetails = findViewById(R.id.btnSaveDetails);
    }

    private void saveProfileToFirestore() {
        String aadhaar = etAadhaar.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String village = etVillage.getText().toString().trim();

        if (aadhaar.isEmpty() || district.isEmpty() || village.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mask Aadhaar - only show last 4 digits
        String aadhaarMasked = "XXXX-XXXX-" + aadhaar.substring(Math.max(0, aadhaar.length() - 4));

        String farmerId = FirebaseHelper.getInstance().getCurrentUserId();
        if (farmerId == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(farmerId, aadhaarMasked, district, village);

        profileRepository.saveProfile(profile, new ProfileRepository.OnCompleteListener() {
            @Override
            public void onSuccess(Profile savedProfile) {
                Toast.makeText(PersonalDetailsActivity.this, 
                        "Profile saved successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PersonalDetailsActivity.this, FarmLocationActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PersonalDetailsActivity.this, 
                        "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

## 3. HarvestTrackingFragment - Load Real-Time Harvest Data

**Current State:** Probably shows hardcoded harvest data
**Goal:** Load real harvests from Firestore with live updates

```java
package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karkhanaapp.adapters.HarvestAdapter; // You'll need to create this
import com.example.karkhanaapp.models.Harvest;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.repositories.HarvestRepository;
import com.example.karkhanaapp.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HarvestTrackingFragment extends Fragment {

    private RecyclerView harvestRecyclerView;
    private ProgressBar progressBar;
    private HarvestAdapter harvestAdapter;
    private HarvestRepository harvestRepository;
    private FarmRepository farmRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_harvest_tracking, container, false);

        harvestRecyclerView = view.findViewById(R.id.harvestRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        harvestRepository = new HarvestRepository();
        farmRepository = new FarmRepository();

        setupRecyclerView();
        loadHarvests();

        return view;
    }

    private void setupRecyclerView() {
        harvestAdapter = new HarvestAdapter(new ArrayList<>());
        harvestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        harvestRecyclerView.setAdapter(harvestAdapter);
    }

    private void loadHarvests() {
        progressBar.setVisibility(View.VISIBLE);

        String farmerId = FirebaseHelper.getInstance().getCurrentUserId();
        if (farmerId == null) return;

        // First get all farms for this farmer
        FarmRepository farmRepository = new FarmRepository();
        farmRepository.getFarmsByFarmerId(farmerId).observe(getViewLifecycleOwner(), 
            new Observer<List<com.example.karkhanaapp.models.Farm>>() {
                @Override
                public void onChanged(List<com.example.karkhanaapp.models.Farm> farms) {
                    if (farms == null || farms.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    List<Harvest> allHarvests = new ArrayList<>();

                    // For each farm, get harvests
                    for (com.example.karkhanaapp.models.Farm farm : farms) {
                        harvestRepository.getHarvestsByFarmId(farm.getFarmId())
                            .observe(getViewLifecycleOwner(), new Observer<List<Harvest>>() {
                                @Override
                                public void onChanged(List<Harvest> harvests) {
                                    if (harvests != null) {
                                        allHarvests.addAll(harvests);
                                        harvestAdapter.updateHarvests(allHarvests);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    }
                }
            });
    }
}
```

## 4. ProfileFragment - Load User Profile

**Current State:** Shows hardcoded profile
**Goal:** Load real profile from Firestore

```java
package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.karkhanaapp.models.Farmer;
import com.example.karkhanaapp.models.Profile;
import com.example.karkhanaapp.repositories.ProfileRepository;
import com.example.karkhanaapp.repositories.UserRepository;
import com.example.karkhanaapp.utils.FirebaseHelper;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvPhone, tvDistrict, tvVillage;
    private ImageView ivProfilePhoto;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvDistrict = view.findViewById(R.id.tvDistrict);
        tvVillage = view.findViewById(R.id.tvVillage);
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);

        userRepository = new UserRepository();
        profileRepository = new ProfileRepository();

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) return;

        // Load farmer data
        userRepository.getFarmerById(userId).observe(getViewLifecycleOwner(),
            new Observer<Farmer>() {
                @Override
                public void onChanged(Farmer farmer) {
                    if (farmer != null) {
                        tvName.setText(farmer.getName());
                        tvEmail.setText(farmer.getEmail());
                        tvPhone.setText(farmer.getPhone());
                        tvDistrict.setText(farmer.getDistrict());
                        tvVillage.setText(farmer.getVillage());
                    }
                }
            });

        // Load profile photo
        profileRepository.getProfileByFarmerId(userId).observe(getViewLifecycleOwner(),
            new Observer<Profile>() {
                @Override
                public void onChanged(Profile profile) {
                    if (profile != null && profile.getProfilePhotoUrl() != null) {
                        Glide.with(requireContext())
                            .load(profile.getProfilePhotoUrl())
                            .into(ivProfilePhoto);
                    }
                }
            });
    }
}
```

## 5. NearbyFactoriesFragment - Load Sugar Factories

**Current State:** Hardcoded factories
**Goal:** Load real factories from Firestore

```java
package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karkhanaapp.adapters.SugarFactoryAdapter; // Create this
import com.example.karkhanaapp.models.SugarFactory;
import com.example.karkhanaapp.repositories.SugarFactoryRepository;

import java.util.ArrayList;
import java.util.List;

public class NearbyFactoriesFragment extends Fragment {

    private RecyclerView factoriesRecyclerView;
    private SugarFactoryAdapter factoryAdapter;
    private SugarFactoryRepository factoryRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_factories, container, false);

        factoriesRecyclerView = view.findViewById(R.id.factoriesRecyclerView);
        factoryRepository = new SugarFactoryRepository();

        setupRecyclerView();
        loadFactories();

        return view;
    }

    private void setupRecyclerView() {
        factoryAdapter = new SugarFactoryAdapter(new ArrayList<>());
        factoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        factoriesRecyclerView.setAdapter(factoryAdapter);
    }

    private void loadFactories() {
        factoryRepository.getAllFactories().observe(getViewLifecycleOwner(),
            new Observer<List<SugarFactory>>() {
                @Override
                public void onChanged(List<SugarFactory> factories) {
                    if (factories != null) {
                        factoryAdapter.updateFactories(factories);
                    }
                }
            });
    }
}
```

## 6. OtpVerificationActivity - Verify OTP with Firestore

**Current State:** OTP verification probably not integrated
**Goal:** Use Firestore OTP verification

```java
package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.karkhanaapp.repositories.OTPRepository;

import java.util.Date;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button btnVerifyOtp;
    private String email;
    private OTPRepository otpRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        email = getIntent().getStringExtra("contact");
        otpRepository = new OTPRepository();

        otpInput = findViewById(R.id.otpInput);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        // In production, you would send OTP via email/SMS
        sendOTP();

        btnVerifyOtp.setOnClickListener(v -> verifyOTP());
    }

    private void sendOTP() {
        // Generate random 6-digit OTP
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        
        // OTP expires in 10 minutes
        Date expiresAt = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

        otpRepository.createOTP(email, otp, expiresAt, new OTPRepository.OnCompleteListener() {
            @Override
            public void onSuccess(com.example.karkhanaapp.models.OTPVerification otpVerification) {
                // In production, send via email/SMS service
                Toast.makeText(OtpVerificationActivity.this, 
                        "OTP sent to " + email, Toast.LENGTH_SHORT).show();
                
                // For development/testing, show OTP (remove in production!)
                Toast.makeText(OtpVerificationActivity.this, 
                        "Test OTP: " + otp, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OtpVerificationActivity.this, 
                        "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP() {
        String enteredOtp = otpInput.getText().toString().trim();

        if (enteredOtp.isEmpty()) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        otpRepository.verifyOTP(email, enteredOtp, new OTPRepository.OnCompleteListener() {
            @Override
            public void onSuccess(com.example.karkhanaapp.models.OTPVerification otpVerification) {
                Toast.makeText(OtpVerificationActivity.this, 
                        "OTP verified successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OtpVerificationActivity.this, PersonalDetailsActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OtpVerificationActivity.this, 
                        "Invalid OTP: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

## Adapters Needed

You'll need to create these adapters for RecyclerViews:

### HarvestAdapter
```java
public class HarvestAdapter extends RecyclerView.Adapter<HarvestAdapter.ViewHolder> {
    private List<Harvest> harvests;
    
    public HarvestAdapter(List<Harvest> harvests) {
        this.harvests = harvests;
    }

    public void updateHarvests(List<Harvest> newHarvests) {
        this.harvests = newHarvests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_harvest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Harvest harvest = harvests.get(position);
        holder.bind(harvest);
    }

    @Override
    public int getItemCount() {
        return harvests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(Harvest harvest) {
            // Bind data to views
        }
    }
}
```

---

**Next:** Test each activity thoroughly before releasing to production.

