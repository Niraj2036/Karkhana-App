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

public class NearbyFactoriesFragment extends Fragment {

    private static final String FACTORY_MAP_URL = "https://www.openstreetmap.org/export/embed.html?bbox=74.1%2C15.1%2C75.7%2C16.9&layer=mapnik";

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

        WebSettings settings = nearbyMapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        nearbyMapWebView.setWebViewClient(new WebViewClient());
        nearbyMapWebView.loadUrl(FACTORY_MAP_URL);

        btnEnrollFactory1.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory1_name)));
        btnEnrollFactory2.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory2_name)));
        btnEnrollFactory3.setOnClickListener(v -> showFarmSelectionDialog(getString(R.string.factory3_name)));
    }

    private void showFarmSelectionDialog(String factoryName) {
        String[] options = {
                "Farm A - Athani, Survey 142/2A | Harvest: Early Season",
                "Farm B - Kagwad, Survey 88/1 | Harvest: Main Season"
        };

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

                    String farmName = selectedIndex[0] == 0 ? "Farm A" : "Farm B";
                    String harvestName = selectedIndex[0] == 0 ? "Early Season" : "Main Season";
                    EnrollmentState.setEnrollment(requireContext(), farmName + " - " + factoryName, harvestName);
                    Toast.makeText(requireContext(), R.string.enroll_success, Toast.LENGTH_SHORT).show();

                    if (requireActivity() instanceof MainAppActivity) {
                        ((MainAppActivity) requireActivity()).switchToHarvestTab();
                    }
                })
                .show();
    }
}
