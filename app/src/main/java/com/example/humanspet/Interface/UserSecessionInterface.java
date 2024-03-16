package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserSecessionInterface {

    @FormUrlEncoded
    @POST("userSecession.php")
    Call<String> userSecession(
            @Field("email") String email,
            @Field("name") String name
    );
}
