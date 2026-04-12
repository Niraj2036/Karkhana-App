package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.Payment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Repository for managing Payment data in Firestore.
 */
public class PaymentRepository {
    private static final String TAG = "PaymentRepository";
    private static final String PAYMENTS_COLLECTION = "payments";

    private FirebaseFirestore firestore;

    public PaymentRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new payment
     */
    public void createPayment(Payment payment, OnCompleteListener listener) {
        firestore.collection(PAYMENTS_COLLECTION)
                .add(payment)
                .addOnSuccessListener(documentReference -> {
                    payment.setPaymentId(documentReference.getId());
                    listener.onSuccess(payment);
                    Log.d(TAG, "Payment created with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create payment", e);
                });
    }

    /**
     * Get payment by ID
     */
    public LiveData<Payment> getPaymentById(String paymentId) {
        MutableLiveData<Payment> paymentLiveData = new MutableLiveData<>();

        firestore.collection(PAYMENTS_COLLECTION)
                .document(paymentId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching payment", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Payment payment = snapshot.toObject(Payment.class);
                        paymentLiveData.setValue(payment);
                    }
                });

        return paymentLiveData;
    }

    /**
     * Get all payments for a harvest
     */
    public LiveData<List<Payment>> getPaymentsByHarvestId(String harvestId) {
        MutableLiveData<List<Payment>> paymentsLiveData = new MutableLiveData<>();

        firestore.collection(PAYMENTS_COLLECTION)
                .whereEqualTo("harvestId", harvestId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching payments", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<Payment> payments = snapshot.toObjects(Payment.class);
                        paymentsLiveData.setValue(payments);
                    }
                });

        return paymentsLiveData;
    }

    /**
     * Update payment
     */
    public void updatePayment(String paymentId, Payment payment, OnCompleteListener listener) {
        firestore.collection(PAYMENTS_COLLECTION)
                .document(paymentId)
                .set(payment)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Payment updated successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to update payment", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(Payment payment);
        void onError(String error);
    }
}

