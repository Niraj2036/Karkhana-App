package com.example.karkhanaapp.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Repository for managing file uploads and downloads from Firebase Storage.
 */
public class StorageRepository {
    private static final String TAG = "StorageRepository";

    private FirebaseStorage storage;
    private StorageReference storageReference;

    public StorageRepository() {
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    /**
     * Upload farm photo
     * @param farmId Farm ID
     * @param imageUri Local image URI
     * @param listener Completion listener
     */
    public void uploadFarmPhoto(String farmId, Uri imageUri, OnUploadCompleteListener listener) {
        String path = "farms/" + farmId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageReference.child(path);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            listener.onSuccess(uri.toString());
                            Log.d(TAG, "Farm photo uploaded: " + uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to upload farm photo", e);
                });
    }

    /**
     * Upload harvest document
     * @param harvestId Harvest ID
     * @param fileUri Local file URI
     * @param listener Completion listener
     */
    public void uploadHarvestDocument(String harvestId, Uri fileUri, OnUploadCompleteListener listener) {
        String path = "harvests/" + harvestId + "/" + System.currentTimeMillis() + ".pdf";
        StorageReference fileRef = storageReference.child(path);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            listener.onSuccess(uri.toString());
                            Log.d(TAG, "Harvest document uploaded: " + uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to upload harvest document", e);
                });
    }

    /**
     * Upload payment receipt
     * @param paymentId Payment ID
     * @param receiptUri Local file URI
     * @param listener Completion listener
     */
    public void uploadPaymentReceipt(String paymentId, Uri receiptUri, OnUploadCompleteListener listener) {
        String path = "payments/" + paymentId + "/" + System.currentTimeMillis() + ".pdf";
        StorageReference fileRef = storageReference.child(path);

        fileRef.putFile(receiptUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            listener.onSuccess(uri.toString());
                            Log.d(TAG, "Payment receipt uploaded: " + uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to upload payment receipt", e);
                });
    }

    /**
     * Upload profile photo
     * @param farmerId Farmer ID
     * @param photoUri Local photo URI
     * @param listener Completion listener
     */
    public void uploadProfilePhoto(String farmerId, Uri photoUri, OnUploadCompleteListener listener) {
        String path = "profiles/" + farmerId + "/profile.jpg";
        StorageReference fileRef = storageReference.child(path);

        fileRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            listener.onSuccess(uri.toString());
                            Log.d(TAG, "Profile photo uploaded: " + uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to upload profile photo", e);
                });
    }

    /**
     * Delete file from storage
     * @param filePath File path in storage
     * @param listener Completion listener
     */
    public void deleteFile(String filePath, OnDeleteCompleteListener listener) {
        storageReference.child(filePath)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess();
                    Log.d(TAG, "File deleted successfully");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to delete file", e);
                });
    }

    /**
     * Listener interface for upload operations
     */
    public interface OnUploadCompleteListener {
        void onSuccess(String downloadUrl);
        void onError(String error);
    }

    /**
     * Listener interface for delete operations
     */
    public interface OnDeleteCompleteListener {
        void onSuccess();
        void onError(String error);
    }
}

