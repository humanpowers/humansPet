package com.example.humanspet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryRecyclerViewInterface;
import com.example.humanspet.Interface.WalkingDiaryInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkingDiary extends AppCompatActivity {

    String TAG = "산책다이어리";
    Button previousBtn;
    RecyclerView petRecyclerView,diaryRecyclerView;
    LinearLayoutManager linearLayoutManager,walkingDiaryManager;
    ArrayList<DiaryInfoItem> diaryInfoItemArrayList = new ArrayList<>();
    DiaryInfoAdapter diaryInfoAdapter;
    WalkingDiaryAdapter walkingDiaryAdapter;
    ArrayList<WalkingDiaryItem> walkingDiaryItems = new ArrayList<>();

    private SharedPreferences preferences;
    String userId;
    ArrayList responseArray;
    int selectPosition=0;
    ArrayList walkingResponseArray;
    String firstName;
    ImageView map;
    ImageButton mapCancelBtn;
    ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_diary);

        preferences=getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");

        previousBtn=findViewById(R.id.walkingDiaryPreviousButton);
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        petRecyclerView.setLayoutManager(linearLayoutManager);

        diaryInfoAdapter =new DiaryInfoAdapter(diaryInfoItemArrayList);
        petRecyclerView.setAdapter(diaryInfoAdapter);

        diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        walkingDiaryManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        diaryRecyclerView.setLayoutManager(walkingDiaryManager);

        walkingDiaryAdapter = new WalkingDiaryAdapter(walkingDiaryItems);
        diaryRecyclerView.setAdapter(walkingDiaryAdapter);

        DiaryRecyclerViewInterface api = ApiClient.getApiClient().create(DiaryRecyclerViewInterface.class);
        Call<ArrayList> call= api.getUserDiary(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                responseArray=response.body();
                for(int i=0;i<responseArray.size();i++){
                    String responseSt= String.valueOf(responseArray.get(i));
                    String[] responseSp=responseSt.split(", ");
                    String[] nameSp=responseSp[0].split("");
                    String nameSt="";
                    for(int j=1;j<responseSp[0].length();j++){
                        nameSt+=nameSp[j];
                    }
                    DiaryInfoItem diaryInfoItem =new DiaryInfoItem(responseSp[1],nameSt);
                    diaryInfoItemArrayList.add(diaryInfoItem);
                    diaryInfoAdapter.notifyDataSetChanged();
                    if(i==0){
                        firstName=nameSt;
                    }
                }

                WalkingDiaryInterface walkingApi=ApiClient.getApiClient().create(WalkingDiaryInterface.class);
                Call<ArrayList> walkingCall = walkingApi.walkingDiary(userId);
                walkingCall.enqueue(new Callback<ArrayList>() {
                    @Override
                    public void onResponse(Call<ArrayList> walkingCall, Response<ArrayList> walkingResponse) {
                        walkingResponseArray=walkingResponse.body();
                        Log.d(TAG, "onResponse: "+walkingResponseArray.get(1));
                        for(int i=0;i<walkingResponseArray.size();i++){
                            String walkingDiary = String.valueOf(walkingResponseArray.get(i));
                            String[] walkingDiarySp = walkingDiary.split(", ");

                            String name = walkingDiarySp[0];
                            String[] nameSp = name.split("");
                            String nameSt="";
                            for(int j=1;j<nameSp.length;j++){
                                nameSt+=nameSp[j];
                            }

                            String date = walkingDiarySp[5];
                            String[] dateSp = date.split("");
                            String dateSt = "";
                            for(int j=0;j<17;j++){
                                if(j==4){
                                    dateSt+="년 ";
                                }else if(j==7){
                                    dateSt+="월 ";
                                }else if(j==10){
                                    dateSt+="일 ";
                                }else if(j==13){
                                    dateSt+="시 ";
                                }else if(j==16){
                                    dateSt+="분 ";
                                }else{
                                    dateSt+=dateSp[j];
                                }
                            }
                            if(nameSt.equals(firstName)){
                                WalkingDiaryItem walkingDiaryItem = new WalkingDiaryItem(dateSt,walkingDiarySp[2],walkingDiarySp[3],walkingDiarySp[1],walkingDiarySp[4]);
                                walkingDiaryItems.add(walkingDiaryItem);
                            }
                        }
                        walkingDiaryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ArrayList> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });

        diaryInfoAdapter.setOnClickListener(new DiaryInfoAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                selectPosition=position;
            }
        });
        apiClient=new ApiClient();
        map = findViewById(R.id.walkingDiaryMap);
        walkingDiaryAdapter.setOnClickListener(new WalkingDiaryAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                mapOn();
                Log.d(TAG, "onImageButtonClicker: "+walkingDiaryItems.get(position).getImage());
                Glide.with(WalkingDiary.this).load("http://"+apiClient.goUri(walkingDiaryItems.get(position).getImage())).thumbnail(Glide.with(map).load(R.raw.loadinggif)).override(1000,1000).into(map);
            }
        });

        mapCancelBtn=findViewById(R.id.walkingDiaryMapCancel);
        mapCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapOff();
            }
        });
    }

    public void mapOn(){
        ImageButton mapCancelBtn = findViewById(R.id.walkingDiaryMapCancel);
        ImageView map = findViewById(R.id.walkingDiaryMap);
        TextView walkingDiaryText = findViewById(R.id.walkingDiaryText);
        Button previousBtn = findViewById(R.id.walkingDiaryPreviousButton);
        RecyclerView petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        RecyclerView diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        mapCancelBtn.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);
        walkingDiaryText.setVisibility(View.GONE);
        previousBtn.setVisibility(View.GONE);
        petRecyclerView.setVisibility(View.GONE);
        diaryRecyclerView.setVisibility(View.GONE);
    }

    public void mapOff(){
        ImageButton mapCancelBtn = findViewById(R.id.walkingDiaryMapCancel);
        ImageView map = findViewById(R.id.walkingDiaryMap);
        TextView walkingDiaryText = findViewById(R.id.walkingDiaryText);
        Button previousBtn = findViewById(R.id.walkingDiaryPreviousButton);
        RecyclerView petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        RecyclerView diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        mapCancelBtn.setVisibility(View.GONE);
        map.setVisibility(View.GONE);
        walkingDiaryText.setVisibility(View.VISIBLE);
        previousBtn.setVisibility(View.VISIBLE);
        petRecyclerView.setVisibility(View.VISIBLE);
        diaryRecyclerView.setVisibility(View.VISIBLE);
    }
}