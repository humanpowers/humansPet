package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendPushMessage {
    @Multipart
    @POST("sendPush.php")
    Call<String> SendPush(
            @Part("userId") String userId
    );
}
