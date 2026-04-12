package com.example.karkhanaapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.models.Farm;
import com.example.karkhanaapp.repositories.FarmRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NearbyFactoriesFragment extends Fragment {

    private static final String FACTORY_MAP_URL = "https://www.openstreetmap.org/export/embed.html?bbox=74.1%2C15.1%2C75.7%2C16.9&layer=mapnik";
    private final List<Farm> userFarms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_factories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebView nearbyMapWebView = view.findViewById(R.id.nearbyMapWebView);
        Button btnEnrollFactory1 = view.findViewById(R.id.btnEnrollFactory1);
        Button btnEnrollFactory2 = view.findViewById(R.id.btnEnrollFactory2);
        Button btnEnrollFactory3 = view.findViewById(R.id.btnEnrollFactory3);
        view.findViewById(R.id.btnFactoriesBack).setOnClickListener(v -> {
            if (requireActivity() instanceof MainAppActivity) {
                ((MainAppActivity) requireActivity()).switchToHarvestTab();
            }
        });
        view.findViewById(R.id.btnFactoriesFilter).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Filter coming soon", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.btnZoomIn).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Zoom in not supported in embed view", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.btnZoomOut).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Zoom out not supported in embed view", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.btnMyLocation).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Using saved farm location", Toast.LENGTH_SHORT).show());

        WebSettings settings = nearbyMapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        nearbyMapWebView.setWebViewClient(new WebViewClient());
        nearbyMapWebView.loadUrl(FACTORY_MAP_URL);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            new FarmRepository().getFarmsByFarmerId(uid).observe(getViewLifecycleOwner(), farms -> {
                userFarms.clear();
                if (farms != null) {
                    userFarms.addAll(farms);
                }
            });
        }

        btnEnrollFactory1.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory1_name)));
        btnEnrollFactory2.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory2_name)));
        btnEnrollFactory3.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory3_name)));
    }

    private void showFarmSelectionDialog(String factoryName) {
        if (userFarms.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one farm first", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = new String[userFarms.size()];
        for (int i = 0; i < userFarms.size(); i++) {
            Farm farm = userFarms.get(i);
            options[i] = farm.getVillage() + " • Survey " + farm.getSurveyNumber() + " • " + farm.getCropType();
        }

        final int[] selectedIndex = {-1};

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_farm_title))
                .setSingleChoiceItems(options, -1, (dialog, which) -> selectedIndex[0] = which)
                .setNegativeButton(R.string.enroll_cancel, null)
                .setPositiveButton(R.string.enroll_confirm, (dialog, which) -> {
                    if (selectedIndex[0] < 0) {
                        Toast.makeText(requireContext(), R.string.enroll_pick_farm, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Farm selectedFarm = userFarms.get(selectedIndex[0]);
                    String farmName = selectedFarm.getVillage() + " - " + selectedFarm.getSurveyNumber();
                    String harvestName = selectedFarm.getCropType();
                    EnrollmentState.setEnrollment(requireContext(), farmName + " - " + factoryName, harvestName);
                    Toast.makeText(requireContext(), R.string.enroll_success, Toast.LENGTH_SHORT).show();

                    if (requireActivity() instanceof MainAppActivity) {
                        ((MainAppActivity) requireActivity()).switchToHarvestTab();
                    }
                })
                .show();
    }
}
