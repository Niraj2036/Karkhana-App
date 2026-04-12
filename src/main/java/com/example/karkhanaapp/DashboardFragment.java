package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.models.Farmer;
import com.example.karkhanaapp.models.Harvest;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.repositories.HarvestRepository;
import com.example.karkhanaapp.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private UserRepository userRepository;
    private FarmRepository farmRepository;
    private HarvestRepository harvestRepository;
    private TextView tvGreeting;
    private TextView tvTotalAreaValue;
    private TextView tvLastYieldValue;
    private TextView tvEnrolledFactoryName;
    private TextView tvHarvestStatus;
    private TextView tvPaymentsStatus;
    private TextView tvUpdate1Title;
    private TextView tvUpdate1Time;
    private TextView tvUpdate2Title;
    private TextView tvUpdate2Time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository();
        farmRepository = new FarmRepository();
        harvestRepository = new HarvestRepository();

        tvGreeting = view.findViewById(R.id.tvDashboardGreeting);
        tvTotalAreaValue = view.findViewById(R.id.tvTotalAreaValue);
        tvLastYieldValue = view.findViewById(R.id.tvLastYieldValue);
        tvEnrolledFactoryName = view.findViewById(R.id.tvEnrolledFactoryName);
        tvHarvestStatus = view.findViewById(R.id.tvHarvestStatusValue);
        tvPaymentsStatus = view.findViewById(R.id.tvPaymentsStatusValue);
        tvUpdate1Title = view.findViewById(R.id.tvUpdate1Title);
        tvUpdate1Time = view.findViewById(R.id.tvUpdate1Time);
        tvUpdate2Title = view.findViewById(R.id.tvUpdate2Title);
        tvUpdate2Time = view.findViewById(R.id.tvUpdate2Time);

        setDashboardDefaults();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRepository.getFarmerById(uid).observe(getViewLifecycleOwner(), farmer -> {
                bindFarmer(farmer);
            });

            farmRepository.getFarmsByFarmerId(uid).observe(getViewLifecycleOwner(), farms -> {
                bindFarms(farms);
            });

            harvestRepository.getHarvestsByFarmerId(uid).observe(getViewLifecycleOwner(), harvests -> {
                bindHarvests(harvests);
            });
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDashboardDefaults() {
        tvGreeting.setText(getString(R.string.dashboard_greeting_default));
        tvTotalAreaValue.setText("0.0");
        tvLastYieldValue.setText("0.0");
        tvEnrolledFactoryName.setText(getString(R.string.dashboard_not_enrolled));
        tvHarvestStatus.setText(getString(R.string.dashboard_no_active_harvest));
        tvPaymentsStatus.setText(getString(R.string.dashboard_no_payment_updates));
        tvUpdate1Title.setText(getString(R.string.dashboard_no_recent_updates));
        tvUpdate1Time.setText("--");
        tvUpdate2Title.setText(getString(R.string.dashboard_no_recent_updates));
        tvUpdate2Time.setText("--");
    }

    private void bindFarmer(Farmer farmer) {
        if (farmer == null) {
            return;
        }
        String name = farmer.getName();
        if (name != null && !name.trim().isEmpty()) {
            tvGreeting.setText(getString(R.string.dashboard_greeting_name, name.trim()));
        }
        if (farmer.getLastYield() != null) {
            tvLastYieldValue.setText(String.format(Locale.US, "%.1f", farmer.getLastYield()));
        }
    }

    private void bindFarms(List<com.example.karkhanaapp.models.Farm> farms) {
        if (farms == null || farms.isEmpty()) {
            return;
        }
        double totalArea = 0;
        String firstFactoryId = null;
        for (com.example.karkhanaapp.models.Farm farm : farms) {
            if (farm.getArea() != null) {
                totalArea += farm.getArea();
            }
            if (firstFactoryId == null && farm.getSugarFactoryId() != null && !farm.getSugarFactoryId().trim().isEmpty()) {
                firstFactoryId = farm.getSugarFactoryId().trim();
            }
        }
        tvTotalAreaValue.setText(String.format(Locale.US, "%.1f", totalArea));
        if (firstFactoryId != null) {
            tvEnrolledFactoryName.setText(firstFactoryId);
        }
    }

    private void bindHarvests(List<Harvest> harvests) {
        if (harvests == null || harvests.isEmpty()) {
            return;
        }
        Harvest latest = harvests.get(0);
        if (latest.getStatus() != null && !latest.getStatus().trim().isEmpty()) {
            tvHarvestStatus.setText(latest.getStatus());
            tvUpdate1Title.setText(getString(R.string.dashboard_harvest_status_update, latest.getStatus()));
            tvUpdate1Time.setText(getString(R.string.dashboard_time_now));
        }
        if (latest.getActualWeight() != null && latest.getActualWeight() > 0) {
            tvPaymentsStatus.setText(getString(R.string.dashboard_payment_pending));
            tvUpdate2Title.setText(getString(R.string.dashboard_weight_update, latest.getActualWeight()));
            tvUpdate2Time.setText(getString(R.string.dashboard_time_now));
        }
    }
}
