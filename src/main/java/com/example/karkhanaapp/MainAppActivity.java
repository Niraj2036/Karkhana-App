package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.utils.NotificationUtils;
import com.example.karkhanaapp.workers.DashboardSyncWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAppActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout fabContainer;
    private ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        NotificationUtils.ensureNotificationChannel(this);
        DashboardSyncWorker.schedule(this);

        bottomNav = findViewById(R.id.bottomNav);
        fabContainer = findViewById(R.id.fabContainer);
        fab = findViewById(R.id.fab);

        // Set active color programmatically
        bottomNav.setItemIconTintList(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_inactive, null)));
        bottomNav.setItemTextColor(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_inactive, null)));

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
            updateFabForTab(R.id.nav_home);
        }

        fab.setOnClickListener(v -> startActivity(new Intent(MainAppActivity.this, FarmLocationActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_harvest) {
                fragment = new HarvestTrackingFragment();
            } else if (itemId == R.id.nav_factories) {
                fragment = new NearbyFactoriesFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                // Update colors for selected item
                updateNavColors(itemId);
                updateFabForTab(itemId);
                return true;
            }
            return false;
        });

        updateFabForTab(bottomNav.getSelectedItemId());
    }

    public void switchToHarvestTab() {
        bottomNav.setSelectedItemId(R.id.nav_harvest);
    }

    private void updateFabForTab(int selectedId) {
        fabContainer.setVisibility(selectedId == R.id.nav_harvest ? View.VISIBLE : View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateNavColors(int selectedId) {
        int activeColor = getResources().getColor(R.color.primary, null);
        int inactiveColor = getResources().getColor(R.color.nav_inactive, null);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
        };
        int[] colors = new int[] { activeColor, inactiveColor };

        android.content.res.ColorStateList colorStateList =
                new android.content.res.ColorStateList(states, colors);

        bottomNav.setItemIconTintList(colorStateList);
        bottomNav.setItemTextColor(colorStateList);
    }
}
