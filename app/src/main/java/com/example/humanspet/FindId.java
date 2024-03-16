package com.example.humanspet;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.FindIdInterface;
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

public class FindId extends AppCompatActivity {
    String TAG = "아이디찾기";
    private CountDownTimer countDownTimer;
    long initialTimeMillis;
    String phoneCodeId;
    private Long mLastClickTime = 0L;
    TextView countdownText;
    boolean phoneBoolean,phoneCodeBoolean;
    EditText etPhone,etPhoneCode;
    Button phoneCodeBtn,finishBtn,findIdPhoneCheckBtn;
    TextView phoneCodeText,phoneCheckText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        phoneCheckText = findViewById(R.id.findIdPhoneCheckText);
        phoneCodeText = findViewById(R.id.findIdPhoneCodeText);
        etPhone=findViewById(R.id.findIdPhoneEdit);
        etPhoneCode=findViewById(R.id.findIdPhoneCodeEdit);
        phoneCodeBtn = findViewById(R.id.findIdPhoneCodeBtn);
        finishBtn = findViewById(R.id.findIdFinishButton);
        findIdPhoneCheckBtn=findViewById(R.id.findIdPhoneCheckBtn);



        FirebaseApp.initializeApp(this);
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        initialTimeMillis = 2*60 * 1000;

        countdownText = findViewById(R.id.findIdPhoneCodeTime);
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
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countdownText.getText().toString().equals("00:00")){
                    Toast.makeText(FindId.this, "인증 시간이 초과되었습니다.\n 코드를 다시 전송해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    // PhoneAuthCredential 객체 생성
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneCodeId, etPhoneCode.getText().toString());

                    // FirebaseAuth 인스턴스 가져오기
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    // PhoneAuthCredential을 사용하여 사용자를 인증
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(FindId.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // 인증이 성공한 경우
                                        Log.d(TAG, "signInWithCredential:success");
                                        Toast.makeText(FindId.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                                        phoneCodeBoolean=true;
                                        countdownText.setVisibility(View.INVISIBLE);
                                        if(phoneBoolean==true&&phoneCodeBoolean==true){
                                            finishBtn.setBackgroundResource(R.drawable.box_mint);
                                            phoneCodeBtn.setBackgroundResource(R.drawable.box_gray);
                                            findIdPhoneCheckBtn.setBackgroundResource(R.drawable.box_gray);
                                        }else{
                                            finishBtn.setBackgroundResource(R.drawable.box_gray);
                                        }
                                    } else {
                                        // 인증이 실패한 경우
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        Toast.makeText(FindId.this, "인증 코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            // 잘못된 인증 코드를 입력한 경우 등에 대한 처리
                                            Toast.makeText(FindId.this, "인증 코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }

            }
        });

        findIdPhoneCheckBtn.setOnClickListener(new View.OnClickListener() {
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
                            FindId.this,  // 현재 액티비티
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
                                    Toast.makeText(FindId.this, "인증코드가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                    findIdPhoneCheckBtn.setText("재전송");
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

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 클릭됨");
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    switch (view.getId()) {
                        case R.id.findIdFinishButton:
                            if(phoneBoolean==true&&phoneCodeBoolean==true){
                                findId();
                            }
                            break;
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

    }

    public void findId(){
        final String phone=etPhone.getText().toString();
        FindIdInterface findIdInterface = ApiClient.getApiClient().create(FindIdInterface.class);
        Call<String> call = findIdInterface.getUserId(phone);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                if(response.body().equals("없음")){
                    Toast.makeText(FindId.this,"아이디가 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    String[] idSp = response.body().split("@");
                    String finalId = "";
                    String firstId = idSp[0];
                    String[] firstIdSp=firstId.split("");
                    for(int i=0;i<firstIdSp.length;i++){
                        if(i<firstIdSp.length-5){
                            finalId+=firstIdSp[i];
                        }else{
                            finalId+="*";
                        }
                    }
                    finalId+="@"+idSp[1];
                    AlertDialog.Builder dialog = new AlertDialog.Builder(FindId.this);
                    dialog.setTitle("아이디");
                    dialog.setMessage(finalId);
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

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
            if(phoneBoolean==true&&phoneCodeBoolean==true){
                finishBtn.setBackgroundResource(R.drawable.box_mint);
            }else{
                finishBtn.setBackgroundResource(R.drawable.box_gray);
            }
            phoneCheckText.setVisibility(View.GONE);
            phoneCheckText.setText("");
            phoneCheckText.setTextColor(Color.parseColor("#000000"));
        }

    }

    private void updateCountdownText(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / (1000 * 60));
        int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        countdownText.setText(timeLeftFormatted);
    }
}