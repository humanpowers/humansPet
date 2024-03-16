package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ExistenceEmailInterface {
    @FormUrlEncoded
    @POST("existenceEmail.php")
    Call<String> existenceEmail(
            @Field("email") String email
    );
}
