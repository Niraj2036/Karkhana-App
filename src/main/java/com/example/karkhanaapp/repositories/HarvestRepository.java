package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.Harvest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Repository for managing Harvest data in Firestore.
 */
public class HarvestRepository {
    private static final String TAG = "HarvestRepository";
    private static final String HARVESTS_COLLECTION = "harvests";

    private FirebaseFirestore firestore;

    public HarvestRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new harvest
     */
    public void createHarvest(Harvest harvest, OnCompleteListener listener) {
        firestore.collection(HARVESTS_COLLECTION)
                .add(harvest)
                .addOnSuccessListener(documentReference -> {
                    harvest.setHarvestId(documentReference.getId());
                    listener.onSuccess(harvest);
                    Log.d(TAG, "Harvest created with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create harvest", e);
                });
    }

    /**
     * Create or update harvest timeline for enrolled farm.
     */
    public void upsertEnrollmentHarvest(String farmerId, String farmId, String cropType, String sugarFactoryId, OnCompleteListener listener) {
        firestore.collection(HARVESTS_COLLECTION)
                .whereEqualTo("farmId", farmId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String harvestId = querySnapshot.getDocuments().get(0).getId();
                        Harvest existingHarvest = querySnapshot.getDocuments().get(0).toObject(Harvest.class);
                        if (existingHarvest == null) {
                            existingHarvest = new Harvest();
                        }
                        existingHarvest.setFarmId(farmId);
                        existingHarvest.setFarmerId(farmerId);
                        existingHarvest.setCropType(cropType);
                        existingHarvest.setSugarFactoryId(sugarFactoryId);
                        if (existingHarvest.getStatus() == null || existingHarvest.getStatus().trim().isEmpty()) {
                            existingHarvest.setStatus("NONE");
                        }
                        Harvest finalHarvest = existingHarvest;
                        firestore.collection(HARVESTS_COLLECTION)
                                .document(harvestId)
                                .set(finalHarvest)
                                .addOnSuccessListener(aVoid -> listener.onSuccess(finalHarvest))
                                .addOnFailureListener(e -> listener.onError(e.getMessage()));
                        return;
                    }

                    Harvest harvest = new Harvest();
                    harvest.setFarmId(farmId);
                    harvest.setFarmerId(farmerId);
                    harvest.setCropType(cropType);
                    harvest.setSugarFactoryId(sugarFactoryId);
                    harvest.setStatus("NONE");
                    harvest.setActualWeight(0.0);
                    harvest.setExpectedYield(0.0);

                    createHarvest(harvest, listener);
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    /**
     * Get harvest by ID
     */
    public LiveData<Harvest> getHarvestById(String harvestId) {
        MutableLiveData<Harvest> harvestLiveData = new MutableLiveData<>();

        firestore.collection(HARVESTS_COLLECTION)
                .document(harvestId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching harvest", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Harvest harvest = snapshot.toObject(Harvest.class);
                        harvestLiveData.setValue(harvest);
                    }
                });

        return harvestLiveData;
    }

    /**
     * Get all harvests for a farm
     */
    public LiveData<List<Harvest>> getHarvestsByFarmId(String farmId) {
        MutableLiveData<List<Harvest>> harvestsLiveData = new MutableLiveData<>();

        firestore.collection(HARVESTS_COLLECTION)
                .whereEqualTo("farmId", farmId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching harvests", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<Harvest> harvests = snapshot.toObjects(Harvest.class);
                        harvestsLiveData.setValue(harvests);
                    }
                });

        return harvestsLiveData;
    }

    /**
     * Get all harvests for a farmer (across all farms)
     */
    public LiveData<List<Harvest>> getHarvestsByFarmerId(String farmerId) {
        MutableLiveData<List<Harvest>> harvestsLiveData = new MutableLiveData<>();

        firestore.collection(HARVESTS_COLLECTION)
                .whereEqualTo("farmerId", farmerId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching harvests by farmer", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<Harvest> harvests = snapshot.toObjects(Harvest.class);
                        harvestsLiveData.setValue(harvests);
                    }
                });

        return harvestsLiveData;
    }

    /**
     * Update harvest
     */
    public void updateHarvest(String harvestId, Harvest harvest, OnCompleteListener listener) {
        firestore.collection(HARVESTS_COLLECTION)
                .document(harvestId)
                .set(harvest)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Harvest updated successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to update harvest", e);
                });
    }

    /**
     * Delete harvest
     */
    public void deleteHarvest(String harvestId, OnCompleteListener listener) {
        firestore.collection(HARVESTS_COLLECTION)
                .document(harvestId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Harvest deleted successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to delete harvest", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(Harvest harvest);
        void onError(String error);
    }
}

