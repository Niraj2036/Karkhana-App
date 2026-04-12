package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.SugarFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Repository for managing SugarFactory data in Firestore.
 */
public class SugarFactoryRepository {
    private static final String TAG = "SugarFactoryRepository";
    private static final String FACTORIES_COLLECTION = "sugar_factories";

    private FirebaseFirestore firestore;

    public SugarFactoryRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Get all sugar factories
     */
    public LiveData<List<SugarFactory>> getAllFactories() {
        MutableLiveData<List<SugarFactory>> factoriesLiveData = new MutableLiveData<>();

        firestore.collection(FACTORIES_COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching factories", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<SugarFactory> factories = snapshot.toObjects(SugarFactory.class);
                        factoriesLiveData.setValue(factories);
                    }
                });

        return factoriesLiveData;
    }

    /**
     * Get factory by ID
     */
    public LiveData<SugarFactory> getFactoryById(String factoryId) {
        MutableLiveData<SugarFactory> factoryLiveData = new MutableLiveData<>();

        firestore.collection(FACTORIES_COLLECTION)
                .document(factoryId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching factory", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        SugarFactory factory = snapshot.toObject(SugarFactory.class);
                        factoryLiveData.setValue(factory);
                    }
                });

        return factoryLiveData;
    }

    /**
     * Get factories by location (nearby factories)
     */
    public LiveData<List<SugarFactory>> getNearbyFactories(String location) {
        MutableLiveData<List<SugarFactory>> factoriesLiveData = new MutableLiveData<>();

        firestore.collection(FACTORIES_COLLECTION)
                .whereEqualTo("location", location)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching nearby factories", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<SugarFactory> factories = snapshot.toObjects(SugarFactory.class);
                        factoriesLiveData.setValue(factories);
                    }
                });

        return factoriesLiveData;
    }

    /**
     * Create a new sugar factory (admin only)
     */
    public void createFactory(SugarFactory factory, OnCompleteListener listener) {
        firestore.collection(FACTORIES_COLLECTION)
                .add(factory)
                .addOnSuccessListener(documentReference -> {
                    factory.setFactoryId(documentReference.getId());
                    listener.onSuccess(factory);
                    Log.d(TAG, "Factory created with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create factory", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(SugarFactory factory);
        void onError(String error);
    }
}

