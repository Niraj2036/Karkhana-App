package com.example.karkhanaapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.karkhanaapp.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class KarkhanaFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "KarkhanaFCM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Karkhana update";
        String message = "You have a new update.";

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                message = remoteMessage.getNotification().getBody();
            }
        }

        if (remoteMessage.getData().containsKey("title")) {
            title = remoteMessage.getData().get("title");
        }
        if (remoteMessage.getData().containsKey("message")) {
            message = remoteMessage.getData().get("message");
        }

        NotificationUtils.showNotification(getApplicationContext(), title, message, (int) (System.currentTimeMillis() & 0x0FFFFFFF));
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM token refreshed: " + token);
    }
}

