package com.example.humanspet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.DuplicationInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailCertification extends AppCompatActivity {
    String TAG = "이메일인증";
    Button mailBtn,previousBtn,nextBtn,codeBtn;
    EditText emailEdit,codeEdit;
    TextView emailCheckText;
    private SharedPreferences preferences;
    boolean emailBoolean,codeBoolean;
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    long initialTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_certification);
        emailBoolean=false;
        codeBoolean=false;
        initialTimeMillis = 3*60 * 1000;

        countdownText = findViewById(R.id.emailCertificationCodeTime);
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

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        mailBtn = (Button) findViewById(R.id.emailCertificationSendButton);
        emailEdit = findViewById(R.id.emailCertificationEmailEdit);
        emailCheckText=findViewById(R.id.emailCertificationEmailCheckText);
        previousBtn=findViewById(R.id.emailCertificationPreviousButton);
        nextBtn=findViewById(R.id.emailCertificationNextButton);
        codeBtn=findViewById(R.id.emailCertificationCodeButton);
        codeEdit=findViewById(R.id.emailCertificationCodeEdit);


        codeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true){
                    codeEdit.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    codeEdit.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });

        emailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true){
                    emailEdit.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    emailEdit.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });

        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailBoolean=false;
                codeBoolean=false;
                mailBtn.setText("이메일 인증");
                nextBtn.setBackgroundResource(R.drawable.box_gray);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DuplicationInterface duplicationApi = ApiClient.getApiClient().create(DuplicationInterface.class);
                Call<String> duplicationCall = duplicationApi.doDuplication(emailEdit.getText().toString());
                duplicationCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("존재")){
                            Toast.makeText(EmailCertification.this, "이미 사용중인 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            countdownText.setVisibility(View.VISIBLE);
                            initialTimeMillis = 30 * 1000;
                            countDownTimer.start();
                            mailBtn.setClickable(false);
                            emailCheck();
                            mailBtn.setText("재전송");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mailBtn.setClickable(true);
                                }
                            }, 3000); //딜레이 타임 조절
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailBoolean==false){
                    Toast.makeText(EmailCertification.this, "이메일 인증을 진행해주세요.", Toast.LENGTH_SHORT).show();
                }else if(codeBoolean==false){
                    Toast.makeText(EmailCertification.this, "인증 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(EmailCertification.this,Register.class);
                    intent.putExtra("id",emailEdit.getText().toString());
                    startActivity(intent);
                }
            }
        });

        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = getSharedPreferences("USER", MODE_PRIVATE);
                String checkCode = preferences.getString("CODE","");
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(TAG, "onClick: "+checkCode);
                Log.d(TAG, "onClick: "+codeEdit.getText().toString());
                String time = countdownText.getText().toString();
                if(time.equals("00:00")){
                    Toast.makeText(EmailCertification.this, "인증 시간이 초과되었습니다.\n 메일을 다시 전송해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(codeEdit.getText().toString().equals(checkCode)){
                        codeBoolean=true;
                        nextBtn.setBackgroundResource(R.drawable.box_mint);
                        countdownText.setVisibility(View.INVISIBLE);
                        Toast.makeText(EmailCertification.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                        editor.remove("CODE");
                        editor.commit();
                    }else{
                        Toast.makeText(EmailCertification.this, "인증 번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void startCountdown() {
        countDownTimer.start();
    }

    private void updateCountdownText(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / (1000 * 60));
        int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        countdownText.setText(timeLeftFormatted);
    }

    private void emailCheck(){
        final String email = emailEdit.getText().toString();
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        emailBoolean=false;
        codeBoolean=false;
        if(pattern.matcher(email).matches()){
            emailBoolean=true;
            emailCheckText.setText("");
            emailCheckText.setTextColor(Color.parseColor("#000000"));
            Toast.makeText(this, "인증 메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // UI 업데이트 코드 작성
                    SendMail mailServer = new SendMail();
                    mailServer.sendSecurityCode(getApplicationContext(), email);
                }
            });
        } else {
            emailCheckText.setText("올바른 이메일 형식이 아닙니다.");
            emailCheckText.setTextColor(Color.parseColor("#FF0000"));
        }
    }
}