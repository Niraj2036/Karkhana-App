package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.karkhanaapp.models.Farmer;
import com.example.karkhanaapp.models.Profile;
import com.example.karkhanaapp.repositories.ProfileRepository;
import com.example.karkhanaapp.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private UserRepository userRepository;
    private ProfileRepository profileRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository();
        profileRepository = new ProfileRepository();

        TextView tvProfileName = view.findViewById(R.id.tvProfileName);
        TextView tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        TextView tvProfileAadhaar = view.findViewById(R.id.tvProfileAadhaar);
        TextView tvProfileDistrict = view.findViewById(R.id.tvProfileDistrict);
        TextView tvProfileVillage = view.findViewById(R.id.tvProfileVillage);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRepository.getFarmerById(uid).observe(getViewLifecycleOwner(), farmer -> bindFarmer(farmer, tvProfileName, tvProfilePhone));
            profileRepository.getProfileByFarmerId(uid).observe(getViewLifecycleOwner(), profile ->
                    bindProfile(profile, tvProfileAadhaar, tvProfileDistrict, tvProfileVillage));
        }

        view.findViewById(R.id.rowEditProfile).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PersonalDetailsActivity.class)));
        view.findViewById(R.id.rowSettings).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Settings coming soon", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.rowHelp).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Support: contact admin", Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.rowAbout).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Karkhana v1", Toast.LENGTH_SHORT).show());

        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void bindFarmer(Farmer farmer, TextView nameView, TextView phoneView) {
        if (farmer == null) {
            return;
        }
        if (farmer.getName() != null && !farmer.getName().isEmpty()) {
            nameView.setText(farmer.getName());
        }
        if (farmer.getPhone() != null && !farmer.getPhone().isEmpty()) {
            phoneView.setText(farmer.getPhone());
        }
    }

    private void bindProfile(Profile profile, TextView aadhaarView, TextView districtView, TextView villageView) {
        if (profile == null) {
            return;
        }
        if (profile.getAadhaarMasked() != null && !profile.getAadhaarMasked().isEmpty()) {
            aadhaarView.setText(profile.getAadhaarMasked());
        }
        if (profile.getDistrict() != null && !profile.getDistrict().isEmpty()) {
            districtView.setText(profile.getDistrict());
        }
        if (profile.getVillage() != null && !profile.getVillage().isEmpty()) {
            villageView.setText(profile.getVillage());
        }
    }
}
