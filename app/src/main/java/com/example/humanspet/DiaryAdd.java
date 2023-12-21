package com.example.humanspet;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryDiaryAddInterface;

import java.io.File;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryAdd extends AppCompatActivity {
    String TAG="addDiary";
    Button checkBtn,cancelBtn;
    EditText titleEdit,contentEdit;
    ImageButton imageAddBtn;
    private SharedPreferences preferences;
    String diaryDataUri;
    String userId,petName;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_add);

        Intent getIntent = getIntent();
        petName= getIntent.getStringExtra("petName");
        Log.d(TAG, "onCreate: petName: "+petName);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        Log.d(TAG, "onCreate: userid: "+userId);

        ActivityResultLauncher<Intent> diaryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri diaryUri = intent.getData();
                        Log.e(TAG, "uri : " + diaryUri);
                        String diaryRealUri=getRealPathFromUri(diaryUri);
                        Log.e(TAG,"realUri"+diaryRealUri);
                        diaryDataUri=diaryRealUri;
                        Glide.with(this)
                                .load(diaryRealUri)
                                .into(imageAddBtn);
                    }
                });

        TextView textCountText=findViewById(R.id.textCountText);

        contentEdit=findViewById(R.id.addDiaryContentEdit);
        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                int lineCount = contentEdit.getLineCount();
                if(lineCount>30){
                    contentEdit.setText(text.substring(0,text.length()-1));
                    contentEdit.setSelection(contentEdit.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lineCount = contentEdit.getLineCount();
                String textCount = contentEdit.getText().toString();
                textCountText.setText(lineCount+"/30\n"+textCount.length()+"/500");

            }
        });

        imageAddBtn=findViewById(R.id.addDiaryImageButton);
        imageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 다이어리 사진 누름");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                diaryLauncher.launch(intent);
            }
        });

        checkBtn=findViewById(R.id.addDiaryCheckButton);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                titleEdit=findViewById(R.id.addDiaryTitleEdit);
                String title=titleEdit.getText().toString();
                contentEdit=findViewById(R.id.addDiaryContentEdit);
                String content=contentEdit.getText().toString();
                int lineCount = contentEdit.getLineCount();
                if(lineCount>30){
                    Toast.makeText(DiaryAdd.this,"본문은 30줄까지만 입력 가능합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    animationView = findViewById(R.id.addDiaryLottie);
                    animationView.loop(true);
                    animationView.playAnimation();
                    animationView.setVisibility(View.VISIBLE);

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String todayDate=format.format(date);
                    Log.d(TAG, "오늘날짜 "+todayDate);

                    Log.d(TAG, "onClick: "+diaryDataUri);
                    File diaryDataFile= new File(diaryDataUri);
                    Log.d(TAG, "onClick: dataFile: "+diaryDataFile);
                    String diaryFileName=title+".jpg";
                    Log.d(TAG, "onClick: fileName: "+diaryFileName);

                    RequestBody diaryRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),diaryDataFile);
                    MultipartBody.Part diaryBody = MultipartBody.Part.createFormData("uploaded_file",diaryFileName,diaryRequestBody);
                    Log.d(TAG, "onClick: body: "+diaryBody);

                    DiaryDiaryAddInterface api = ApiClient.getApiClient().create(DiaryDiaryAddInterface.class);
                    Call<String> call = api.getDiaryDiaryAdd(userId,petName,title,content,todayDate,diaryBody);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d(TAG, "onResponse: "+response.body());
                            if(response.body().equals("성공")){
                                finish();
                            }else{
                                Log.d(TAG, "onResponse: 실패");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG,"에러 = " + t.getMessage());
                        }
                    });
                }
            }
        });

        cancelBtn=findViewById(R.id.addDiaryCancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

}