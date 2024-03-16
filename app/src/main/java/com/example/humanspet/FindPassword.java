package com.example.humanspet;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.ExistenceEmailInterface;
import com.example.humanspet.Interface.PasswordChangeInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPassword extends AppCompatActivity {
    private String TAG = "비밀번호찾기";
    TextView emailText,emailCheckText,codeText,codeTimeText,passwordText,passwordCheckText,rePasswordText,rePasswordCheckText;
    EditText emailEdit,codeEdit,passwordEdit,rePasswordEdit;
    Button emailSendBtn,codeBtn,nextBtn,finishBtn;
    long initialTimeMillis;
    private CountDownTimer countDownTimer;
    boolean emailBoolean, codeBoolean;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        idMatch();

        initialTimeMillis = 3*60 * 1000;

        countDownTimer = new CountDownTimer(initialTimeMillis,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                codeTimeText.setText("00:00");
            }
        };

        emailSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExistenceEmailInterface existenceEmailApi = ApiClient.getApiClient().create(ExistenceEmailInterface.class);
                Call<String> existenceEmailCall = existenceEmailApi.existenceEmail(emailEdit.getText().toString());
                existenceEmailCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("존재")){
                            codeTimeText.setVisibility(View.VISIBLE);
                            initialTimeMillis = 3*60*1000;
                            countDownTimer.start();
                            emailSendBtn.setClickable(false);
                            emailCheck();
                            emailSendBtn.setText("재전송");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    emailSendBtn.setClickable(true);
                                }
                            }, 3000); //딜레이 타임 조절
                        }else{
                            Toast.makeText(FindPassword.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });


            }
        });

        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = getSharedPreferences("USER", MODE_PRIVATE);
                String checkCode = preferences.getString("CODE","");
                SharedPreferences.Editor editor = preferences.edit();
                String time = codeTimeText.getText().toString();
                if(time.equals("00:00")){
                    Toast.makeText(FindPassword.this, "인증 시간이 초과되었습니다.\n 메일을 다시 전송해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    if(codeEdit.getText().toString().equals(checkCode)){
                        codeTimeText.setVisibility(View.INVISIBLE);
                        Toast.makeText(FindPassword.this, "인증되었습니다.\n새로운 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        editor.remove("CODE");
                        editor.commit();
                        passwordEdit();
                    }else{
                        Toast.makeText(FindPassword.this, "인증 번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    passwordEdit.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    passwordEdit.setBackgroundResource(R.drawable.box_white_gray);
                    passCheck();
                }

            }
        });

        rePasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    rePasswordEdit.setBackgroundResource(R.drawable.box_white_mint);
                }else{
                    rePasswordEdit.setBackgroundResource(R.drawable.box_white_gray);
                }
            }
        });

        rePasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(passwordEdit.getText().toString().equals(rePasswordEdit.getText().toString())){
                    rePasswordCheckText.setText("");
                    finishBtn.setBackgroundResource(R.drawable.box_mint);
                }else{
                    rePasswordCheckText.setText("두 비밀번호가 일치하지 않습니다.");
                    finishBtn.setBackgroundResource(R.drawable.box_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordChangeInterface passwordChangeApi = ApiClient.getApiClient().create(PasswordChangeInterface.class);
                Call<String> passwordChangeCall = passwordChangeApi.passwordChange(emailEdit.getText().toString(),passwordEdit.getText().toString());
                passwordChangeCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("완료")){
                            Toast.makeText(FindPassword.this, "비밀번호가 재설정되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });



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

    private void updateCountdownText(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / (1000 * 60));
        int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        codeTimeText.setText(timeLeftFormatted);
    }

    private void passCheck(){
        final String password = passwordEdit.getText().toString();
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!.%*#?&])[A-Za-z\\d$@$.!%*#?&]{8,}$";

        if(!Pattern.matches(passwordPattern,password)){
            passwordCheckText.setText("올바른 비밀번호 형식이 아닙니다.");
            passwordCheckText.setTextColor(Color.parseColor("#FF0000"));
        }else{
            passwordCheckText.setText("");
            passwordCheckText.setTextColor(Color.parseColor("#000000"));
        }
    }



    public void passwordEdit(){
        emailText.setVisibility(View.GONE);
        emailEdit.setVisibility(View.GONE);
        emailCheckText.setVisibility(View.GONE);
        emailSendBtn.setVisibility(View.GONE);
        codeText.setVisibility(View.GONE);
        codeEdit.setVisibility(View.GONE);
        codeTimeText.setVisibility(View.GONE);
        codeBtn.setVisibility(View.GONE);
        passwordText.setVisibility(View.VISIBLE);
        passwordEdit.setVisibility(View.VISIBLE);
        passwordCheckText.setVisibility(View.VISIBLE);
        rePasswordCheckText.setVisibility(View.VISIBLE);
        rePasswordEdit.setVisibility(View.VISIBLE);
        rePasswordText.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.GONE);
        finishBtn.setVisibility(View.VISIBLE);
    }

    public void idMatch(){
        passwordText = findViewById(R.id.findPasswordPasswordText);
        passwordEdit = findViewById(R.id.findPasswordPasswordEdit);
        passwordCheckText = findViewById(R.id.findPasswordPasswordReCheck);
        rePasswordCheckText = findViewById(R.id.findPasswordRePasswordReCheck);
        rePasswordText = findViewById(R.id.findPasswordRePasswordText);
        rePasswordEdit = findViewById(R.id.findPasswordRePasswordEdit);
        emailText = findViewById(R.id.findPasswordEmailText);
        emailEdit = findViewById(R.id.findPasswordEmailEdit);
        emailCheckText = findViewById(R.id.findPasswordEmailCheckText);
        emailSendBtn = findViewById(R.id.findPasswordSendButton);
        codeText = findViewById(R.id.findPasswordCodeText);
        codeEdit = findViewById(R.id.findPasswordCodeEdit);
        codeTimeText = findViewById(R.id.findPasswordCodeTime);
        codeBtn = findViewById(R.id.findPasswordCodeButton);
        nextBtn = findViewById(R.id.findPasswordNextButton);
        finishBtn = findViewById(R.id.findPasswordFinishButton);
    }
}