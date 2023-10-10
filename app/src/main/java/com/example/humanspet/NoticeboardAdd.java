package com.example.humanspet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.NoticeboardAddInterface;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeboardAdd extends AppCompatActivity {
    String TAG="게시판추가";

    String[] doItem={"서울 특별시","인천 광역시","대전 광역시","대구 광역시","광주 광역시","울산 광역시","부산 광역시","경기도","충청북도","충청남도","전라북도","전라남도","경상북도","경상남도","강원특별자치도","세종특별자치시","제주특별자치도"};
    String[] seoulItem={"종로구","중구","용산구","성동구","광진구","동대문구","중랑구","성북구","강북구","도봉구","노원구","은평구","서대문구","마포구","양천구","강서구","구로구","금천구","영등포구","동작구","관악구","서초구","강남구","송파구","강동구"};
    String[] busanItem={"중구","서구","동구","영도구","부산진구","동래구","남구","북구","해운대구","사하구","금정구","강서구","연제구","수영구","사상구","기장군"};
    String[] daeguItem={"중구","동구","서구","남구","북구","수성구","달서구","달성군","군위군"};
    String[] incheonItem={"중구","동구","미추홀구","연수구","남동구","부평구","계양구","서구","강화구","옹진군"};
    String[] gwangjuItem={"동구","서구","남구","북구","광산구"};
    String[] daejeonItem={"동구","중구","서구","유성구","대덕구"};
    String[] ulsanItem={"중구","남구","동구","북구","울주군"};
    String[] sejongItem={"세종"};
    String[] geounggiItem={"수원시","고양시","용신시","성남시","부천시","화성시","안산시","남양주시","안양시","평택시","시흥시","파주시","의정부시","김포시","광주시","광명시","군포시","하남시","오산시","양주시","이천시","구리시","안성시","포천시","의왕시","양평군","여주시","동두천시","가평군","과천시","연천군"};
    String[] gwangwonItem={"춘천시","원주시","강릉시","동해시","태백시","속초시","삼척시","홍천군","횡성군","영월군","평창군","정선군","철원군","화천군","양구군","인제군","고성군","양양군"};
    String[] chungcheonbukItem={"청주시","충주시","제천시","보은군","옥천군","영동군","증평군","진천군","괴산군","음성군","단양군"};
    String[] chungcheonnamItem={"천안시","공주시","보령시","아산시","서산시","논산시","계룡시","당진시","금산군","부여군","서천군","청양군","홍성군","예산군","태안군"};
    String[] jeollabukItem={"전주시","군산시","익산시","정읍시","남원시","김제시","완주군","진안군","무주군","장수군","임실군","순창군","고창군","부안군"};
    String[] jeollanamItem={"목포시","여수시","순천시","나주시","광양시","담양군","곡성군","구례군","고흥군","보성군","화순군","장흥군","강진군","해남군","영암군","무안군","함평군","영광군","장성군","완도군","진도군","신안군"};
    String[] gyeongsangbukItem={"포항시","경주시","김천시","안동시","구미시","영주시","영천시","상주시","문경시","경산시","의성군","청송군","영양군","영덕군","청도군","고령군","성주군","칠곡군","예천군","봉화군","울진군","울릉군"};
    String[] gyeongsangnamItem={"창원시","진주시","통영시","사천시","김해시","밀양시","거제시","양산시","의령군","함안군","창녕군","고성군","남해군","하동군","산청군","함양군","거창군","합천군"};
    String[] jejuItem={"제주시","서귀포시"};
    Spinner siSpinner;
    ArrayAdapter<String> siAdapter;
    String doText="서울 특별시";
    String siText="종로구";
    EditText titleEdit,contentEdit;
    ImageButton imageBtn,cancelBtn;
    String noticeboardDataUri;
    Button finishBtn;
    String title,content;
    String userId,userName;
    private SharedPreferences preferences;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard_add);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        userName=preferences.getString("USERNAME","");
        Log.d(TAG, "onCreate: userName"+userName);

        titleEdit=findViewById(R.id.noticeboardAddTitleText);
        contentEdit=findViewById(R.id.noticeboardAddContentText);
        imageBtn=findViewById(R.id.noticeboardAddImage);
        cancelBtn=findViewById(R.id.noticeboardAddCancelButton);
        finishBtn=findViewById(R.id.noticeboardAddFinishButton);

        ActivityResultLauncher<Intent> noticeboardLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri noticeboardUri = intent.getData();
                        Log.e(TAG, "uri : " + noticeboardUri);
                        String noticeboardRealUri=getRealPathFromUri(noticeboardUri);
                        Log.e(TAG,"realUri"+noticeboardRealUri);
                        noticeboardDataUri=noticeboardRealUri;
                        Glide.with(this)
                                .load(noticeboardRealUri)
                                .into(imageBtn);
                    }
                });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                noticeboardLauncher.launch(intent);
            }
        });


        Spinner doSpinner=findViewById(R.id.doSpinner);
        ArrayAdapter<String> doAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,doItem);
        doSpinner.setAdapter(doAdapter);


        doSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spinnerSelect(doItem[position]);
                doText=doItem[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title=titleEdit.getText().toString();
                content=contentEdit.getText().toString();
                File noticeboardDataFile= new File(noticeboardDataUri);
                String noticeboardFileName=userId+title+".jpg";

                animationView = findViewById(R.id.noticeboardAddLottie);
                animationView.loop(true);
                animationView.playAnimation();
                animationView.setVisibility(View.VISIBLE);

                RequestBody noticeboardRequestBody=RequestBody.create(MediaType.parse("multipart/form-data"),noticeboardDataFile);
                MultipartBody.Part noticeboardBody = MultipartBody.Part.createFormData("uploaded_file",noticeboardFileName,noticeboardRequestBody);
                Log.d(TAG, "onClick: body: "+noticeboardBody);

                NoticeboardAddInterface noticeboardApi=ApiClient.getApiClient().create(NoticeboardAddInterface.class);
                Call<String> call = noticeboardApi.noticeboardAdd(userId,userName,siText,doText,title,content,noticeboardBody);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("성공")){
                            animationView.setVisibility(View.GONE);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void spinnerSelect(String select){
        siSpinner=findViewById(R.id.siSpinner);
        siAdapter = null;
        if(select.equals("서울 특별시")){
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,seoulItem);
            siText="종로구";
            SiSelect(seoulItem);
        }else if(select.equals("인천 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,incheonItem);
            siText="중구";
            SiSelect(incheonItem);
        }else if(select.equals("대전 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,daejeonItem);
            siText="동구";
            SiSelect(daejeonItem);
        }else if(select.equals("대구 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,daeguItem);
            siText="중구";
            SiSelect(daeguItem);
        }else if(select.equals("광주 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,gwangjuItem);
            siText="동구";
            SiSelect(gwangjuItem);
        }else if(select.equals("울산 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,ulsanItem);
            siText="중구";
            SiSelect(ulsanItem);
        }else if(select.equals("부산 광역시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,busanItem);
            siText="중구";
            SiSelect(busanItem);
        }else if(select.equals("경기도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,geounggiItem);
            siText="수원시";
            SiSelect(geounggiItem);
        }else if(select.equals("충청북도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,chungcheonbukItem);
            siText="청주시";
            SiSelect(chungcheonbukItem);
        }else if(select.equals("충청남도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,chungcheonnamItem);
            siText="천안시";
            SiSelect(chungcheonnamItem);
        }else if(select.equals("전라북도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,jeollabukItem);
            siText="전주시";
            SiSelect(jeollabukItem);
        }else if(select.equals("전라남도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,jeollanamItem);
            siText="목포시";
            SiSelect(jeollanamItem);
        }else if(select.equals("경상북도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,gyeongsangbukItem);
            siText="포항시";
            SiSelect(gyeongsangbukItem);
        }else if(select.equals("경상남도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,gyeongsangnamItem);
            siText="창원시";
            SiSelect(gyeongsangnamItem);
        }else if(select.equals("강원특별자치도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,gwangwonItem);
            siText="춘천시";
            SiSelect(gwangwonItem);
        }else if(select.equals("세종특별자치시")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,sejongItem);
            siText="세종";
            SiSelect(sejongItem);
        }else if(select.equals("제주특별자치도")) {
            siAdapter=new ArrayAdapter<>(this, R.layout.custom_dropdown_item,jejuItem);
            siText="제주시";
            SiSelect(jejuItem);
        }
        siSpinner.setAdapter(siAdapter);
    }

    public void SiSelect(String[] item){
        siSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                siText=item[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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