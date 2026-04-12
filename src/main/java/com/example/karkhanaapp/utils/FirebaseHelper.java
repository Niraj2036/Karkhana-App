package com.example.karkhanaapp.utils;

import android.util.Log;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Firebase initialization and configuration utility.
 */
public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseHelper instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private FirebaseHelper() {
        initializeFirebase();
    }

    /**
     * Get singleton instance
     */
    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    /**
     * Initialize Firebase services
     */
    private void initializeFirebase() {
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();

            // Configure Firestore settings
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)  // Enable offline persistence
                    .build();
            firestore.setFirestoreSettings(settings);

            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase", e);
        }
    }

    /**
     * Get FirebaseAuth instance
     */
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    /**
     * Get Firestore instance
     */
    public FirebaseFirestore getFirestore() {
        return firestore;
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
        return firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
    }

    /**
     * Get current user email
     */
    public String getCurrentUserEmail() {
        return firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getEmail() : null;
    }

    /**
     * Sign out
     */
    public void signOut() {
        firebaseAuth.signOut();
        Log.d(TAG, "User signed out");
    }
}

