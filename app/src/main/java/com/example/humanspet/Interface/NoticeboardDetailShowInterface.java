package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NoticeboardDetailShowInterface {
    @FormUrlEncoded
    @POST("noticeboardDetailShow.php")
    Call<String> noticeboardDetailShow(
            @Field("userId") String userId,
            @Field("title") String title
    );
}
