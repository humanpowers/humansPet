package com.example.humanspet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.RegisterInterface;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    String TAG="조인";
    EditText etName,etEmail,etPhone,etPassword,etRePassword,etPhoneCode;
    Button finishBtn,cancelBtn,joinPhoneCheckBtn,phoneCodeBtn;
    TextView phoneCheckText,passwordCheck,passwordReCheck,phoneCodeText;
    PreferenceHelper preferenceHelper;
    private Long mLastClickTime = 0L;
    boolean passwordBoolean,nameBoolean,phoneBoolean,phoneCodeBoolean;
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    long initialTimeMillis;
    String phoneCodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        initialTimeMillis = 2*60 * 1000;

        countdownText = findViewById(R.id.joinPhoneCodeTime);
        countDownTimer = new CountDownTimer(initialTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // 시간이 종료될 때 수행할 작업
                countdownText.setText("00:00");
            }
        };

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        passwordBoolean=false;
        nameBoolean=false;
        phoneBoolean=false;
        phoneCodeBoolean=false;

        preferenceHelper = new PreferenceHelper(this);

        phoneCheckText=findViewById(R.id.joinPhoneCheckText);
        passwordCheck=findViewById(R.id.joinPasswordCheck);
        passwordReCheck=findViewById(R.id.joinPasswordReCheck);
        etName= findViewById(R.id.joinNameEdit);
        etEmail= findViewById(R.id.joinEmailEdit);
        etPassword= findViewById(R.id.joinPwEdit);
        etRePassword= findViewById(R.id.joinRePwEdit);
        etPhone= findViewById(R.id.joinPhoneEdit);
        finishBtn= findViewById(R.id.joinFinishButton);
        joinPhoneCheckBtn= findViewById(R.id.joinPhoneCheckBtn);
        cancelBtn= findViewById(R.id.joinPreviousButton);
        phoneCodeText=findViewById(R.id.joinPhoneCodeText);
        phoneCodeBtn=findViewById(R.id.joinPhoneCodeBtn);
        etPhoneCode=findViewById(R.id.joinPhoneCodeEdit);

        etEmail.setText(id);
        etEmail.setEnabled(false);

        focusOn(etEmail);
        focusOn(etPhone);
        focusOn(etPhoneCode);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etName.getText().toString().equals("")){
                    finishBtn.setBackgroundResource(R.drawable.box_gray);
                    nameBoolean=false;
                }else{
                    nameBoolean=true;
                    if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true&&phoneCodeBoolean==true){
                        finishBtn.setBackgroundResource(R.drawable.box_mint);
                    }else{
                        finishBtn.setBackgroundResource(R.drawable.box_gray);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 클릭됨");
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    switch (view.getId()) {
                        case R.id.joinFinishButton:
                            if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true&&phoneCodeBoolean==true){
                                registerMe();
                            }
                            break;
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    etPassword.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    etPassword.setBackgroundResource(R.drawable.box_white_gray);
                    passCheck();
                }

            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    etRePassword.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    etRePassword.setBackgroundResource(R.drawable.box_white_gray);
                    if(etPassword.getText().toString().equals(etRePassword.getText().toString())){
                        passwordCheck.setVisibility(View.VISIBLE);
                        passwordBoolean=true;
                        passwordCheck.setText("두 비밀번호가 일치합니다.");
                        passwordCheck.setTextColor(Color.parseColor("#0000FF"));
                        if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true&&phoneCodeBoolean==true){
                            finishBtn.setBackgroundResource(R.drawable.box_mint);
                        }else{
                            finishBtn.setBackgroundResource(R.drawable.box_gray);
                        }
                    }else{
                        finishBtn.setBackgroundResource(R.drawable.box_gray);
                        passwordCheck.setVisibility(View.VISIBLE);
                        passwordCheck.setText("두 비밀번호가 일치하지 않습니다.");
                        passwordCheck.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }
        });


        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        joinPhoneCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rawPhoneNumber = etPhone.getText().toString();
