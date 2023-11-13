package com.example.humanspet;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessage extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            FCMNotificationHandler.showNotification(this, title, body);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // 여기서 알림 메시지를 처리하거나 원하는 동작을 수행합니다
        }
    }

}
