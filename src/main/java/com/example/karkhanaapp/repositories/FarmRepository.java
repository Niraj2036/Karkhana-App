package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.Farm;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Repository for managing Farm data in Firestore.
 */
public class FarmRepository {
    private static final String TAG = "FarmRepository";
    private static final String FARMS_COLLECTION = "farms";

    private FirebaseFirestore firestore;

    public FarmRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new farm
     */
    public void createFarm(Farm farm, OnCompleteListener listener) {
        firestore.collection(FARMS_COLLECTION)
                .add(farm)
                .addOnSuccessListener(documentReference -> {
                    farm.setFarmId(documentReference.getId());
                    listener.onSuccess(farm);
                    Log.d(TAG, "Farm created with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create farm", e);
                });
    }

    /**
     * Get farm by ID
     */
    public LiveData<Farm> getFarmById(String farmId) {
        MutableLiveData<Farm> farmLiveData = new MutableLiveData<>();

        firestore.collection(FARMS_COLLECTION)
                .document(farmId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching farm", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Farm farm = snapshot.toObject(Farm.class);
                        farmLiveData.setValue(farm);
                    }
                });

        return farmLiveData;
    }

    /**
     * Get all farms for a farmer
     */
    public LiveData<List<Farm>> getFarmsByFarmerId(String farmerId) {
        MutableLiveData<List<Farm>> farmsLiveData = new MutableLiveData<>();

        firestore.collection(FARMS_COLLECTION)
                .whereEqualTo("farmerId", farmerId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching farms", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<Farm> farms = snapshot.toObjects(Farm.class);
                        farmsLiveData.setValue(farms);
                    }
                });

        return farmsLiveData;
    }

    /**
     * Update farm
     */
    public void updateFarm(String farmId, Farm farm, OnCompleteListener listener) {
        firestore.collection(FARMS_COLLECTION)
                .document(farmId)
                .set(farm)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Farm updated successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to update farm", e);
                });
    }

    /**
     * Delete farm
     */
    public void deleteFarm(String farmId, OnCompleteListener listener) {
        firestore.collection(FARMS_COLLECTION)
                .document(farmId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Farm deleted successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to delete farm", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(Farm farm);
        void onError(String error);
    }
}

