package com.example.humanspet;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryAddInterface;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetAdd extends AppCompatActivity {
    String TAG="펫추가";
    String userId;
    EditText nameEdit,sexEdit,birthEdit,weightEdit,kindEdit,typesEdit,registrationEdit;
    String name,sex,birth,weight,types,kind,registration;
    Button checkBtn,cancelBtn;
    ImageButton imageBtn;
    String dataUri;
    LottieAnimationView animationView;
    int imageCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_add);
        imageCheck=0;

        Intent getIntent=getIntent();
        userId=getIntent.getStringExtra("USERID");

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri uri = intent.getData();
                        Log.e(TAG, "uri : " + uri);
                        String realUri=getRealPathFromUri(uri);
                        Log.e(TAG,"realUri"+realUri);
                        dataUri=realUri;
                        Glide.with(this)
                                .load(realUri)
                                .into(imageBtn);
                        imageCheck++;
                    }
                });

        imageBtn=findViewById(R.id.petAddImageButton);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 다이어리 사진 누름");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                launcher.launch(intent);
            }
        });

        checkBtn=findViewById(R.id.petAddCheckButton);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEdit=findViewById(R.id.petAddNameEdit);
                name=nameEdit.getText().toString();
                sexEdit=findViewById(R.id.petAddSexEdit);
                sex=sexEdit.getText().toString();
                birthEdit=findViewById(R.id.petAddBirthEdit);
                birth=birthEdit.getText().toString();
                weightEdit=findViewById(R.id.petAddWeightEdit);
                weight=weightEdit.getText().toString();
                typesEdit=findViewById(R.id.petAddTypesEdit);
                types=typesEdit.getText().toString();
                kindEdit=findViewById(R.id.petAddKindEdit);
                kind=kindEdit.getText().toString();
                registrationEdit=findViewById(R.id.petAddRegistrationNumberEdit);
                registration=registrationEdit.getText().toString();
                if(name.trim().equals("")){
                    Toast.makeText(PetAdd.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(sex.trim().equals("")){
                    Toast.makeText(PetAdd.this, "성별을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(birth.trim().equals("")){
                    Toast.makeText(PetAdd.this, "생일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(weight.trim().equals("")){
                    Toast.makeText(PetAdd.this, "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(types.trim().equals("")){
                    Toast.makeText(PetAdd.this, "동물과를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(kind.trim().equals("")){
                    Toast.makeText(PetAdd.this, "품종을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(registration.trim().equals("")){
                    Toast.makeText(PetAdd.this, "등록번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(imageCheck==0){
                    Toast.makeText(PetAdd.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    File dataFile= new File(dataUri);
                    Log.d(TAG, "onClick: dataFile: "+dataFile);
                    String fileName=name+".jpg";
                    Log.d(TAG, "onClick: fileName: "+fileName);

                    RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"),dataFile);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody);
                    Log.d(TAG, "onClick: body: "+body);

                    animationView = findViewById(R.id.petAddLottie);
                    animationView.loop(true);
                    animationView.playAnimation();
                    animationView.setVisibility(View.VISIBLE);

                    DiaryAddInterface api = ApiClient.getApiClient().create(DiaryAddInterface.class);
                    Call<String> call = api.getDiaryAdd(userId,name,sex,birth,weight,types,kind,registration,body);
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

        cancelBtn=findViewById(R.id.petAddCancelButton);
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