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
import com.example.karkhanaapp.repositories.FarmRepository;
import com.example.karkhanaapp.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardFragment extends Fragment {

    private UserRepository userRepository;
    private FarmRepository farmRepository;

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

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            // Load and display user data
            userRepository.getFarmerById(uid).observe(getViewLifecycleOwner(), farmer -> {
                // User data loaded - can be used to update any dynamic UI elements
                if (farmer != null) {
                    // Update UI with farmer data
                }
            });

            // Load farms to display statistics
            farmRepository.getFarmsByFarmerId(uid).observe(getViewLifecycleOwner(), farms -> {
                // Farms data loaded
                if (farms != null) {
                    // Calculate stats if needed
                }
            });
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
