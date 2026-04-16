package com.example.karkhanaapp.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.karkhanaapp.models.Harvest;
import com.example.karkhanaapp.utils.NotificationUtils;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardSyncWorker extends Worker {

    private static final String TAG = "DashboardSyncWorker";
    private static final String UNIQUE_ONCE_NAME = "dashboard_sync_once";
    private static final String UNIQUE_PERIODIC_NAME = "dashboard_sync_periodic";
    private static final long SYNC_INTERVAL_MINUTES = 1L;
    private static final String PREFS_NAME = "dashboard_sync_prefs";
    private static final String KEY_LAST_FARM_COUNT = "last_farm_count";
    private static final String KEY_LAST_HARVEST_COUNT = "last_harvest_count";
    private static final String INPUT_FORCE_NOTIFY = "force_notify";

    public DashboardSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean forceNotify = getInputData().getBoolean(INPUT_FORCE_NOTIFY, false);
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Log.d(TAG, "No logged in user yet; retrying sync loop");
            enqueueNextRun(getApplicationContext(), forceNotify, SYNC_INTERVAL_MINUTES, ExistingWorkPolicy.APPEND_OR_REPLACE);
            return Result.success();
        }

        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            List<String> farmIds = new ArrayList<>();
            int farmCount = Tasks.await(
                            .whereEqualTo("farmerId", uid)
                            .get()
            )) {
                String farmId = farmDoc.getId();
                if (farmId != null && !farmId.trim().isEmpty()) {
                    farmIds.add(farmId);
            ).size();
                        firestore.collection("harvests")
                                .whereEqualTo("farmId", farmId)
                                .get()
            List<Harvest> harvests = Tasks.await(
                    firestore.collection("harvests")
                            .whereEqualTo("farmerId", uid)
                            .get()
            ).toObjects(Harvest.class);

            SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int lastFarmCount = prefs.getInt(KEY_LAST_FARM_COUNT, -1);
            int lastHarvestCount = prefs.getInt(KEY_LAST_HARVEST_COUNT, -1);
            boolean changed = lastFarmCount != farmCount || lastHarvestCount != harvestCount;

            prefs.edit()
                    .putInt(KEY_LAST_FARM_COUNT, farmCount)
                    .putInt(KEY_LAST_HARVEST_COUNT, harvestCount)
                    .apply();

            if (changed || forceNotify) {
                String message;
                if (farmCount == 0) {
                    message = "No farms added yet. Add a farm to start tracking updates.";
                } else if (hasPendingHarvest) {
                    message = "You have " + farmCount + " farm(s) and " + harvestCount + " harvest record(s). One or more timelines are still at NONE.";
                } else if (forceNotify) {
                    message = "Background sync is active. No new changes found.";
                } else {
                    message = "Dashboard synced: " + farmCount + " farm(s), " + harvestCount + " harvest record(s).";
                }

                NotificationUtils.showNotification(
                        getApplicationContext(),
                        "Karkhana sync update",
                        message,
                        2001
                );
            }

            enqueueNextRun(getApplicationContext(), false, SYNC_INTERVAL_MINUTES, ExistingWorkPolicy.APPEND_OR_REPLACE);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Background sync failed", e);
            enqueueNextRun(getApplicationContext(), false, SYNC_INTERVAL_MINUTES, ExistingWorkPolicy.APPEND_OR_REPLACE);
            return Result.retry();
        }
    }

    public static void schedule(Context context) {
        // Replace any stale one-time chain so the first forced notification can fire now.
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC_NAME);
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_ONCE_NAME);
        enqueueNextRun(context, true, 0, ExistingWorkPolicy.REPLACE);
    }

    private static void enqueueNextRun(Context context, boolean forceNotify, long delayMinutes, ExistingWorkPolicy policy) {
        Data input = new Data.Builder()
                .putBoolean(INPUT_FORCE_NOTIFY, forceNotify)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DashboardSyncWorker.class)
                .setInputData(input)
                .setConstraints(constraints)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_ONCE_NAME, policy, request);
    }
}



