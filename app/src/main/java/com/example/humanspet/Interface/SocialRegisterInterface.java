package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SocialRegisterInterface {

    @FormUrlEncoded
    @POST("socialRegister.php")
    Call<String> socialRegister(
            @Field("name") String name,
            @Field("email") String email,
            @Field("type") String type
    );
}
