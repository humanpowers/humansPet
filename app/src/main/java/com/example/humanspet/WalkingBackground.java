package com.example.humanspet;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class WalkingBackground extends Service {
    String TAG = "산책 백그라운드";
    double latitude;
    double longitude;
    ArrayList<CoordinateItem> coordinateArrayList=new ArrayList<>();
    RemoteViews custom_layout;
    NotificationCompat.Builder builder;


    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                Log.d(TAG,"현재위치 위도: "+ latitude + ", 경도: " + longitude);
                CoordinateItem coordinateItem = new CoordinateItem(latitude,longitude);
                coordinateArrayList.add(coordinateItem);
                Intent intent = new Intent("custom-action");
                intent.putExtra("coordinate", coordinateArrayList);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                custom_layout.setTextViewText(R.id.walkingNotificationDistance,Double.toString(latitude));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Context context = getApplicationContext();
        String packageName = context.getPackageName();
        custom_layout = new RemoteViews(packageName,R.layout.walking_notification);
        RemoteViews custom_layout_expanded = new RemoteViews(packageName,R.layout.walking_notification_expanded);

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.basic_profile);
        builder.setContentTitle("위치 서비스");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setCustomContentView(custom_layout);
        builder.setCustomBigContentView(custom_layout_expanded);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Foreground Service로 위치 업데이트 요청
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());

        // Foreground Service 시작
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        Log.d(TAG, "stopLocationService: Stopping");

        // 위치 업데이트 중지
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);

        // Foreground Service 정지
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}