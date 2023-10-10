package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DiaryAddInterface {

    @Multipart
    @POST("diaryAdd.php")
    Call<String> getDiaryAdd(
            @Part("id") String id,
            @Part("name") String name,
            @Part("sex") String sex,
            @Part("birth") String birth,
            @Part("weight") String weight,
            @Part("types") String types,
            @Part("kind") String kind,
            @Part("registrationNumber") String registrationNumber,
            @Part MultipartBody.Part file
    );


}
