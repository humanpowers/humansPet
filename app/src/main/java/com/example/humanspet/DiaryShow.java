package com.example.humanspet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryDeleteInterface;
import com.example.humanspet.Interface.DiaryUpdateInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryShow extends AppCompatActivity {
    String TAG="diaryShow";
    String title,content,image,userId;
    ImageView imageView;
    TextView titleText;
    EditText contentText;
    ApiClient apiClient=new ApiClient();
    private SharedPreferences preferences;
    String beforeContent="";
    LottieAnimationView animationView;
    ImageButton checkBtn,cancelBtn,deleteBtn,correctionBtn,finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_show);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");

        Intent getIntent=getIntent();
        title=getIntent.getStringExtra("title");
        content=getIntent.getStringExtra("content");
        image=getIntent.getStringExtra("image");
        String trimContent = content.trim();

        cancelBtn=findViewById(R.id.showDiaryCancelButton);
        finishBtn=findViewById(R.id.showDiaryFinishButton);

        imageView=findViewById(R.id.showDiaryImage);
        titleText=findViewById(R.id.showDiaryTitleEdit);
        contentText=findViewById(R.id.showDiaryContentEdit);
        contentText.setEnabled(false);

        titleText.setText(title);
        contentText.setText(trimContent);
        Glide.with(DiaryShow.this).load("http://"+apiClient.goUri(image)).into(imageView);

        checkBtn=findViewById(R.id.showDiaryCheckButton);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteBtn=findViewById(R.id.showDiaryDeleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(DiaryShow.this);
                dlg.setTitle("삭제");
                dlg.setMessage("정말로 삭제하시겠습니까?");
                dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DiaryDeleteInterface deleteApi= ApiClient.getApiClient().create(DiaryDeleteInterface.class);
                        Call<String> deleteCall = deleteApi.diaryDelete(userId,title);
                        deleteCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.d(TAG, "onResponse: "+response.body());
                                finish();
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

        correctionBtn=findViewById(R.id.showDiaryCorrectionButton);
        correctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView diaryShowTextCount=findViewById(R.id.diaryShowCountText);
                diaryShowTextCount.setVisibility(View.VISIBLE);
                int contentLineCount=contentText.getLineCount();
                String contentTextCount=contentText.getText().toString();
                diaryShowTextCount.setText(contentLineCount+"/30\n"+contentTextCount.length()+"/500");
                contentText.setEnabled(true);
                contentText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String text = charSequence.toString();
                        int lineCount = contentText.getLineCount();
                        if(lineCount>30){
                            contentText.setText(text.substring(0,text.length()-1));
                            contentText.setSelection(contentText.getText().length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int lineCount = contentText.getLineCount();
                        String textCount = contentText.getText().toString();
                        diaryShowTextCount.setText(lineCount+"/30\n"+textCount.length()+"/500");
                    }
                });
                Toast.makeText(DiaryShow.this, "본문을 클릭하여 수정해주세요.", Toast.LENGTH_SHORT).show();
                checkBtn.setVisibility(View.INVISIBLE);
                correctionBtn.setVisibility(View.INVISIBLE);
                deleteBtn.setVisibility(View.INVISIBLE);
                finishBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder cancelDlg = new AlertDialog.Builder(DiaryShow.this);
                        cancelDlg.setTitle("취소");
                        cancelDlg.setMessage("수정을 취소하시겠습니까?");
                        cancelDlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentText.setText(trimContent);
                                contentText.setEnabled(false);
                                checkBtn.setVisibility(View.VISIBLE);
                                correctionBtn.setVisibility(View.VISIBLE);
                                deleteBtn.setVisibility(View.VISIBLE);
                                finishBtn.setVisibility(View.INVISIBLE);
                                cancelBtn.setVisibility(View.INVISIBLE);
                                diaryShowTextCount.setVisibility(View.GONE);
                            }
                        });
                        cancelDlg.setNegativeButton("아니요",null);
                        cancelDlg.show();
                    }
                });

                finishBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diaryShowTextCount.setVisibility(View.GONE);
                        contentText=findViewById(R.id.showDiaryContentEdit);
                        String afterContent=contentText.getText().toString();
                        Log.d(TAG, "onClick: afterText: "+afterContent);
                        Log.d(TAG, "onClick: beforeText: "+beforeContent);
                        Log.d(TAG, "onClick: title: "+title);

                        int lineCount = contentText.getLineCount();
                        if(lineCount>30){
                            Toast.makeText(DiaryShow.this,"본문은 30줄까지만 입력 가능합니다.",Toast.LENGTH_SHORT).show();
                        }else {
                            animationView = findViewById(R.id.showDiaryLottie);
                            animationView.loop(true);
                            animationView.playAnimation();
                            animationView.setVisibility(View.VISIBLE);

                            DiaryUpdateInterface api = ApiClient.getApiClient().create(DiaryUpdateInterface.class);
                            Call<String> call = api.diaryUpdate(title, afterContent);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.d(TAG, "onResponse: " + response.body());
                                    if (response.body().equals("성공")) {
                                        animationView.setVisibility(View.GONE);
                                        contentText.setEnabled(false);
                                        checkBtn.setVisibility(View.VISIBLE);
                                        correctionBtn.setVisibility(View.VISIBLE);
                                        deleteBtn.setVisibility(View.VISIBLE);
                                        finishBtn.setVisibility(View.INVISIBLE);
                                        cancelBtn.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });
                        }

                    }
                });
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
}