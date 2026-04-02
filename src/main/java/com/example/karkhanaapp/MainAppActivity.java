package com.example.karkhanaapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAppActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        bottomNav = findViewById(R.id.bottomNav);

        // Set active color programmatically
        bottomNav.setItemIconTintList(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_inactive, null)));
        bottomNav.setItemTextColor(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.nav_inactive, null)));

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

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
                return true;
            }
            return false;
        });
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
