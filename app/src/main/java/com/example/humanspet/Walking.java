package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryRecyclerViewInterface;
import com.example.humanspet.Interface.WalkingMarkerInterface;
import com.example.humanspet.Interface.WalkingPetInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Walking extends Fragment implements OnMapReadyCallback {
    String TAG = "산책";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
    private static final int REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION = 101;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private Button startBtn,saveBtn,selectBtn;
    private TextView timeText,distanceText,speedText,calorieText;
    boolean isRunning = false;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private Handler handler;
    private Runnable timerRunnable;
    ArrayList<LatLng> overLay = new ArrayList<>();
    double totalDistance=0.0;
    double xDistance;
    double yDistance;
    int count;
    int timeCount=0;
    double totalCalorie;
    double lat;
    double lon;
    MapFragment mapFragment;
    PathOverlay path;
    ArrayList<CoordinateItem> coordinateItems=new ArrayList<>();
    private SharedPreferences preferences,userPreferences;
    ConstraintLayout dialogView,markerDialog;
    ArrayList<DiaryInfoItem> diaryInfoItemArrayList = new ArrayList<>();
    DiaryInfoAdapter diaryInfoAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    String userId;
    ArrayList responseArray;
    int selectPosition=0;
    String petName;
    CircleImageView petImage;
    Bitmap mbitmap;
    View captureScreenShot;
    String result;
    double speed;
    double previousDistance;
    int petWeight;
    NotificationCompat.Builder builder;
    RemoteViews custom_layout;
    public MainPage mActivity;
    Context mContext;
    ArrayList<WalkingMarkerItem> markerItems;
    TextView markerDialogTitle,markerDialogContent;
    ImageView markerDialogImage;
    Button markerDialogCheckBtn,markerDialogMoveBtn;
    Marker marker;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment4, container, false);

        handler = new Handler();
        requestLocationPermission();
        startBtn = v.findViewById(R.id.walkingStartButton);
        timeText = v.findViewById(R.id.walkingTime);
        distanceText=v.findViewById(R.id.walkingDistance);
        speedText=v.findViewById(R.id.walkingSpeed);
        calorieText=v.findViewById(R.id.walkingCalories);
        saveBtn=v.findViewById(R.id.walkingSaveButton);
        petImage=v.findViewById(R.id.walkingSelectPetImage);

        preferences = getActivity().getSharedPreferences("RUNNING",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        userPreferences=getActivity().getSharedPreferences("USER",MODE_PRIVATE);
        userId=userPreferences.getString("USERID","");

        path = new PathOverlay();

        locationSource = new FusedLocationSource(this, REQUEST_CODE_LOCATION_PERMISSION);

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.mapView, mapFragment).commit();
        }

        // 맵이 준비되면 콜백 호출
        mapFragment.getMapAsync(this);

        isRunning=preferences.getBoolean("run",false);
        if(isRunning){
            timeText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            double speed=distance(xDistance,yDistance,lat,lon);
            distanceText.setText(String.format("%.0f",totalDistance)+"m");
            speedText.setText(Integer.toString((int)speed*3600/1000)+"km/h");
            if(totalCalorie<1.0){
                calorieText.setText(String.format("%.0f",totalCalorie*1000)+"cal");
            }else{
                calorieText.setText(String.format("%.2f",totalCalorie)+"kcal");
            }
            startBtn.setText("정지");
            startTimer();
        }else{
            startBtn.setText("시작");
        }


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning=preferences.getBoolean("run",false);
                if (isRunning) {
                    isRunning(editor);
                } else {
                    noRunning(editor);
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
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mAlertReceiver, new IntentFilter("custom-action")
        );

        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                btnReceiver, new IntentFilter("btn-click")
        );

        dialogView = (ConstraintLayout) View.inflate(mContext,R.layout.walking_pet_dialog,null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setView(dialogView);
        markerDialog = (ConstraintLayout) View.inflate(mContext,R.layout.walking_marker_dialog,null);
        AlertDialog.Builder markerDialogBuilder = new AlertDialog.Builder(mContext);
        markerDialogBuilder.setView(markerDialog);
        recyclerView = dialogView.findViewById(R.id.walkingDialogRecyclerView);
        linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        diaryInfoAdapter =new DiaryInfoAdapter(diaryInfoItemArrayList);
        recyclerView.setAdapter(diaryInfoAdapter);
        DiaryRecyclerViewInterface api = ApiClient.getApiClient().create(DiaryRecyclerViewInterface.class);
        Call<ArrayList> call= api.getUserDiary(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                if(response.body().size()==0){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                    dlg.setMessage("등록된 펫이 없습니다.");
                    dlg.setCancelable(false);
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().getSupportFragmentManager().beginTransaction().show(new Diary()).commit();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(new Walking()).commit();
                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavi); // 바텀 네비게이션뷰 ID를 넣어주세요
                            bottomNavigationView.setSelectedItemId(R.id.btn_fragmentB);
                        }
                    });
                    dlg.show();
                }else{
                    Boolean pet = preferences.getBoolean("pet",false);
                    if(pet==false){
                        responseArray=response.body();
                        for(int i=0;i<responseArray.size();i++){
                            String responseSt= String.valueOf(responseArray.get(i));
                            String[] responseSp=responseSt.split(", ");
                            String[] nameSp=responseSp[0].split("");
                            String nameSt="";
                            for(int j=1;j<responseSp[0].length();j++){
                                nameSt+=nameSp[j];
                            }
                            DiaryInfoItem diaryInfoItem =new DiaryInfoItem(responseSp[1],nameSt);
                            diaryInfoItemArrayList.add(diaryInfoItem);
                        }
                        AlertDialog dialog = dialogBuilder.create(); // AlertDialog로 변경
                        dialog.show();
                        dialog.setCancelable(false);

                        selectBtn=dialogView.findViewById(R.id.walkingDialogSelectButton);
                        selectBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ApiClient apiClient = new ApiClient();
                                String responseSt= String.valueOf(responseArray.get(selectPosition));
                                String[] responseSp=responseSt.split(", ");
                                Glide.with(getActivity()).load("http://"+apiClient.goUri(responseSp[1])).thumbnail(Glide.with(getActivity()).load(R.raw.loadinggif)).override(1000,1000).into(petImage);
                                dialog.dismiss();
                                String[] nameSp=responseSp[0].split("");
                                String nameSt="";
                                for(int j=1;j<responseSp[0].length();j++){
                                    nameSt+=nameSp[j];
                                }
                                petName=nameSt;
                                String[] petWeightSp = responseSp[4].split("");
                                petWeight= Integer.parseInt(petWeightSp[0]);
                                editor.putString("selectPet",petName);
                                editor.commit();
                                View mapView = getView().findViewById(R.id.mapView);
                                if (mapView != null) {
                                    mapView.setVisibility(View.VISIBLE);
                                }
                                mapReady();

                                for(int i=0;i<markerItems.size();i++){
                                    if(markerItems.get(i).getName().equals(petName)&&markerItems.get(i).getAddress()!=null){
                                        Log.d(TAG, "onClick: 몇번 들어오냐?");
                                        int finalI = i;
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Bitmap bitmap = Picasso.get().load("http://3.141.38.95"+markerItems.get(finalI).getImage()).get();
                                                    bitmap=getRoundedBitmap(bitmap);
                                                    // 이미지를 OverlayImage로 변환
                                                    OverlayImage overlayImage = OverlayImage.fromBitmap(bitmap);
                                                    // 마커에 이미지 적용
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
//                                                            marker.setMap(null);
                                                            marker = new Marker();
                                                            markers.add(marker);
                                                            marker.setWidth(100);
                                                            marker.setHeight(100);
                                                            marker.setPosition(new LatLng(Float.valueOf(markerItems.get(finalI).getLatitude()), Float.valueOf(markerItems.get(finalI).getLongitude())));
                                                            marker.setCaptionText(markerItems.get(finalI).getTitle());
                                                            marker.setCaptionAligns(Align.Top);
                                                            marker.setMap(naverMap);
                                                            marker.setCaptionOffset(10);
//                                                            marker.setIcon(overlayImage); // 이미지 적용
                                                            marker.setOnClickListener(new Overlay.OnClickListener() {
                                                                @Override
                                                                public boolean onClick(@NonNull Overlay overlay) {
                                                                    if(markerDialog.getParent()!=null){
                                                                        ViewGroup dialogParentView = (ViewGroup) markerDialog.getParent();
                                                                        dialogParentView.removeView(markerDialog);
                                                                    }
                                                                    AlertDialog markerAlertDialog = markerDialogBuilder.create(); // AlertDialog로 변경
                                                                    markerAlertDialog.show();
                                                                    markerAlertDialog.setCancelable(false);
                                                                    markerDialogTitle=markerAlertDialog.findViewById(R.id.markerDialogTitle);
                                                                    markerDialogContent=markerAlertDialog.findViewById(R.id.markerDialogContent);
                                                                    markerDialogImage=markerAlertDialog.findViewById(R.id.markerDialogImage);
                                                                    markerDialogCheckBtn=markerAlertDialog.findViewById(R.id.markerDialogCheckButton);
                                                                    markerDialogMoveBtn=markerAlertDialog.findViewById(R.id.markerDialogMoveButton);
                                                                    markerDialogTitle.setText(markerItems.get(finalI).getTitle());
                                                                    markerDialogContent.setText(markerItems.get(finalI).getContent());
                                                                    Glide.with(getActivity()).load("http://"+apiClient.goUri(markerItems.get(finalI).getImage())).thumbnail(Glide.with(getActivity()).load(R.raw.loadinggif)).override(200,200).into(markerDialogImage);
                                                                    markerDialogCheckBtn.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            markerAlertDialog.dismiss();
                                                                        }
                                                                    });

                                                                    markerDialogMoveBtn.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            Intent intent = new Intent(getActivity(),DiaryShow.class);
                                                                            intent.putExtra("title",markerItems.get(finalI).getTitle());
                                                                            intent.putExtra("content",markerItems.get(finalI).getContent());
                                                                            intent.putExtra("image",markerItems.get(finalI).getImage());
                                                                            startActivity(intent);
                                                                        }
                                                                    });

                                                                    return false;
                                                                }
                                                            });
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                }
                            }
                        });
                    }else{
                        mapReady();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });

        diaryInfoAdapter.setOnClickListener(new DiaryInfoAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                selectPosition=position;
            }
        });

        captureScreenShot = v.findViewById(R.id.mapView);
        captureScreenShot.setDrawingCacheEnabled(true);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imageFile=ImageUtils.persistImage(getContext(), mbitmap, "uploaded_file");
                Log.d(TAG, "onCreateView: "+result);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", imageFile.getName(), requestFile);

                WalkingPetInterface api = ApiClient.getApiClient().create(WalkingPetInterface.class);
                Call<String> call1 = api.walkingPet(userId,petName,distanceText.getText().toString(),timeText.getText().toString(),calorieText.getText().toString(), String.valueOf(result),body);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "onResponse: "+response.body());
                        if(response.body().equals("성공")){
                            Intent intent = new Intent(getContext(),WalkingDiary.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        petImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning){
                    Toast.makeText(mActivity, "산책이 진행중일때는 펫을 바꿀 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    diaryInfoItemArrayList.clear();
                    ViewGroup dialogParentView = (ViewGroup) dialogView.getParent();
                    dialogParentView.removeView(dialogView);
                    for(int i=0;i<responseArray.size();i++){
                        String responseSt= String.valueOf(responseArray.get(i));
                        String[] responseSp=responseSt.split(", ");
                        String[] nameSp=responseSp[0].split("");
                        String nameSt="";
                        for(int j=1;j<responseSp[0].length();j++){
                            nameSt+=nameSp[j];
                        }
                        DiaryInfoItem diaryInfoItem =new DiaryInfoItem(responseSp[1],nameSt);
                        diaryInfoItemArrayList.add(diaryInfoItem);
                    }

                    AlertDialog dialog = dialogBuilder.create(); // AlertDialog로 변경
                    dialog.show();
                    dialog.setCancelable(false);

                    selectBtn=dialogView.findViewById(R.id.walkingDialogSelectButton);
                    selectBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ApiClient apiClient = new ApiClient();
                            String responseSt= String.valueOf(responseArray.get(selectPosition));
                            String[] responseSp=responseSt.split(", ");
                            Glide.with(getActivity()).load("http://"+apiClient.goUri(responseSp[1])).thumbnail(Glide.with(getActivity()).load(R.raw.loadinggif)).override(1000,1000).into(petImage);
                            dialog.dismiss();
                            String[] nameSp=responseSp[0].split("");
                            String nameSt="";
                            for(int j=1;j<responseSp[0].length();j++){
                                nameSt+=nameSp[j];
                            }
                            petName=nameSt;
                            petWeight= Integer.parseInt(responseSp[4]);
                            editor.putString("selectPet",petName);
                            editor.commit();
                            View mapView = getView().findViewById(R.id.mapView);
                            if (mapView != null) {
                                mapView.setVisibility(View.VISIBLE);
                            }
                            mapReady();
                            for(int i=0;i<markers.size();i++){
                                Marker markerRemove = markers.get(i);
                                markerRemove.setMap(null);
                            }
                            markers.clear();

                            for(int i=0;i<markerItems.size();i++){
                                if(markerItems.get(i).getName().equals(petName)&&markerItems.get(i).getAddress()!=null){
                                    Log.d(TAG, "onClick: 몇번 들어오냐?");
                                    int finalI = i;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Bitmap bitmap = Picasso.get().load("http://3.141.38.95"+markerItems.get(finalI).getImage()).get();
                                                bitmap=getRoundedBitmap(bitmap);
                                                // 이미지를 OverlayImage로 변환
                                                OverlayImage overlayImage = OverlayImage.fromBitmap(bitmap);
                                                // 마커에 이미지 적용
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Marker marker = new Marker();
                                                        markers.add(marker);
                                                        marker.setWidth(100);
                                                        marker.setHeight(100);
                                                        marker.setPosition(new LatLng(Float.valueOf(markerItems.get(finalI).getLatitude()), Float.valueOf(markerItems.get(finalI).getLongitude())));
                                                        marker.setCaptionText(markerItems.get(finalI).getTitle());
                                                        marker.setCaptionAligns(Align.Top);
                                                        marker.setMap(naverMap);
                                                        marker.setCaptionOffset(10);
//                                                            marker.setIcon(overlayImage); // 이미지 적용
                                                        marker.setOnClickListener(new Overlay.OnClickListener() {
                                                            @Override
                                                            public boolean onClick(@NonNull Overlay overlay) {
                                                                if(markerDialog.getParent()!=null){
                                                                    ViewGroup dialogParentView = (ViewGroup) markerDialog.getParent();
                                                                    dialogParentView.removeView(markerDialog);
                                                                }
                                                                AlertDialog markerAlertDialog = markerDialogBuilder.create(); // AlertDialog로 변경
                                                                markerAlertDialog.show();
                                                                markerAlertDialog.setCancelable(false);
                                                                markerDialogTitle=markerAlertDialog.findViewById(R.id.markerDialogTitle);
                                                                markerDialogContent=markerAlertDialog.findViewById(R.id.markerDialogContent);
                                                                markerDialogImage=markerAlertDialog.findViewById(R.id.markerDialogImage);
                                                                markerDialogCheckBtn=markerAlertDialog.findViewById(R.id.markerDialogCheckButton);
                                                                markerDialogMoveBtn=markerAlertDialog.findViewById(R.id.markerDialogMoveButton);
                                                                markerDialogTitle.setText(markerItems.get(finalI).getTitle());
                                                                markerDialogContent.setText(markerItems.get(finalI).getContent());
                                                                Glide.with(getActivity()).load("http://"+apiClient.goUri(markerItems.get(finalI).getImage())).thumbnail(Glide.with(getActivity()).load(R.raw.loadinggif)).override(1000,1000).into(markerDialogImage);
                                                                markerDialogCheckBtn.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        markerAlertDialog.dismiss();
                                                                    }
                                                                });

                                                                markerDialogMoveBtn.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        Intent intent = new Intent(getActivity(),DiaryShow.class);
                                                                        intent.putExtra("title",markerItems.get(finalI).getTitle());
                                                                        intent.putExtra("content",markerItems.get(finalI).getContent());
                                                                        intent.putExtra("image",markerItems.get(finalI).getImage());
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                                return false;
                                                            }
                                                        });
                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    });
                }
            }
        });



        return v;
    }

    private final BroadcastReceiver btnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 들어왔을까요?");
            String btnClick = intent.getStringExtra("btnClick");
            Log.d(TAG, "onReceive: "+btnClick);
            preferences = getActivity().getSharedPreferences("RUNNING",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(btnClick.equals("버튼 누름")){
                if (isRunning) {
                    isRunning(editor);
                } else {
                    noRunning(editor);
                }
            }
        }
    };

    private final BroadcastReceiver mAlertReceiver = new BroadcastReceiver() {
        private PendingIntent getPendingIntent(Context context) {
            Intent intent = new Intent(context, NotificationService.class);
            intent.setAction("BUTTON_CLICK_ACTION");
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            coordinateItems = (ArrayList<CoordinateItem>) intent.getSerializableExtra("coordinate");
            isRunning = preferences.getBoolean("run", false);
            if (isRunning) {
                overLay.clear();
                totalDistance = 0.0;
                totalCalorie = 0.0;
                for (int i = 0; i < coordinateItems.size(); i++) {
                    double latitude = coordinateItems.get(i).getLatitude();
                    double longitude = coordinateItems.get(i).getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    overLay.add(latLng);
                    if (i >= 1) {
                        path.setCoords(overLay);
                        path.setMap(naverMap);
                        double distance = distance(coordinateItems.get(i - 1).getLatitude(), coordinateItems.get(i - 1).getLongitude(), latitude, longitude);
                        totalDistance += distance;
                        speed = distance(coordinateItems.get(i - 1).getLatitude(), coordinateItems.get(i - 1).getLongitude(), latitude, longitude);
                        distanceText.setText(String.format("%.0f", totalDistance) + "m");
                        speedText.setText(Integer.toString((int) speed * 3600 / 1000) + "km/h");
                        if (previousDistance != totalDistance) {
                            totalCalorie += 0.8 / 0.453 * petWeight / 3600;
                        }

                        if (totalCalorie < 1.0) {
                            calorieText.setText(String.format("%.0f", totalCalorie * 1000) + "cal");
                        } else {
                            calorieText.setText(String.format("%.2f", totalCalorie) + "kcal");
                        }
                    }
                    previousDistance = totalDistance;
                }
                LatLng[] bounds = getBounds(overLay);
                if (bounds != null && isAdded()) {
                    CameraUpdate cameraUpdate = CameraUpdate.fitBounds(LatLngBounds.from(bounds), 100); // 100은 여유 공간입니다.
                    naverMap.moveCamera(cameraUpdate);
                    naverMap.setMinZoom(10.0);
                }

                Intent serviceIntent = new Intent(mContext, WidgetUpdateService.class);
                if (getActivity() != null) {
                    serviceIntent.setAction("com.example.Widget");
                    serviceIntent.putExtra("distanceValue", totalDistance);
                    serviceIntent.putExtra("speedValue", (int) speed * 3600 / 1000);
                    serviceIntent.putExtra("timeValue", timeText.getText().toString());
                    serviceIntent.putExtra("calorieValue", totalCalorie);
                    getActivity().startService(serviceIntent);
                }
                String channelId = "location_notification_channel";
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                String packageName = context.getPackageName();
                custom_layout = new RemoteViews(packageName, R.layout.walking_notification);
                RemoteViews custom_layout_expanded = new RemoteViews(packageName, R.layout.walking_notification_expanded);

                Intent buttonIntent = new Intent("BUTTON_CLICK_ACTION");
                PendingIntent buttonPendingIntent = getPendingIntent(mContext);
                custom_layout_expanded.setOnClickPendingIntent(R.id.walkingNotificationExpandedBtn, buttonPendingIntent);


                Intent resultIntent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                builder = new NotificationCompat.Builder(mContext, channelId);
                builder.setSmallIcon(R.drawable.basic_profile);
                builder.setContentTitle("위치 서비스");
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(false);
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
                builder.setCustomContentView(custom_layout);
                builder.setCustomBigContentView(custom_layout_expanded);
                custom_layout.setTextViewText(R.id.walkingNotificationDistance, String.format("%.0f", totalDistance) + "m");
                if (totalCalorie < 1.0) {
                    custom_layout.setTextViewText(R.id.walkingNotificationCalories, String.format("%.0f", totalCalorie * 1000) + "cal");
                } else {
                    custom_layout.setTextViewText(R.id.walkingNotificationCalories, String.format("%.2f", totalCalorie) + "kcal");
                }
                custom_layout.setTextViewText(R.id.walkingNotificationTime,timeText.getText().toString());
                custom_layout_expanded.setTextViewText(R.id.walkingNotificationExpandedDistance, String.format("%.0f", totalDistance) + "m");
                if (totalCalorie < 1.0) {
                    custom_layout_expanded.setTextViewText(R.id.walkingNotificationExpandedCalories, String.format("%.0f", totalCalorie * 1000) + "cal");
                } else {
                    custom_layout_expanded.setTextViewText(R.id.walkingNotificationExpandedCalories, String.format("%.2f", totalCalorie) + "kcal");
                }
                custom_layout_expanded.setTextViewText(R.id.walkingNotificationExpandedTime,timeText.getText().toString());
                int notificationId = Constants.LOCATION_SERVICE_ID;
                if(mContext!=null){
                    notificationManager.notify(notificationId,builder.build());
                }else{
                    Log.d(TAG, "onReceive: mContext: null임");
                    return;
                }
            }

        }
    };








    public static class ImageUtils {
        public static File persistImage(Context context, Bitmap bitmap, String name) {
            File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir, name + ".png");

            OutputStream os;
            try {
                os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageFile;
        }
    }

    private LatLng[] getBounds(List<LatLng> pathPoints) {
        if (pathPoints.isEmpty()) {
            return null;
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = Double.MIN_VALUE;

        for (LatLng point : pathPoints) {
            minLat = Math.min(minLat, point.latitude);
            maxLat = Math.max(maxLat, point.latitude);
            minLng = Math.min(minLng, point.longitude);
            maxLng = Math.max(maxLng, point.longitude);
        }

        return new LatLng[]{new LatLng(maxLat, maxLng), new LatLng(minLat, minLng)};
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (WalkingBackground.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getContext(), WalkingBackground.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);
            }else{
                getActivity().startService(intent);
            }
            Toast.makeText(getContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getContext(), WalkingBackground.class);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("pet",false);
            editor.putBoolean("run",false);
            editor.commit();
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);
            }else{
                getActivity().startService(intent);
            }
            Toast.makeText(getContext(), "서비스 정지", Toast.LENGTH_SHORT).show();
        }
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
    public void onAttach(Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (MainPage) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart호출");
        super.onStart();
        mapFragment.onStart();
        count=0;
        Log.d(TAG, "onStart: 호출");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();
        Log.d(TAG, "onResume: 호출");

        WalkingMarkerInterface walkingMarkerInterface = ApiClient.getApiClient().create(WalkingMarkerInterface.class);
        Call<ArrayList<WalkingMarkerItem>> markerCall = walkingMarkerInterface.walkingMarker(userId);
        markerCall.enqueue(new Callback<ArrayList<WalkingMarkerItem>>() {
            @Override
            public void onResponse(Call<ArrayList<WalkingMarkerItem>> call, Response<ArrayList<WalkingMarkerItem>> response) {
                if (response.isSuccessful()) {
                    markerItems = response.body();

                } else {
                    Log.e(TAG, "onResponse: Error response, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WalkingMarkerItem>> call, Throwable t) {
                Log.d(TAG, "onFailure: ",t);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
        Log.d(TAG, "onPause: 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop호출");
        mapFragment.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isAppInForeground()) {
            // 앱이 완전히 종료된 경우에만 서비스를 종료
            preferences=getActivity().getSharedPreferences("RUNNING",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("run",false);
            editor.putBoolean("pet",false);
            editor.putString("selectPet","");
            editor.commit();
            stopLocationService();
            Intent serviceIntent = new Intent(getActivity(), WidgetUpdateService.class);
            if (getActivity() != null) {
                serviceIntent.setAction("com.example.Widget");
                serviceIntent.putExtra("distanceValue", 0.0);
                serviceIntent.putExtra("speedValue",0.0);
                serviceIntent.putExtra("timeValue","00:00:00");
                serviceIntent.putExtra("calorieValue",0.0);
                getActivity().startService(serviceIntent);
            }
        }
        Log.d(TAG, "onDestroy: 호출");
        mapFragment.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (mapFragment != null) {
            // MapFragment를 제거하여 지도 객체를 해제합니다.
            getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
        super.onDestroyView();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "onMapReady");

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        View mapView = getView().findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.setVisibility(View.GONE);
        }
    }


    private boolean isAppInForeground() {
        if (getActivity() != null) {
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();

            if (processInfoList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && Arrays.asList(processInfo.pkgList).contains(getActivity().getPackageName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 이미 허용되어 있음
            } else {
                // 위치 권한이 허용되어 있지 않음
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            }
        } else {
            // 안드로이드 버전이 마시멜로우 미만일 경우에는 바로 실행
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("권한확인", "onRequestPermissionsResult: 들어옴");
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("권한확인", "onRequestPermissionsResult: 1번");
                // 위치 권한이 허용되었을 때
                showAppUsageLimitedDialog();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("권한확인", "onRequestPermissionsResult: 2번");
                // 사용자가 권한 요청을 거부한 경우
                showPermissionExplanationDialog();
            } else {
                Log.d("권한확인", "Show App Usage Limited Dialog");
                // 사용자가 "앱 사용 중에만 허용"을 선택한 경우 또는 영구적으로 거부한 경우
                // 여기에서 추가적인 로직을 수행할 수 있음
                // 예를 들어 사용자에게 앱 사용 중에만 제한된 기능이 있다는 안내를 표시하고,
                // 추가적인 백그라운드 위치 권한을 요청하는 다이얼로그를 표시할 수 있음
                showAppUsageLimitedDialog();
            }
        }
    }


    private void showAppUsageLimitedDialog() {
        Log.d("권한확인", "showAppUsageLimitedDialog() called");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("앱 사용 중에만 위치 권한 허용")
                .setMessage("앱 사용 중에만 위치 기능을 이용할 수 있습니다. 추가로 위치(시작 후 홈버튼을 눌렀을때)를 수집하려면 위치 권한을 항상 허용해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 누르면 백그라운드 위치 권한을 요청
                        requestBackgroundLocationPermission();
                    }
                })
                .show();
    }

    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 권한 필요")
                .setMessage("이 앱은 정상적으로 작동하기 위해 위치 권한이 필요합니다. 위치 권한을 부여해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 누르면 권한 요청 다이얼로그를 다시 띄움
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 취소한 경우에 대한 처리
                        // 필요에 따라 추가 구현 가능
                    }
                })
                .show();
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 사용자에게 백그라운드 위치 정보 항상 허용 권한을 요청하는 코드를 추가
            // 따로 요청해야 하는 경우를 고려하여 ActivityCompat.requestPermissions() 또는 다른 적절한 방법을 사용합니다.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION);

        }
    }


    public void mapReady(){
        LinearLayout linearLayout = getView().findViewById(R.id.walkingLinear);
        Button startBtn= getView().findViewById(R.id.walkingStartButton);
        linearLayout.setVisibility(View.VISIBLE);
        startBtn.setVisibility(View.VISIBLE);
    }

    public void isRunning(SharedPreferences.Editor editor){
        editor.putBoolean("run",false);
        editor.putBoolean("pet",false);
        editor.commit();
        handler.removeCallbacks(timerRunnable);
        startBtn.setText("시작");
        stopLocationService();
        saveBtn.setVisibility(View.VISIBLE);
        naverMap.takeSnapshot(new NaverMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(@NonNull Bitmap bitmap) {
                mbitmap=bitmap;
            }
        });
        isRunning=false;
    }

    public void noRunning(SharedPreferences.Editor editor){
        saveBtn.setVisibility(View.GONE);
        count=0;
        timeCount=0;
        hours=0;
        minutes=0;
        seconds=0;
        totalDistance=0;
        totalCalorie=0;
        timeText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        double speed=distance(xDistance,yDistance,lat,lon);
        distanceText.setText(String.format("%.0f",totalDistance)+"m");
        speedText.setText(Integer.toString((int)speed*3600/1000)+"km/h");
        if(totalCalorie<1.0){
            calorieText.setText(String.format("%.0f",totalCalorie*1000)+"cal");
        }else{
            calorieText.setText(String.format("%.2f",totalCalorie)+"kcal");
        }
        // 타이머 시작 또는 재개
        path.setMap(null);
        overLay.clear();
        editor.putBoolean("run",true);
        editor.putBoolean("pet",true);
        editor.commit();
        isRunning=true;
        startBtn.setText("정지");
        startTimer();
        startLocationService();
        Date currentDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result = outputFormat.format(currentDate);
    }
    public Bitmap getRoundedBitmap(Bitmap bitmap) {
        // 비트맵 크기 설정
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 원형 비트맵 생성
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 원형 모양의 마스크 생성
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, paint);

        // 원형 모양의 마스크 위에 원본 비트맵을 그립니다.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

}
