package com.example.karkhanaapp.repositories;

import android.util.Log;

import com.example.karkhanaapp.models.OTPVerification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

/**
 * Repository for managing OTP verification in Firestore.
 */
public class OTPRepository {
    private static final String TAG = "OTPRepository";
    private static final String OTP_COLLECTION = "otp_verifications";

    private FirebaseFirestore firestore;

    public OTPRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create and store a new OTP
     */
    public void createOTP(String email, String otp, Date expiresAt, OnCompleteListener listener) {
        OTPVerification otpVerification = new OTPVerification(email, otp, expiresAt);

        firestore.collection(OTP_COLLECTION)
                .add(otpVerification)
                .addOnSuccessListener(documentReference -> {
                    otpVerification.setOtpId(documentReference.getId());
                    listener.onSuccess(otpVerification);
                    Log.d(TAG, "OTP created for email: " + email);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create OTP", e);
                });
    }

    /**
     * Verify OTP by email and OTP code
     */
    public void verifyOTP(String email, String otpCode, OnCompleteListener listener) {
        firestore.collection(OTP_COLLECTION)
                .whereEqualTo("email", email)
                .whereEqualTo("otp", otpCode)
                .whereGreaterThan("expiresAt", new Date())
                .whereEqualTo("isVerified", false)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.getDocuments().isEmpty()) {
                        listener.onError("Invalid or expired OTP");
                        return;
                    }

                    // Update OTP as verified
                    String docId = querySnapshot.getDocuments().get(0).getId();
                    firestore.collection(OTP_COLLECTION)
                            .document(docId)
                            .update("isVerified", true)
                            .addOnSuccessListener(aVoid -> {
                                OTPVerification otp = querySnapshot.getDocuments().get(0).toObject(OTPVerification.class);
                                listener.onSuccess(otp);
                                Log.d(TAG, "OTP verified for email: " + email);
                            })
                            .addOnFailureListener(e -> {
                                listener.onError("Failed to verify OTP: " + e.getMessage());
                                Log.e(TAG, "Failed to update OTP verification status", e);
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to verify OTP", e);
                });
    }

    /**
     * Check if email is verified
     */
    public void isEmailVerified(String email, OnVerifyCheckListener listener) {
        firestore.collection(OTP_COLLECTION)
                .whereEqualTo("email", email)
                .whereEqualTo("isVerified", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean isVerified = !querySnapshot.getDocuments().isEmpty();
                    listener.onResult(isVerified);
                    Log.d(TAG, "Email verification check for: " + email + " - " + isVerified);
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to check email verification", e);
                });
    }

    /**
     * Delete expired OTPs (admin/cleanup function)
     */
    public void deleteExpiredOTPs(OnCompleteListener listener) {
        firestore.collection(OTP_COLLECTION)
                .whereLessThan("expiresAt", new Date())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    querySnapshot.getDocuments().forEach(doc -> {
                        doc.getReference().delete();
                    });
                    listener.onSuccess(null);
                    Log.d(TAG, "Expired OTPs deleted");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to delete expired OTPs", e);
                });
    }

    /**
     * Listener interface for OTP operations
     */
    public interface OnCompleteListener {
        void onSuccess(OTPVerification otp);
        void onError(String error);
    }

    /**
     * Listener interface for verification check
     */
    public interface OnVerifyCheckListener {
        void onResult(boolean isVerified);
        void onError(String error);
    }
}

