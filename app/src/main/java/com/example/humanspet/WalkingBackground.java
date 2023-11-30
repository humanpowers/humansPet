package com.example.humanspet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.util.FusedLocationSource;

public class WalkingBackground extends Service {

    NaverMap naverMap;
    private FusedLocationSource locationSource;
    double lat,lon;

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;

    public WalkingBackground() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                lat=location.getLatitude();
                lon=location.getLongitude();
            }
        });

        throw new UnsupportedOperationException("Not yet implemented");
    }

//    @Override
//    public int onStartCommand(Intent intent,int flags, int startId){
//        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        myServiceHandler handler = new myServiceHandler();
//        thread = new ServiceThread(handler);
//        thread.start();
//        return START_STICKY;
//    }
}