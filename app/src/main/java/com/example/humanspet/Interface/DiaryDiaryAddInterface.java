package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DiaryDiaryAddInterface {
    @Multipart
    @POST("diaryDiaryAdd.php")
    Call<String> getDiaryDiaryAdd(
            @Part("id") String id,
            @Part("name") String name,
            @Part("title") String title,
            @Part("content") String content,
            @Part("date") String date,
            @Part MultipartBody.Part file
    );

}
