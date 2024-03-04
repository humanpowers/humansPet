package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FindIdInterface {

    @FormUrlEncoded
    @POST("findId.php")
    Call<String> getUserId(
            @Field("phone") String phone
    );
}
