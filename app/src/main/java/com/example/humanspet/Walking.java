package com.example.humanspet;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class Walking extends Fragment implements OnMapReadyCallback {
    String TAG = "산책";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private MapView mapView;
    private Button startBtn;
    private TextView timeText,distanceText,speedText,calorieText;
    private boolean isRunning = false;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private final Handler handler = new Handler();
    private Runnable timerRunnable;
    private Marker marker;
    ArrayList<LatLng> overLay = new ArrayList<>();
    LatLng initialLocation;
    int totalDistance=0;
    double xDistance;
    double yDistance;
    int count;
    int timeCount=0;
    int totalCalorie;
    double lat;
    double lon;
    MapFragment mapFragment;
    Boolean timerBoolean;
    PathOverlay path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment4, container, false);

        startBtn = v.findViewById(R.id.walkingStartButton);
        timeText = v.findViewById(R.id.walkingTime);
        distanceText=v.findViewById(R.id.walkingDistance);
        speedText=v.findViewById(R.id.walkingSpeed);
        calorieText=v.findViewById(R.id.walkingCalories);

        path = new PathOverlay();

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mapFragment = (MapFragment) getParentFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getParentFragmentManager().beginTransaction().add(R.id.mapView, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        // 지도가 준비되면 onMapReady 메서드가 호출됩니다.



        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    // 타이머 정지
                    isRunning = false;
                    handler.removeCallbacks(timerRunnable);
                    startBtn.setText("시작");
                } else {
                    count=0;
                    timeCount=0;
                    hours=0;
                    minutes=0;
                    seconds=0;
                    totalDistance=0;
                    totalCalorie=0;
                    // 타이머 시작 또는 재개
                    path.setMap(null);
                    overLay.clear();
                    isRunning = true;
                    startBtn.setText("정지");
                    startTimer();
                }
            }
        });

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeCount++;
                seconds++;
                if (seconds >= 60) {
                    seconds = 0;
                    minutes++;
                    if (minutes >= 60) {
                        minutes = 0;
                        hours++;
                    }
                }
                updateTimerText();
                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };



        return v;
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart호출");
        super.onStart();
        count=0;


    }


    private void startTimer() {
        handler.post(timerRunnable);
    }

    private void updateTimerText() {
        timeText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60*1.1515*1609.344;

        return dist; //단위 meter
    }

    private static double deg2rad(double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private static double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop호출");
        handler.removeCallbacks(timerRunnable);
        mapFragment.onStop();
        overLay.clear();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "onMapReady");

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                lat=location.getLatitude();
                lon=location.getLongitude();
                if(isRunning){
                    overLay.add(new LatLng(location.getLatitude(),location.getLongitude()));
                    if(count>=1){
                        path.setCoords(overLay);
                        path.setMap(naverMap);

                        totalDistance+=distance(xDistance,yDistance,lat,lon);
                        double speed=distance(xDistance,yDistance,lat,lon);
                        distanceText.setText(Integer.toString(totalDistance)+"m");
                        speedText.setText(Integer.toString((int)speed*3600/1000)+"km/h");
                        int calorie=(int)speed*17*70/1/60/100;
                        totalCalorie+=calorie;
                        calorieText.setText(Integer.toString(totalCalorie));
                        Log.d(TAG, "onSuccess: "+totalDistance);
                    }
                    xDistance=lat;
                    yDistance=lon;
                    count++;
                }


                Log.d(TAG, "onLocationChange: lat: "+lat+" lon: "+lon +"정확도 : "+location.getAccuracy());
            }
        });
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

}
