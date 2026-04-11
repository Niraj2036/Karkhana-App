package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HarvestTrackingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_harvest_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout cropInfoCard = view.findViewById(R.id.cropInfoCard);
        LinearLayout timelineSection = view.findViewById(R.id.timelineSection);
        LinearLayout emptyHarvestState = view.findViewById(R.id.emptyHarvestState);
        TextView tvSelectedFarm = view.findViewById(R.id.tvSelectedFarm);
        TextView tvSelectedHarvest = view.findViewById(R.id.tvSelectedHarvest);

        boolean isEnrolled = EnrollmentState.isEnrolled(requireContext());
        cropInfoCard.setVisibility(isEnrolled ? View.VISIBLE : View.GONE);
        timelineSection.setVisibility(isEnrolled ? View.VISIBLE : View.GONE);
        emptyHarvestState.setVisibility(isEnrolled ? View.GONE : View.VISIBLE);

        if (isEnrolled) {
            tvSelectedFarm.setText(EnrollmentState.getFarmName(requireContext()));
            tvSelectedHarvest.setText(getString(R.string.harvest_label_format, EnrollmentState.getHarvestName(requireContext())));
        }
    }
}
