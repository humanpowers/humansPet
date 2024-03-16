package com.example.humanspet;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.humanspet.Interface.MyInfoInterface;
import com.example.humanspet.Interface.UserImageChangeInterface;
import com.example.humanspet.Interface.UserSecessionInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInfo extends AppCompatActivity {
    String TAG="인포";
    Button checkBtn,logoutBtn,secessionBtn;
    String userEmail,userName;
    TextView nameText,addressText,idText,imageChange,userIdText,userNameText,userAddressText;
    ImageButton imageChangeBtn;
    ImageView userImage;
    ApiClient apiClient=new ApiClient();
    LottieAnimationView animationView,loadingAnimation;
    private SharedPreferences preferences;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Log.d(TAG, "onCreate호출");

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        userEmail=preferences.getString("USERID","");

        init();

        userImage=findViewById(R.id.myInfoImage);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MyInfo.this, gso);

        ActivityResultLauncher<Intent> diaryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(MyInfo.this);
                        dlg.setMessage("정말로 변경하시겠습니까?");
                        dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                animationView = findViewById(R.id.userInfoLottie);
                                animationView.loop(true);
                                animationView.playAnimation();
                                animationView.setVisibility(View.VISIBLE);
                                Intent intent = result.getData();
                                Uri userImageUri = intent.getData();
                                Log.d(TAG, "onClick: userImageUri"+userImageUri);
                                String userImageRealUri=getRealPathFromUri(userImageUri);
                                Log.d(TAG, "onClick: userImageRealUri"+userImageRealUri);
                                File userImageFile= new File(userImageRealUri);
                                Log.d(TAG, "onClick: userImageFIle"+userImageFile);
                                String userImageFileName=userEmail+".jpg";
                                Log.d(TAG, "onClick: userImageFileName"+userImageFileName);

                                RequestBody userImageRequestBody=RequestBody.create(MediaType.parse("multipart/form-data"),userImageFile);
                                MultipartBody.Part userImageBody = MultipartBody.Part.createFormData("uploaded_file",userImageFileName,userImageRequestBody);

                                UserImageChangeInterface userApi=ApiClient.getApiClient().create(UserImageChangeInterface.class);
                                Call<String> userCall = userApi.UserImageChange(userEmail,userImageBody);
                                userCall.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> userCall, Response<String> userResponse) {
                                        Log.d(TAG, "onResponse: "+userResponse.body());
                                        if(userResponse.body().equals("성공")){
                                            animationView.setVisibility(View.GONE);
                                            if(userImageRealUri.equals(null)){
                                                Glide.with(MyInfo.this)
                                                        .load(R.drawable.basic_profile)
                                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                        .skipMemoryCache(true)
                                                        .into(userImage);
                                            }else{
                                                Glide.with(MyInfo.this)
                                                        .load(userImageRealUri)
                                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                        .skipMemoryCache(true)
                                                        .into(userImage);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        dlg.setNegativeButton("아니요",null);
                        dlg.show();
                    }
                });

        imageChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 다이어리 사진 누름");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                diaryLauncher.launch(intent);
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패 , SDK에서 토큰 삭제됨", error);
                    } else {
                        Log.e(TAG, "로그아웃 성공, SDK에서 토큰 삭제됨");
                    }
                    return null;
                });
                editor.remove("USERID");
                editor.remove("AUTOLOGIN");
                editor.commit();
                Intent intent = new Intent(MyInfo.this,Login.class); //fragment라서 activity intent와는 다른 방식
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        secessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyInfo.this);
                dlg.setMessage("정말로 탈퇴하시겠습니까?\n(탈퇴시 등록되어있던 모든 정보가 사라집니다.)");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserSecessionInterface secessionApi = ApiClient.getApiClient().create(UserSecessionInterface.class);
                        Call<String> secessionCall = secessionApi.userSecession(userEmail,userName);
                        secessionCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.body().equals("완료")){
                                    Toast.makeText(MyInfo.this, "회원 탈퇴하셨습니다 ㅠ.ㅠ", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MyInfo.this,Login.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                });
                dlg.setNegativeButton("취소",null);
                dlg.show();

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
        infoGone();
        myInfoInput();
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

    private void myInfoInput(){
        Log.d(TAG,userEmail);

        loadingAnimation = findViewById(R.id.myInfoFirstLottie);
        loadingAnimation.loop(true);
        loadingAnimation.playAnimation();
        loadingAnimation.setVisibility(View.VISIBLE);

        MyInfoInterface api = ApiClient.getApiClient().create(MyInfoInterface.class);
        Call<String> call = api.getUserInfo(userEmail);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if(response.isSuccessful()&&response.body()!=null){
                    loadingAnimation.setVisibility(View.GONE);
                    infoVisible();
                    String[] responseSp=response.body().split("!!@!!");
                    userNameText.setText(responseSp[0]);
                    userName = responseSp[0];
                    Log.d(TAG, "onResponse: userName"+userName);
                    userIdText.setText(responseSp[1]);
                    if(responseSp.length<=3){
                        userAddressText.setText("아직 주소를 입력하지 않았습니다");
                    }else{
                        userAddressText.setText(responseSp[3]);
                    }
                    Log.d(TAG, "onResponse: length"+responseSp.length);
                    if(!responseSp[2].equals("")){
                        Glide.with(MyInfo.this).load("http://"+apiClient.goUri(responseSp[2])).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(Glide.with(MyInfo.this).load(R.drawable.thumbnail_image)).into(userImage);
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: 실패"+t);
            }
        });

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

    public void infoGone(){
        logoutBtn.setVisibility(View.GONE);
        secessionBtn.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        nameText.setVisibility(View.GONE);
        addressText.setVisibility(View.GONE);
        idText.setVisibility(View.GONE);
        checkBtn.setVisibility(View.GONE);
        imageChange.setVisibility(View.GONE);
        imageChangeBtn.setVisibility(View.GONE);
    }

    public void infoVisible(){
        logoutBtn.setVisibility(View.VISIBLE);
        secessionBtn.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);
        addressText.setVisibility(View.VISIBLE);
        idText.setVisibility(View.VISIBLE);
        checkBtn.setVisibility(View.VISIBLE);
        imageChange.setVisibility(View.VISIBLE);
        imageChangeBtn.setVisibility(View.VISIBLE);
    }

    public void init(){
        nameText=findViewById(R.id.myInfoNameText);
        addressText=findViewById(R.id.myInfoAddressText);
        idText=findViewById(R.id.myInfoIdText);
        userNameText = findViewById(R.id.myInfoUserName);
        userIdText = findViewById(R.id.myInfoUserId);
        userAddressText = findViewById(R.id.myInfoUserAddress);
        checkBtn=findViewById(R.id.myInfoCheckBtn);
        imageChange=findViewById(R.id.myInfoImageAddText);
        imageChangeBtn=findViewById(R.id.myInfoImageAddButton);
        userImage=findViewById(R.id.myInfoImage);
        logoutBtn = findViewById(R.id.myInfoLogoutBtn);
        secessionBtn = findViewById(R.id.myInfoSecessionBtn);

    }

}