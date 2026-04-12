package com.example.karkhanaapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.karkhanaapp.models.HarvestTimeline;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Repository for managing HarvestTimeline data in Firestore.
 */
public class HarvestTimelineRepository {
    private static final String TAG = "HarvestTimelineRepository";
    private static final String TIMELINES_COLLECTION = "harvest_timelines";

    private FirebaseFirestore firestore;

    public HarvestTimelineRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new harvest timeline event
     */
    public void createTimelineEvent(HarvestTimeline timeline, OnCompleteListener listener) {
        firestore.collection(TIMELINES_COLLECTION)
                .add(timeline)
                .addOnSuccessListener(documentReference -> {
                    timeline.setTimelineId(documentReference.getId());
                    listener.onSuccess(timeline);
                    Log.d(TAG, "Timeline event created: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to create timeline event", e);
                });
    }

    /**
     * Get all timeline events for a harvest
     */
    public LiveData<List<HarvestTimeline>> getTimelinesByHarvestId(String harvestId) {
        MutableLiveData<List<HarvestTimeline>> timelineLiveData = new MutableLiveData<>();

        firestore.collection(TIMELINES_COLLECTION)
                .whereEqualTo("harvestId", harvestId)
                .orderBy("eventDate")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching timelines", e);
                        return;
                    }

                    if (snapshot != null) {
                        List<HarvestTimeline> timelines = snapshot.toObjects(HarvestTimeline.class);
                        timelineLiveData.setValue(timelines);
                    }
                });

        return timelineLiveData;
    }

    /**
     * Update a timeline event
     */
    public void updateTimelineEvent(String timelineId, HarvestTimeline timeline, OnCompleteListener listener) {
        firestore.collection(TIMELINES_COLLECTION)
                .document(timelineId)
                .set(timeline)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(null);
                    Log.d(TAG, "Timeline event updated");
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    Log.e(TAG, "Failed to update timeline event", e);
                });
    }

    /**
     * Listener interface for async operations
     */
    public interface OnCompleteListener {
        void onSuccess(HarvestTimeline timeline);
        void onError(String error);
    }
}

