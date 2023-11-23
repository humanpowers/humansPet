package com.example.humanspet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.humanspet.Interface.RegisterInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    String TAG="조인";
    EditText etName,etEmail,etPhone,etPassword,etRePassword;
    Button finishBtn,cancelBtn,joinPhoneCheckBtn;
    TextView phoneCheckText,passwordCheck,passwordReCheck;
    PreferenceHelper preferenceHelper;
    private Long mLastClickTime = 0L;
    boolean passwordBoolean,nameBoolean,phoneBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        passwordBoolean=false;
        nameBoolean=false;
        phoneBoolean=false;

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

        etEmail.setText(id);
        etEmail.setEnabled(false);

        focusOn(etEmail);
        focusOn(etPhone);

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
                    if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true){
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
                            if(nameBoolean==true&&phoneBoolean==true&&passwordBoolean==true){
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
                        if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true){
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
                if(phoneBoolean==true){
                    final String phone = etPhone.getText().toString();
                    Intent intent = new Intent(Register.this,PhoneCertification.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
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
            if(passwordBoolean==true&&phoneBoolean==true&&nameBoolean==true){
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

}