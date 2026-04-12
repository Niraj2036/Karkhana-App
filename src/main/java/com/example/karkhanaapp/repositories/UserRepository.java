package com.example.karkhanaapp.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.Farmer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Repository for managing user authentication and Farmer data in Firestore.
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private static final String FARMERS_COLLECTION = "farmers";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private MutableLiveData<FirebaseUser> currentUser;
    private MutableLiveData<String> authError;

    public UserRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = new MutableLiveData<>(firebaseAuth.getCurrentUser());
        this.authError = new MutableLiveData<>();
    }

    /**
     * Get current authenticated user
     */
    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    /**
     * Get authentication errors
     */
    public LiveData<String> getAuthError() {
        return authError;
    }

    /**
     * Sign in with Google and sync user data to Firestore
     */
    public void syncGoogleUserToFirestore(String userId, String email, String displayName) {
        Farmer farmer = new Farmer();
        farmer.setFarmerId(userId);
        farmer.setEmail(email);
        farmer.setName(displayName);

        firestore.collection(FARMERS_COLLECTION)
                .document(userId)
                .set(farmer)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Farmer data synced to Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync farmer data", e);
                    authError.setValue("Failed to sync user data: " + e.getMessage());
                });
    }

    /**
     * Get farmer data by ID
     */
    public LiveData<Farmer> getFarmerById(String farmerId) {
        MutableLiveData<Farmer> farmerLiveData = new MutableLiveData<>();

        firestore.collection(FARMERS_COLLECTION)
                .document(farmerId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching farmer", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Farmer farmer = snapshot.toObject(Farmer.class);
                        farmerLiveData.setValue(farmer);
                    }
                });

        return farmerLiveData;
    }

    /**
     * Update farmer profile
     */
    public void updateFarmer(String farmerId, Farmer farmer) {
        firestore.collection(FARMERS_COLLECTION)
                .document(farmerId)
                .set(farmer)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Farmer updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update farmer", e);
                    authError.setValue("Failed to update farmer: " + e.getMessage());
                });
    }

    /**
     * Sign out user
     */
    public void signOut() {
        firebaseAuth.signOut();
        currentUser.setValue(null);
        Log.d(TAG, "User signed out");
    }

    /**
     * Check if user is authenticated
     */
    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Get current user ID
     */
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}

