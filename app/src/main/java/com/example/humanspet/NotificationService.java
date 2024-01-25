package com.example.humanspet;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationService extends IntentService {
    String TAG = "노티";
    private SharedPreferences preferences;


    public NotificationService() {
        super("YourNotificationUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && "BUTTON_CLICK_ACTION".equals(intent.getAction())) {
            Log.d(TAG, "onHandleIntent: 들어옴");
            Intent btnIntent = new Intent("btn-click");
            btnIntent.putExtra("btnClick", "버튼 누름");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(btnIntent);
            updateNotificationText();
        }
    }

    private void updateNotificationText() {
        // 여기서 노티피케이션의 텍스트 업데이트 로직을 수행
        // 예: remoteViews.setTextViewText(...)
    }

}