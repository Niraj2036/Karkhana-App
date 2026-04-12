package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.models.Harvest;
import com.example.karkhanaapp.repositories.HarvestRepository;
import com.google.firebase.auth.FirebaseAuth;

public class HarvestTrackingFragment extends Fragment {

    private HarvestRepository harvestRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_harvest_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        harvestRepository = new HarvestRepository();

        LinearLayout cropInfoCard = view.findViewById(R.id.cropInfoCard);
        LinearLayout timelineSection = view.findViewById(R.id.timelineSection);
        LinearLayout emptyHarvestState = view.findViewById(R.id.emptyHarvestState);
        TextView tvSelectedFarm = view.findViewById(R.id.tvSelectedFarm);
        TextView tvSelectedHarvest = view.findViewById(R.id.tvSelectedHarvest);

        boolean isEnrolled = EnrollmentState.isEnrolled(requireContext());

        String uid = FirebaseAuth.getInstance().getUid();

        if (isEnrolled) {
            // Show UI
            cropInfoCard.setVisibility(View.VISIBLE);
            timelineSection.setVisibility(View.VISIBLE);
            emptyHarvestState.setVisibility(View.GONE);

            tvSelectedFarm.setText(EnrollmentState.getFarmName(requireContext()));
            tvSelectedHarvest.setText(getString(R.string.harvest_label_format, EnrollmentState.getHarvestName(requireContext())));
        } else if (uid != null) {
            // Load active harvests from Firestore
            harvestRepository.getHarvestsByFarmerId(uid).observe(getViewLifecycleOwner(), harvests -> {
                if (harvests != null && !harvests.isEmpty()) {
                    // Show first harvest
                    Harvest harvest = harvests.get(0);
                    cropInfoCard.setVisibility(View.VISIBLE);
                    timelineSection.setVisibility(View.VISIBLE);
                    emptyHarvestState.setVisibility(View.GONE);

                    if (harvest.getCropType() != null) {
                        tvSelectedFarm.setText(harvest.getCropType());
                    }
                    if (harvest.getPlantingDate() != null) {
                        tvSelectedHarvest.setText(getString(R.string.harvest_label_format, harvest.getPlantingDate()));
                    }
                } else {
                    // No harvests - show empty state
                    cropInfoCard.setVisibility(View.GONE);
                    timelineSection.setVisibility(View.GONE);
                    emptyHarvestState.setVisibility(View.VISIBLE);
                }
            });
        } else {
            // Not authenticated
            emptyHarvestState.setVisibility(View.VISIBLE);
            cropInfoCard.setVisibility(View.GONE);
            timelineSection.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
        }
    }
}
