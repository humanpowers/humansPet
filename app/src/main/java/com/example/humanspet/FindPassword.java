package com.example.humanspet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FindPassword extends AppCompatActivity {
    TextView emailText,emailCheckText,codeText,codeTimeText,passwordText,passwordCheckText,rePasswordText;
    EditText emailEdit,codeEdit,passwordEdit,rePasswordEdit;
    Button emailCheckBtn,codeBtn,nextBtn,finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        idMatch();


    }

    public void passwordEdit(){
        emailText.setVisibility(View.GONE);
        emailEdit.setVisibility(View.GONE);
        emailCheckText.setVisibility(View.GONE);
        emailCheckBtn.setVisibility(View.GONE);
        codeText.setVisibility(View.GONE);
        codeEdit.setVisibility(View.GONE);
        codeTimeText.setVisibility(View.GONE);
        codeBtn.setVisibility(View.GONE);
        passwordText.setVisibility(View.VISIBLE);
        passwordEdit.setVisibility(View.VISIBLE);
        passwordCheckText.setVisibility(View.VISIBLE);
        rePasswordEdit.setVisibility(View.VISIBLE);
        rePasswordText.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.GONE);
        finishBtn.setVisibility(View.VISIBLE);
    }

    public void idMatch(){
        passwordText = findViewById(R.id.findPasswordPasswordText);
        passwordEdit = findViewById(R.id.findPasswordPasswordEdit);
        passwordCheckText = findViewById(R.id.findPasswordPasswordReCheck);
        rePasswordText = findViewById(R.id.findPasswordRePasswordText);
        rePasswordEdit = findViewById(R.id.findPasswordRePasswordEdit);
        emailText = findViewById(R.id.findPasswordEmailText);
        emailEdit = findViewById(R.id.findPasswordEmailEdit);
        emailCheckText = findViewById(R.id.findPasswordEmailCheckText);
        emailCheckBtn = findViewById(R.id.findPasswordSendButton);
        codeText = findViewById(R.id.findPasswordCodeText);
        codeEdit = findViewById(R.id.findPasswordCodeEdit);
        codeTimeText = findViewById(R.id.findPasswordCodeTime);
        codeBtn = findViewById(R.id.findPasswordCodeButton);
        nextBtn = findViewById(R.id.findPasswordNextButton);
        finishBtn = findViewById(R.id.findPasswordFinishButton);
    }
}