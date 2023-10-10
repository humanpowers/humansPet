package com.example.humanspet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.humanspet.Interface.RegisterInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    String TAG="조인";
    EditText etName,etEmail,etPhone,etPassword,etRePassword;
    Button finishBtn,cancelBtn,joinPhoneCheckBtn,joinEmailCheckBtn;
    TextView phoneCheckText,emailCheckText,passwordCheck;
    PreferenceHelper preferenceHelper;
    private Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferenceHelper = new PreferenceHelper(this);

        phoneCheckText=findViewById(R.id.joinPhoneCheckText);
        emailCheckText=findViewById(R.id.joinEmailCheckText);
        passwordCheck=findViewById(R.id.joinPasswordCheck);
        etName= findViewById(R.id.joinNameEdit);
        etEmail= findViewById(R.id.joinEmailEdit);
        etPassword= findViewById(R.id.joinPwEdit);
        etRePassword= findViewById(R.id.joinRePwEdit);
        etPhone= findViewById(R.id.joinPhoneEdit);
        finishBtn= findViewById(R.id.joinFinishBtn2);
        joinPhoneCheckBtn= findViewById(R.id.joinPhoneCheckBtn);
        joinEmailCheckBtn=findViewById(R.id.joinEmailCheckBtn);
        cancelBtn= findViewById(R.id.joinCancelButton);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 클릭됨");
                if(SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    switch (view.getId()) {
                        case R.id.joinFinishBtn2:
                            registerMe();
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

                }else{
                    passCheck();
                }

            }
        });

        etRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){

                }else{
                    if(etPassword.getText().toString().equals(etRePassword.getText().toString())){
                        passwordCheck.setText("두 비밀번호가 일치합니다.");
                        passwordCheck.setTextColor(Color.parseColor("#0000FF"));
                    }else{
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
                phoneCheck();
            }
        });

        joinEmailCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailCheck();
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
        final String phone = etPhone.getText().toString();
        String phonePattern = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";


        if(!Pattern.matches(phonePattern,phone)){
            phoneCheckText.setText("올바른 핸드폰 번호 형식이 아닙니다.");
            phoneCheckText.setTextColor(Color.parseColor("#FF0000"));
        }else{
            phoneCheckText.setText("");
            phoneCheckText.setTextColor(Color.parseColor("#000000"));
            Intent intent = new Intent(Register.this,PhoneCertification.class);
            intent.putExtra("phone",phone);
            startActivity(intent);

        }

    }

    private void emailCheck(){
        final String email = etEmail.getText().toString();
//        String emailPattern = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+$";
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

        if(pattern.matcher(email).matches()){
            emailCheckText.setText("");
            emailCheckText.setTextColor(Color.parseColor("#000000"));
            Intent emailIntent = new Intent(Register.this, EmailCertification.class);
            emailIntent.putExtra("email",email);
            startActivity(emailIntent);
        } else {
            emailCheckText.setText("올바른 이메일 형식이 아닙니다.");
            emailCheckText.setTextColor(Color.parseColor("#FF0000"));
        }

//        if(!Pattern.matches(emailPattern,email)){
//            emailCheckText.setText("올바른 이메일 형식이 아닙니다.");
//            emailCheckText.setTextColor(Color.parseColor("#FF0000"));
//        }else{
//            emailCheckText.setText("");
//            emailCheckText.setTextColor(Color.parseColor("#000000"));
//            Intent emailIntent = new Intent(Join.this, EmailCertification.class);
//            emailIntent.putExtra("email",email);
//            startActivity(emailIntent);
//        }
    }

    private void passCheck(){
        final String password = etPassword.getText().toString();
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!.%*#?&])[A-Za-z\\d$@$.!%*#?&]{8,}$";


        if(!Pattern.matches(passwordPattern,password)){
            passwordCheck.setText("올바른 비밀번호 형식이 아닙니다.");
            passwordCheck.setTextColor(Color.parseColor("#FF0000"));
        }else{
            passwordCheck.setText("");
            passwordCheck.setTextColor(Color.parseColor("#000000"));
        }
    }

}