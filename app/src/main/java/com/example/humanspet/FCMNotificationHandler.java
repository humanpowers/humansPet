package com.example.humanspet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class FCMNotificationHandler {
    private static final String CHANNEL_ID = "channel_id"; // 알림 채널 ID

    public static void showNotification(Context context, String title, String body, Intent intent) {
        // Notification Manager 생성
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Android Oreo 이상에서는 알림 채널을 설정해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Notification Builder 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.basic_profile) // 알림 아이콘
                .setContentTitle(title) // 알림 제목
                .setContentText(body) // 알림 내용
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // 알림 우선순위

        // Notification을 표시
        notificationManager.notify(0, builder.build());
    }
}