//                rawPhoneNumber="010-1111-2222";
                String numericPhoneNumber = rawPhoneNumber.replaceAll("[^0-9]", "");
                String e164PhoneNumber = "+82" + numericPhoneNumber;
                if(phoneBoolean==true){
                    Log.d(TAG, "onClick: 들어옴");
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            e164PhoneNumber,
                            120,  // 타임아웃 시간 (초)
                            TimeUnit.SECONDS,
                            Register.this,  // 현재 액티비티
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    // 인증이 자동으로 완료된 경우 호출
                                    Log.d(TAG, "onVerificationCompleted: 인증자동");
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    // 인증이 실패한 경우 호출
                                    Log.e(TAG, "인증 실패: " + e.getMessage());
                                }

                                @Override
                                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    // 인증 코드가 성공적으로 전송된 경우 호출
                                    // 여기에서 verificationId를 저장하거나 다른 과정을 진행할 수 있습니다.
                                    phoneCodeId=verificationId;
                                    Toast.makeText(Register.this, "인증코드가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                    countdownText.setVisibility(View.VISIBLE);
                                    initialTimeMillis = 30 * 1000;
                                    countDownTimer.start();
                                    etPhoneCode.setVisibility(View.VISIBLE);
                                    phoneCodeBtn.setVisibility(View.VISIBLE);
                                    phoneCodeText.setVisibility(View.VISIBLE);
                                }
                            }
                    );
                }
            }
        });

        phoneCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countdownText.getText().toString().equals("00:00")){
                    Toast.makeText(Register.this, "인증 시간이 초과되었습니다.\n 코드를 다시 전송해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    // PhoneAuthCredential 객체 생성
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneCodeId, etPhoneCode.getText().toString());

                    // FirebaseAuth 인스턴스 가져오기
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    // PhoneAuthCredential을 사용하여 사용자를 인증
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 인증이 성공한 경우
                                        Log.d(TAG, "signInWithCredential:success");
                                        Toast.makeText(Register.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                                        phoneCodeBoolean=true;
                                        countdownText.setVisibility(View.INVISIBLE);
                                        if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true&&phoneCodeBoolean==true){
                                            finishBtn.setBackgroundResource(R.drawable.box_mint);
                                        }else{
                                            finishBtn.setBackgroundResource(R.drawable.box_gray);
                                        }
                                    } else {
                                        // 인증이 실패한 경우
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        Toast.makeText(Register.this, "인증 코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            // 잘못된 인증 코드를 입력한 경우 등에 대한 처리
                                            Toast.makeText(Register.this, "인증 코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }

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

    private void registerMe(){
        final String name=etName.getText().toString();
        final String email=etEmail.getText().toString();
        final String phone=etPhone.getText().toString();
        final String password=etPassword.getText().toString();

        Log.d(TAG, "registerMe: 레지스터 들어옴");
        RegisterInterface api = ApiClient.getApiClient().create(RegisterInterface.class);
        Call<String> call = api.getUserRegist(name, email, password, phone);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if(response.isSuccessful() && response.body()!=null){
                    Log.e("onSuccess",response.body());
                    if(response.body().equals("성공")){
                        Toast.makeText(Register.this,"회원가입을 성공정으로 마치셨습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this,Login.class);
                        startActivity(intent);
                        finish();
                    }else if(response.body().equals("빈칸")){
                        Toast.makeText(Register.this,"모든칸에 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }else if(response.body().equals("존재")){
                        Toast.makeText(Register.this,"이미 존재하는 이메일 입니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Register.this,"회원가입을 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG,"에러 = "+ t.getMessage());
            }
        });
    }

    private void phoneCheck(){
        phoneCodeBoolean=false;
        final String phone = etPhone.getText().toString();
        String phonePattern = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";


        if(!Pattern.matches(phonePattern,phone)){
            finishBtn.setBackgroundResource(R.drawable.box_gray);
            phoneBoolean=false;
            phoneCheckText.setVisibility(View.VISIBLE);
            phoneCheckText.setText("올바른 휴대폰 번호 형식이 아닙니다.");
            phoneCheckText.setTextColor(Color.parseColor("#FF0000"));
        }else{
            phoneBoolean=true;
            if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true&&phoneCodeBoolean==true){
                finishBtn.setBackgroundResource(R.drawable.box_mint);
            }else{
                finishBtn.setBackgroundResource(R.drawable.box_gray);
            }
            phoneCheckText.setVisibility(View.GONE);
            phoneCheckText.setText("");
            phoneCheckText.setTextColor(Color.parseColor("#000000"));
        }

    }

    private void passCheck(){
        final String password = etPassword.getText().toString();
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!.%*#?&])[A-Za-z\\d$@$.!%*#?&]{8,}$";

        if(!Pattern.matches(passwordPattern,password)){
            passwordReCheck.setText("올바른 비밀번호 형식이 아닙니다.");
            passwordReCheck.setTextColor(Color.parseColor("#FF0000"));
        }else{
            passwordReCheck.setText("");
            passwordReCheck.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void focusOn(EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true){
                    editText.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    editText.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });
    }
    private void updateCountdownText(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / (1000 * 60));
        int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        countdownText.setText(timeLeftFormatted);
    }
}