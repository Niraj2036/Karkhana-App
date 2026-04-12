package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.Profile;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Repository for managing Profile data in Firestore.
 */
public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private static final String PROFILES_COLLECTION = "profiles";

    private FirebaseFirestore firestore;

    public ProfileRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create or update a profile
     */
    public void saveProfile(Profile profile, OnCompleteListener listener) {
        firestore.collection(PROFILES_COLLECTION)
                .document(profile.getFarmerId())
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(profile);
                    Log.d(TAG, "Profile saved successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to save profile", e);
                });
    }

    /**
     * Get profile by farmer ID
     */
    public LiveData<Profile> getProfileByFarmerId(String farmerId) {
        MutableLiveData<Profile> profileLiveData = new MutableLiveData<>();

        firestore.collection(PROFILES_COLLECTION)
                .document(farmerId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching profile", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Profile profile = snapshot.toObject(Profile.class);
                        profileLiveData.setValue(profile);
                    }
                });

        return profileLiveData;
    }

    /**
     * Update profile photo URL
     */
    public void updateProfilePhotoUrl(String farmerId, String photoUrl, OnCompleteListener listener) {
        firestore.collection(PROFILES_COLLECTION)
                .document(farmerId)
                .update("profilePhotoUrl", photoUrl)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Profile photo updated");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to update profile photo", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(Profile profile);
        void onError(String error);
    }
}

