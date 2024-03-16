package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PasswordChangeInterface {
    @FormUrlEncoded
    @POST("passwordChange.php")
    Call<String> passwordChange(
            @Field("email") String email,
            @Field("password") String password
    );
}
