package com.example.karkhanaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.models.Farm;
import com.example.karkhanaapp.models.Harvest;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.repositories.HarvestRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HarvestTrackingFragment extends Fragment {

    private HarvestRepository harvestRepository;
    private FarmRepository farmRepository;
    private List<Farm> farms = new ArrayList<>();
    private List<Harvest> harvests = new ArrayList<>();
    private String selectedFarmId;

    private LinearLayout cropInfoCard;
    private LinearLayout timelineSection;
    private LinearLayout emptyHarvestState;
    private Spinner spinnerHarvestFarm;
    private TextView tvCropIdValue;
    private TextView tvCropFieldValue;
    private TextView tvSelectedFarm;
    private TextView tvSelectedHarvest;
    private TextView tvStepScheduledDate;
    private TextView tvStepHarvestDoneDate;
    private TextView tvStageScheduledStatus;
    private TextView tvStageHarvestDoneStatus;
    private TextView tvStageWeightStatus;
    private TextView tvStagePaymentStatus;
    private TextView tvNetWeightValue;
    private TextView tvEstimatedAmountValue;
    private TextView tvExpectedByValue;

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
        farmRepository = new FarmRepository();

        cropInfoCard = view.findViewById(R.id.cropInfoCard);
        timelineSection = view.findViewById(R.id.timelineSection);
        emptyHarvestState = view.findViewById(R.id.emptyHarvestState);
        spinnerHarvestFarm = view.findViewById(R.id.spinnerHarvestFarm);
        tvCropIdValue = view.findViewById(R.id.tvCropIdValue);
        tvCropFieldValue = view.findViewById(R.id.tvCropFieldValue);
        tvSelectedFarm = view.findViewById(R.id.tvSelectedFarm);
        tvSelectedHarvest = view.findViewById(R.id.tvSelectedHarvest);
        tvStepScheduledDate = view.findViewById(R.id.tvStepScheduledDate);
        tvStepHarvestDoneDate = view.findViewById(R.id.tvStepHarvestDoneDate);
        tvStageScheduledStatus = view.findViewById(R.id.tvStageScheduledStatus);
        tvStageHarvestDoneStatus = view.findViewById(R.id.tvStageHarvestDoneStatus);
        tvStageWeightStatus = view.findViewById(R.id.tvStageWeightStatus);
        tvStagePaymentStatus = view.findViewById(R.id.tvStagePaymentStatus);
        tvNetWeightValue = view.findViewById(R.id.tvNetWeightValue);
        tvEstimatedAmountValue = view.findViewById(R.id.tvEstimatedAmountValue);
        tvExpectedByValue = view.findViewById(R.id.tvExpectedByValue);

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            emptyHarvestState.setVisibility(View.VISIBLE);
            cropInfoCard.setVisibility(View.GONE);
            timelineSection.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        farmRepository.getFarmsByFarmerId(uid).observe(getViewLifecycleOwner(), farmsResult -> {
            farms = farmsResult == null ? new ArrayList<>() : farmsResult;
            updateFarmSelector();
            bindScreen();
        });

        harvestRepository.getHarvestsByFarmerId(uid).observe(getViewLifecycleOwner(), harvestsResult -> {
            harvests = harvestsResult == null ? new ArrayList<>() : harvestsResult;
            bindScreen();
        });
    }

    private void updateFarmSelector() {
        if (spinnerHarvestFarm == null) {
            return;
        }

        List<String> farmLabels = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < farms.size(); i++) {
            Farm farm = farms.get(i);
            farmLabels.add(formatFarmName(farm));
            if (selectedFarmId != null && selectedFarmId.equals(farm.getFarmId())) {
                selectedIndex = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                farmLabels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHarvestFarm.setAdapter(adapter);

        if (!farms.isEmpty()) {
            if (selectedFarmId == null) {
                selectedFarmId = farms.get(0).getFarmId();
            }
            spinnerHarvestFarm.setSelection(selectedIndex);
        }

        spinnerHarvestFarm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < farms.size()) {
                    selectedFarmId = farms.get(position).getFarmId();
                    bindScreen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void bindScreen() {
        if (farms.isEmpty()) {
            if (spinnerHarvestFarm != null) {
                spinnerHarvestFarm.setVisibility(View.GONE);
            }
            cropInfoCard.setVisibility(View.GONE);
            timelineSection.setVisibility(View.GONE);
            emptyHarvestState.setVisibility(View.VISIBLE);
            return;
        }

        if (spinnerHarvestFarm != null) {
            spinnerHarvestFarm.setVisibility(View.VISIBLE);
        }

        cropInfoCard.setVisibility(View.VISIBLE);
        timelineSection.setVisibility(View.VISIBLE);
        emptyHarvestState.setVisibility(View.GONE);

        Farm selectedFarm = findSelectedFarm();
        if (selectedFarm == null) {
            selectedFarm = farms.get(0);
            selectedFarmId = selectedFarm.getFarmId();
        }

        tvCropFieldValue.setText(valueOrDash(selectedFarm.getCropType()));
        tvSelectedFarm.setText(formatFarmName(selectedFarm));

        Harvest selectedHarvest = findHarvestByFarmId(selectedFarm.getFarmId());
        if (selectedHarvest == null) {
            tvCropIdValue.setText("--");
            tvSelectedHarvest.setText(getString(R.string.harvest_label_format, "NONE"));
            setTimelineNone();
            tvStepScheduledDate.setText("--");
            tvStepHarvestDoneDate.setText("--");
            tvNetWeightValue.setText("--");
            tvEstimatedAmountValue.setText("--");
            tvExpectedByValue.setText("--");
            return;
        }

        tvCropIdValue.setText(valueOrDash(selectedHarvest.getHarvestId()));
        tvSelectedHarvest.setText(getString(R.string.harvest_label_format, valueOrDash(selectedHarvest.getStatus())));

        String dateText = formatDateOrDash(selectedHarvest.getHarvestDate());
        tvStepScheduledDate.setText(dateText);
        tvStepHarvestDoneDate.setText(dateText);

        if (selectedHarvest.getActualWeight() != null && selectedHarvest.getActualWeight() > 0) {
            tvNetWeightValue.setText(String.format(Locale.US, "%.1f Tons", selectedHarvest.getActualWeight()));
        } else {
            tvNetWeightValue.setText("--");
        }

        if (isNoneStatus(selectedHarvest.getStatus())) {
            setTimelineNone();
        } else {
            tvStageScheduledStatus.setText(getString(R.string.status_completed));
            tvStageHarvestDoneStatus.setText(getString(R.string.status_done));
            tvStageWeightStatus.setText(getString(R.string.status_verified));
            tvStagePaymentStatus.setText(getString(R.string.status_pending));
        }

        tvEstimatedAmountValue.setText("--");
        tvExpectedByValue.setText("--");
    }

    private boolean isNoneStatus(String status) {
        return status == null
                || status.trim().isEmpty()
                || "NONE".equalsIgnoreCase(status.trim())
                || "PLANNED".equalsIgnoreCase(status.trim());
    }

    private void setTimelineNone() {
        tvStageScheduledStatus.setText(getString(R.string.status_none));
        tvStageHarvestDoneStatus.setText(getString(R.string.status_none));
        tvStageWeightStatus.setText(getString(R.string.status_none));
        tvStagePaymentStatus.setText(getString(R.string.status_none));
    }

    private Farm findSelectedFarm() {
        if (selectedFarmId == null) {
            return null;
        }
        for (Farm farm : farms) {
            if (selectedFarmId.equals(farm.getFarmId())) {
                return farm;
            }
        }
        return null;
    }

    private Harvest findHarvestByFarmId(String farmId) {
        if (farmId == null) {
            return null;
        }
        for (Harvest harvest : harvests) {
            if (farmId.equals(harvest.getFarmId())) {
                return harvest;
            }
        }
        return null;
    }

    private String formatFarmName(Farm farm) {
        return valueOrDash(farm.getVillage()) + " - " + valueOrDash(farm.getSurveyNumber()) + " - " + valueOrDash(farm.getCropType());
    }

    private String valueOrDash(String value) {
        return (value == null || value.trim().isEmpty()) ? "--" : value.trim();
    }

    private String formatDateOrDash(Date date) {
        if (date == null) {
            return "--";
        }
        return new SimpleDateFormat("dd MMM yyyy", Locale.US).format(date);
    }
}
