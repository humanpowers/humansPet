package com.example.humanspet;

import android.app.Activity;
import android.content.DialogInterface;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.LoginInterface;
import com.example.humanspet.Interface.MyInfoInterface;
import com.example.humanspet.Interface.SocialRegisterInterface;
import com.example.humanspet.Interface.UserCheckInterface;
import com.example.humanspet.Interface.UserPeristalsisInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthErrorCode;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    String TAG="로그인";
    Button joinBtn,findIdBtn,findPasswordBtn;
    private SharedPreferences preferences;
    CheckBox autoLoginCheckBox;

    EditText etId, etPassword;
    Button btnLogin;
    ImageButton btnKakao,btnGoogle,btnNaver;
    PreferenceHelper preferenceHelper;
    private Long mLastClickTime = 0L;
    String token;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    ApiClient apiClient = new ApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getHashKey();

        NaverIdLoginSDK.INSTANCE.initialize(Login.this,apiClient.getNaver_client_id(),apiClient.getNaver_client_secret(),apiClient.getNaver_client_name());


        findIdBtn=findViewById(R.id.loginFindIdBtn);
        findPasswordBtn = findViewById(R.id.loginFindPasswordBtn);
        btnGoogle = findViewById(R.id.loginGoogleLogin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Login.this, gso);

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
                Intent intent = new Intent(Login.this, EmailCertification.class);
                startActivity(intent);
            }
        });

        preferenceHelper = new PreferenceHelper(this);

        etId=findViewById(R.id.loginIdEdit);
        etPassword=findViewById(R.id.loginPasswordEdit);

        etId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true){
                    etId.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    etId.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true){
                    etPassword.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    etPassword.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });

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

        findIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findIdIntent = new Intent(Login.this,FindId.class);
                startActivity(findIdIntent);
            }
        });

        findPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findPasswordIntent = new Intent(Login.this,FindPassword.class);
                startActivity(findPasswordIntent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                googleLogin();
            }
        });

        btnNaver=findViewById(R.id.loginNaverLogin);
        btnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverLogin();
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
                    editor.putString("USERNAME",user.getKakaoAccount().getProfile().getNickname());
                    editor.commit();
                    Log.d(TAG,"invoke: email: "+user.getKakaoAccount().getEmail());
                    Log.d(TAG,"invoke: photo: "+ user.getKakaoAccount().getProfile().getProfileImageUrl());
                    Log.d(TAG,user.getKakaoAccount().getProfile().getNickname());
                    UserCheckInterface kakaoApi = ApiClient.getApiClient().create(UserCheckInterface.class);
                    Call<String> kakaoCall =  kakaoApi.userCheck(user.getKakaoAccount().getEmail());
                    kakaoCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d(TAG, "onResponse: "+response.body());
                            if(response.body().equals("연동")){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                                alertDialog.setTitle("연동");
                                alertDialog.setMessage("이미 존재하는 계정입니다.\n연동하시겠습니까?");
                                alertDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        UserPeristalsisInterface peristalsisApi = ApiClient.getApiClient().create(UserPeristalsisInterface.class);
                                        Call<String> peristalsisCall = peristalsisApi.userPeristalsis(user.getKakaoAccount().getEmail());
                                        peristalsisCall.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if(response.body().equals("완료")){
                                                    Toast.makeText(Login.this, "연동 되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Login.this,MainPage.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Log.d(TAG, "onResponse: 연동안됨");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {

                                            }
                                        });
                                    }
                                });
                                alertDialog.setNegativeButton("아니요",null);
                                alertDialog.show();
                            }else if(response.body().equals("완료")){
                                Intent intent = new Intent(Login.this,MainPage.class);
                                startActivity(intent);
                                finish();
                            }else if(response.body().equals("처음")){
                                kakaoRegister(user);
                            }else if(response.body().equals("로그인")){
                                Toast.makeText(Login.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this,MainPage.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                }else{
                    Log.d(TAG,"로그인 안됨");
                }

                return null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void googleLogin(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        resultLauncher.launch(signInIntent);
    }

    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                preferences = getSharedPreferences("USER",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("USERNAME",account.getFamilyName());
                editor.putString("USERID", account.getEmail());
                editor.commit();
                String email = account.getEmail();
                String familyName = account.getFamilyName();
                Log.d(TAG, "handleSignInResult: email: "+email);
                Log.d(TAG, "handleSignInResult: fmailyName: "+familyName);

                UserCheckInterface googleApi = ApiClient.getApiClient().create(UserCheckInterface.class);
                Call<String> googleCall =  googleApi.userCheck(account.getEmail());
                googleCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("연동")){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                            alertDialog.setTitle("연동");
                            alertDialog.setMessage("이미 존재하는 계정입니다.\n연동하시겠습니까?");
                            alertDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UserPeristalsisInterface peristalsisApi = ApiClient.getApiClient().create(UserPeristalsisInterface.class);
                                    Call<String> peristalsisCall = peristalsisApi.userPeristalsis(account.getEmail());
                                    peristalsisCall.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if(response.body().equals("완료")){
                                                Toast.makeText(Login.this, "연동 되었습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this,MainPage.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Log.d(TAG, "onResponse: 연동안됨");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                                }
                            });
                            alertDialog.setNegativeButton("아니요",null);
                            alertDialog.show();
                        }else if(response.body().equals("완료")){
                            Intent intent = new Intent(Login.this,MainPage.class);
                            startActivity(intent);
                            finish();
                        }else if(response.body().equals("처음")){
                            SocialRegisterInterface socialApi = ApiClient.getApiClient().create(SocialRegisterInterface.class);
                            Call<String> socialCall = socialApi.socialRegister(account.getFamilyName(),account.getEmail(),"google");
                            socialCall.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.body().equals("성공")){
                                        Toast.makeText(Login.this,"환영합니다.",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this,MainPage.class);
                                        startActivity(intent);
                                        finish();
                                    }else if(response.body().equals("실패")){
                                        Toast.makeText(Login.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });

                        }else if(response.body().equals("로그인")){
                            Toast.makeText(Login.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,MainPage.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

                Intent intent = new Intent(Login.this,MainPage.class);
                startActivity(intent);
                finish();
            }
        } catch (ApiException e) {
            Log.w("failed", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void logout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    // 로그아웃 성공시 실행
                    // 로그아웃 이후의 이벤트들(토스트 메세지, 화면 종료)을 여기서 수행하면 됨
                });
    }

    private void naverLogout(){
        NaverIdLoginSDK.INSTANCE.logout();
    }

    private void naverLogin() {
        NaverIdLoginSDK.INSTANCE.logout();
        OAuthLoginCallback oauthLoginCallback = new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                Log.d(TAG, "onSuccess: "+NaverIdLoginSDK.INSTANCE.getAccessToken());
                naverRegister();
            }

            @Override
            public void onFailure(int httpStatus, String message) {
                NidOAuthErrorCode errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode();
                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                Log.d(TAG, "onFailure: "+"errorCode:" + errorCode + ", errorDesc:" + errorDescription);
            }

            @Override
            public void onError(int errorCode, String message) {
                onFailure(errorCode, message);
            }
        };

        NaverIdLoginSDK.INSTANCE.authenticate(Login.this, oauthLoginCallback);
    }

    public void kakaoRegister(User user){
        SocialRegisterInterface socialApi = ApiClient.getApiClient().create(SocialRegisterInterface.class);
        Call<String> socialCall = socialApi.socialRegister(user.getKakaoAccount().getProfile().getNickname(),user.getKakaoAccount().getEmail(),"kakao");
        socialCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().equals("성공")){
                    Toast.makeText(Login.this,"환영합니다.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this,MainPage.class);
                    startActivity(intent);
                    finish();
                }else if(response.body().equals("실패")){
                    Toast.makeText(Login.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void naverRegister(){
        new NidOAuthLogin().callProfileApi(new NidProfileCallback<NidProfileResponse>() {
            @Override
            public void onSuccess(NidProfileResponse nidProfileResponse) {
                String naverEmail = nidProfileResponse.getProfile().getEmail();
                String naverName = nidProfileResponse.getProfile().getName();
                preferences = getSharedPreferences("USER",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("USERNAME", naverName);
                editor.putString("USERID", naverEmail);
                editor.commit();
                UserCheckInterface naverApi = ApiClient.getApiClient().create(UserCheckInterface.class);
                Call<String> naverCall =  naverApi.userCheck(naverEmail);
                naverCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("연동")){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                            alertDialog.setTitle("연동");
                            alertDialog.setMessage("이미 존재하는 계정입니다.\n연동하시겠습니까?");
                            alertDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UserPeristalsisInterface peristalsisApi = ApiClient.getApiClient().create(UserPeristalsisInterface.class);
                                    Call<String> peristalsisCall = peristalsisApi.userPeristalsis(naverEmail);
                                    peristalsisCall.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if(response.body().equals("완료")){
                                                Toast.makeText(Login.this, "연동 되었습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this,MainPage.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Log.d(TAG, "onResponse: 연동안됨");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                                }
                            });
                            alertDialog.setNegativeButton("아니요",null);
                            alertDialog.show();
                        }else if(response.body().equals("완료")){
                            Intent intent = new Intent(Login.this,MainPage.class);
                            startActivity(intent);
                            finish();
                        }else if(response.body().equals("처음")){
                            SocialRegisterInterface socialApi = ApiClient.getApiClient().create(SocialRegisterInterface.class);
                            Call<String> socialCall = socialApi.socialRegister(naverName,naverEmail,"naver");
                            socialCall.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.body().equals("성공")){
                                        Toast.makeText(Login.this,"환영합니다.",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this,MainPage.class);
                                        startActivity(intent);
                                        finish();
                                    }else if(response.body().equals("실패")){
                                        Toast.makeText(Login.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });

                        }else if(response.body().equals("로그인")){
                            Toast.makeText(Login.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,MainPage.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(int i, @NonNull String s) {

            }

            @Override
            public void onError(int i, @NonNull String s) {

            }
        });

    }




}