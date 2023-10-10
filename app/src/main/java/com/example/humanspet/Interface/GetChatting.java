package com.example.humanspet.Interface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetChatting {

    @Multipart
    @POST("getChat.php")
    Call<ArrayList> GetChatting(
            @Part("name") String name,
            @Part("otherName") String otherName
    );
}
