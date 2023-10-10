package com.example.humanspet.Interface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DiaryDiaryRecyclerViewInterface {
    @FormUrlEncoded
    @POST("diaryDiaryRecyclerView.php")
    Call<ArrayList> getUserPetDiary(
            @Field("userId") String userId
    );
}
