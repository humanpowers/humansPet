package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserPeristalsisInterface {
    @FormUrlEncoded
    @POST("userPeristalsis.php")
    Call<String> userPeristalsis(
            @Field("email") String email
    );
}
