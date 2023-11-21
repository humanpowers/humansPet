package com.example.humanspet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessage extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    private SharedPreferences preferences;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            preferences= getSharedPreferences("USER",MODE_PRIVATE);
            String name = preferences.getString("USERNAME","");
            String id= preferences.getString("USERID","");
            String[] noticeboardTitleSp= body.split("[\\(\\)]");


            Intent intent = new Intent(this,NoticeboardShow.class);
            intent.putExtra("name",name);
            intent.putExtra("id",id);
            intent.putExtra("title",noticeboardTitleSp[1]);
            intent.putExtra("type","push");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            FCMNotificationHandler.showNotification(this, title, body,intent);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }
    }

}
