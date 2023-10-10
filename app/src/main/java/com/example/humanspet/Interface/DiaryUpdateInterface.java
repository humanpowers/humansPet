package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface DiaryUpdateInterface {

    @FormUrlEncoded
    @POST("diaryUpdate.php")
    Call<String> diaryUpdate(
            @Field("title") String title,
            @Field("afterContent") String afterContent
    );
}
