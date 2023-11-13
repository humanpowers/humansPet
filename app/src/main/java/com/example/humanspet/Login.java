package com.example.humanspet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.LoginInterface;
import com.example.humanspet.Interface.MyInfoInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    String TAG="로그인";
    Button joinBtn;
    private SharedPreferences preferences;
    CheckBox autoLoginCheckBox;

    EditText etId, etPassword;
    Button btnLogin;
    ImageButton btnKakao;
    PreferenceHelper preferenceHelper;
    private Long mLastClickTime = 0L;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getHashKey();

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        String autologinCheck=preferences.getString("AUTOLOGIN","");
        if(!autologinCheck.equals("")){
            Intent intent = new Intent(Login.this,MainPage.class);
            startActivity(intent);
            finish();
        }


        joinBtn=(Button) findViewById(R.id.loginJoinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        preferenceHelper = new PreferenceHelper(this);

        etId=findViewById(R.id.loginIdEdit);
        etPassword=findViewById(R.id.loginPasswordEdit);

        btnLogin=findViewById(R.id.loginLoginBtn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    switch (view.getId()) {
                        case R.id.loginLoginBtn:
                            loginUser();
                            break;
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
        Function2<OAuthToken,Throwable, Unit> callback=new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                updateKakaoLogin();
                Log.d(TAG, "invoke: "+oAuthToken);
                return null;
            }
        };

        btnKakao=findViewById(R.id.loginKakaoLogin);
        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(Login.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(Login.this,callback);
                }else{
                    UserApiClient.getInstance().loginWithKakaoTalk(Login.this,callback);
                }
            }
        });
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult();

                            Log.d(TAG, "onComplete: "+token);
                        }
                    }
                });

    }


    private void loginUser(){    //로그인시 서버에 연동,아이디와 비밀번호가 존재하고 맞다면 로그인,쉐어드에 저장
        final String userId=etId.getText().toString();
        final String userPw=etPassword.getText().toString();
        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        LoginInterface api = ApiClient.getApiClient().create(LoginInterface.class);
        Call<String> call= api.getUserLogin(userId,userPw,token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if(response.isSuccessful()&&response.body()!=null){
                    Log.e("onSuccess",response.body());
                    if(response.body().equals("성공")){
                        Toast.makeText(Login.this,"환영합니다",Toast.LENGTH_SHORT).show();
                        autoLoginCheckBox=findViewById(R.id.loginAutoLoginCheckBox);
                        if(autoLoginCheckBox.isChecked()){
                            editor.putString("AUTOLOGIN",userId);
                            editor.commit();
                        }
                        MyInfoInterface api2 = ApiClient.getApiClient().create(MyInfoInterface.class);
                        Call<String> call2 = api2.getUserInfo(userId);
                        call2.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call2, Response<String> response2) {
                                if(response2.isSuccessful()&&response2.body()!=null){
                                    Log.e("onSuccess",response2.body());
                                    String[] responseSp=response2.body().split("!");
                                    editor.putString("USERNAME",responseSp[0]);
                                    editor.putString("USERID",userId);
                                    editor.commit();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                        Intent intent = new Intent(Login.this,MainPage.class);
                        startActivity(intent);
                        finish();
                    }else if(response.body().equals("빈칸")) {
                        Toast.makeText(Login.this,"아이디와 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Login.this,"아이디또는 비밀번호를 잘못 입력했습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG,"에러 = " + t.getMessage());
            }
        });
    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);

            }
        }
    }
    private void updateKakaoLogin() {
        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user !=null){
                    Log.d(TAG,"invoke: id: "+user.getId());
                    editor.putString("USERID", user.getKakaoAccount().getEmail());
                    editor.commit();
                    Log.d(TAG,"invoke: email: "+user.getKakaoAccount().getEmail());
                    Log.d(TAG,"invoke: photo: "+ user.getKakaoAccount().getProfile().getProfileImageUrl());
                    Log.d(TAG,user.getKakaoAccount().getProfile().getNickname());

                }else{
                    Log.d(TAG,"로그인 안됨");
                }

                return null;
            }
        });
        Intent intent = new Intent(Login.this,MainPage.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}