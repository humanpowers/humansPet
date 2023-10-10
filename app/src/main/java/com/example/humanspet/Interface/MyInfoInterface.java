package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyInfoInterface {
    @FormUrlEncoded
    @POST("myInfo.php")
    Call<String> getUserInfo(
            @Field("email") String email
    );
}
