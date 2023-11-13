package com.example.humanspet;

public class FCMNotification {
    private String to;
    private FCMNotificationData data;

    public FCMNotification(String to, FCMNotificationData data) {
        this.to = to;
        this.data = data;
    }
}
