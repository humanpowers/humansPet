package com.example.humanspet;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryDiaryAddInterface;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryAdd extends AppCompatActivity implements OnMapReadyCallback {
    String TAG="addDiary";
    ImageButton cancelBtn;
    EditText titleEdit,contentEdit;
    ImageButton imageAddBtn,checkBtn;
    private SharedPreferences preferences;
    String diaryDataUri;
    String userId,petName;
    LottieAnimationView animationView;
    ImageView selectImage;
    ImageButton selectMapBtn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
    private static final int REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION = 101;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private MapView diaryMap;
    Boolean mapBoolean;
    Boolean mapReady;
    TextView imageText,mapText,selectMapText;
    ActivityResultLauncher<Intent> diaryLauncher;
    String petImage;
    String address,latitude,longitude;
    Button selectAddressBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_add);
        mapReady=false;
        locationSource = new FusedLocationSource(this, REQUEST_CODE_LOCATION_PERMISSION);

        selectImage=findViewById(R.id.addDiarySelectImage);
        selectMapBtn = findViewById(R.id.addDiaryMapButton);
        imageText=findViewById(R.id.addDiaryImageText);
        mapText=findViewById(R.id.addDiaryMapText);
        selectMapText=findViewById(R.id.addDiarySelectMapText);
        selectAddressBtn=findViewById(R.id.addDiarySelectAddressButton);
        mapBoolean=false;
        address="";
        latitude="";
        longitude="";

        locationSource = new FusedLocationSource(this, REQUEST_CODE_LOCATION_PERMISSION);

        diaryMap = findViewById(R.id.diaryMap);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.diaryMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.diaryMap, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        Intent getIntent = getIntent();
        petName= getIntent.getStringExtra("petName");
        petImage= getIntent.getStringExtra("petImage");
        Log.d(TAG, "onCreate: petName: "+petName);
        Log.d(TAG, "onCreate: petImage: "+petImage);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        Log.d(TAG, "onCreate: userid: "+userId);

        diaryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        selectImage.setVisibility(View.VISIBLE);
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri diaryUri = intent.getData();
                        Log.e(TAG, "uri : " + diaryUri);
                        String diaryRealUri=getRealPathFromUri(diaryUri);
                        Log.e(TAG,"realUri"+diaryRealUri);
                        diaryDataUri=diaryRealUri;
                        Glide.with(this)
                                .load(diaryRealUri)
                                .into(selectImage);
                        contentEdit.setHeight(1000);
                    }
                });

        TextView textCountText=findViewById(R.id.textCountText);

        contentEdit=findViewById(R.id.addDiaryContentEdit);
        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                int lineCount = contentEdit.getLineCount();
                if(lineCount>30){
                    contentEdit.setText(text.substring(0,text.length()-1));
                    contentEdit.setSelection(contentEdit.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lineCount = contentEdit.getLineCount();
                String textCount = contentEdit.getText().toString();
                textCountText.setText(lineCount+"/30\n"+textCount.length()+"/500");

            }
        });

        imageAddBtn=findViewById(R.id.addDiaryImageButton);
        imageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        checkBtn=findViewById(R.id.addDiaryCheckButton);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                titleEdit=findViewById(R.id.addDiaryTitleEdit);
                String title=titleEdit.getText().toString();
                contentEdit=findViewById(R.id.addDiaryContentEdit);
                String content=contentEdit.getText().toString();
                int lineCount = contentEdit.getLineCount();
                if(lineCount>30){
                    Toast.makeText(DiaryAdd.this,"본문은 30줄까지만 입력 가능합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    animationView = findViewById(R.id.addDiaryLottie);
                    animationView.loop(true);
                    animationView.playAnimation();
                    animationView.setVisibility(View.VISIBLE);

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String todayDate=format.format(date);
                    Log.d(TAG, "오늘날짜 "+todayDate);

                    Log.d(TAG, "onClick: "+diaryDataUri);
                    File diaryDataFile= new File(diaryDataUri);
                    Log.d(TAG, "onClick: dataFile: "+diaryDataFile);
                    String diaryFileName=title+".jpg";
                    Log.d(TAG, "onClick: fileName: "+diaryFileName);

                    RequestBody diaryRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),diaryDataFile);
                    MultipartBody.Part diaryBody = MultipartBody.Part.createFormData("uploaded_file",diaryFileName,diaryRequestBody);
                    Log.d(TAG, "onClick: body: "+diaryBody);

                    DiaryDiaryAddInterface api = ApiClient.getApiClient().create(DiaryDiaryAddInterface.class);
                    Call<String> call = api.getDiaryDiaryAdd(userId,petName,title,content,todayDate,address,latitude,longitude,diaryBody);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d(TAG, "onResponse: "+response.body());
                            if(response.body().equals("성공")){
                                finish();
                            }else{
                                Log.d(TAG, "onResponse: 실패");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG,"에러 = " + t.getMessage());
                        }
                    });
                }
            }
        });

        cancelBtn=findViewById(R.id.addDiaryCancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selectMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapBoolean=true;
                diaryMap.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.GONE);
                checkBtn.setVisibility(View.GONE);
            }
        });

        mapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapBoolean=true;
                diaryMap.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.GONE);
                checkBtn.setVisibility(View.GONE);
            }
        });

        selectMapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DiaryAdd.this, selectMapText.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart호출");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume호출");
        super.onResume();

        if(mapReady==true){
            Log.d(TAG, "onCreate: mapReady여서 들어옴");
            naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                    Log.d(TAG, "onMapClick: latitude"+latLng.latitude);
                    Log.d(TAG, "onMapClick: longitude"+latLng.longitude);
                    Log.d(TAG, "onMapClick: pointF"+pointF);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause호출");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop호출");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy호출");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG,"onRestart호출");
        super.onRestart();
    }

    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Log.d(TAG, "onMapReady: mapReady됨");
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        mapReady=true;
        Marker marker = new Marker();
        marker.setWidth(150);
        marker.setHeight(110);
        // 이미지를 비동기적으로 가져오는 스레드 실행

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Log.d(TAG, "onMapClick: latitude"+latLng.latitude);
                Log.d(TAG, "onMapClick: longitude"+latLng.longitude);
                Log.d(TAG, "onMapClick: pointF"+pointF);
                Log.d(TAG, "onMapClick: petImage"+petImage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = Picasso.get().load(petImage).get();
                            bitmap=getRoundedBitmap(bitmap);

                            // 이미지를 OverlayImage로 변환
                            OverlayImage overlayImage = OverlayImage.fromBitmap(bitmap);

                            // 마커에 이미지 적용
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    marker.setMap(null);
                                    marker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
                                    marker.setMap(naverMap);
                                    marker.setIcon(overlayImage); // 이미지 적용

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                selectAddressBtn.setVisibility(View.VISIBLE);
                Location location = convertLatLngToLocation(latLng);
                address=getAddress(location);
                selectAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectMapText.setText(address);
                        diaryMap.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.VISIBLE);
                        checkBtn.setVisibility(View.VISIBLE);
                        selectAddressBtn.setVisibility(View.GONE);
                        mapBoolean=false;
                    }
                });
                marker.setCaptionText(address);
                marker.setCaptionAligns(Align.Top);
                latitude= String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);
                Log.d(TAG, "onMapClick: address"+address);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onBackPressed(){
        if(mapBoolean==true){
            diaryMap.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
            checkBtn.setVisibility(View.VISIBLE);
            selectAddressBtn.setVisibility(View.GONE);
            mapBoolean=false;
        }else{
            finish();
        }
    }

    public void selectImage(){
        Log.d(TAG, "onClick: 다이어리 사진 누름");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        diaryLauncher.launch(intent);
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

    private String getAddress(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return getAddress(location);
        }
    }

    public Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 권한 필요")
                .setMessage("이 앱은 정상적으로 작동하기 위해 위치 권한이 필요합니다. 위치 권한을 부여해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 누르면 권한 요청 다이얼로그를 다시 띄움
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION);

        }
    }

}