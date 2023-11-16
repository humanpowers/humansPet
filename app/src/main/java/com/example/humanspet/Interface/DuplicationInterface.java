package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DuplicationInterface {

    @FormUrlEncoded
    @POST("duplication.php")
    Call<String> doDuplication(
            @Field("email") String email
    );
}
