package com.example.humanspet;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import java.util.ArrayList;

public class Walking extends Fragment implements OnMapReadyCallback {
    String TAG = "산책";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private MapFragment mapView;
    private Button startBtn;
    private TextView timeText,distanceText,speedText,calorieText;
    private boolean isRunning = false;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private final Handler handler = new Handler();
    private Runnable timerRunnable;
    private NaverMap naverMap;
    private Marker marker;
    ArrayList<LatLng> overLay = new ArrayList<>();
    LatLng initialLocation;
    int totalDistance=0;
    double xDistance;
    double yDistance;
    int count=0;
    int timeCount=0;
    int totalCalorie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment4, container, false);

        startBtn = v.findViewById(R.id.walkingStartButton);
        timeText = v.findViewById(R.id.walkingTime);
        distanceText=v.findViewById(R.id.walkingDistance);
        speedText=v.findViewById(R.id.walkingSpeed);
        calorieText=v.findViewById(R.id.walkingCalories);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    // 타이머 정지
                    isRunning = false;
                    handler.removeCallbacks(timerRunnable);
                    startBtn.setText("시작");
                } else {
                    // 타이머 시작 또는 재개
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
                getLocation();
                handler.postDelayed(this, 1000); // 1초마다 실행
                if(seconds>1){
                    PathOverlay pathOverlay = new PathOverlay();
                    pathOverlay.setCoords(overLay);
                    pathOverlay.setMap(naverMap);
                }
            }
        };

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // 위치 권한을 요청
        requestLocationPermission();

        mapView = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.onResume();
        mapView.getMapAsync(this);

        return v;
    }

    private void startTimer() {
        handler.post(timerRunnable);
    }

    private void updateTimerText() {
        timeText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 부여되었으면 위치 정보를 얻습니다.
            getLocation();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 정보를 가져오는 작업을 수행
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // 위치 정보를 얻은 경우
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                if(count>=1){
                                    totalDistance+=distance(xDistance,yDistance,latitude,longitude);
                                    double speed=distance(xDistance,yDistance,latitude,longitude);
                                    distanceText.setText(Integer.toString(totalDistance)+"m");
                                    speedText.setText(Integer.toString((int)speed*3600/1000)+"km/h");
                                    int calorie=(int)speed*17*70/1/60/100;
                                    totalCalorie+=calorie;
                                    calorieText.setText(Integer.toString(totalCalorie));
                                    Log.d(TAG, "onSuccess: "+totalDistance);
                                }
                                xDistance=latitude;
                                yDistance=longitude;
                                count++;

                                initialLocation = new LatLng(latitude, longitude);
                                overLay.add(initialLocation);
                                updateLocationOnMap(latitude, longitude);
                                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                            }
                        }
                    });
        }
    }

    private void updateLocationOnMap(double latitude, double longitude) {
        if (naverMap != null) {
            // 이전 마커를 제거하고 새로운 마커를 추가
            if (marker != null) {
                marker.setMap(null); // 이전 마커 제거
            }

            marker = new Marker();
            marker.setPosition(new LatLng(latitude, longitude));
            marker.setMap(naverMap);

            // 지도를 새로운 위치로 이동
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
            naverMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 부여되면 위치 정보를 얻습니다.
                getLocation();
            }
        }
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
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(timerRunnable);
    }
}
