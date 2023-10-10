package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ChatInterface {
    @Multipart
    @POST("chat.php")
    Call<String> ChattingAdd(
            @Part("id") String id,
            @Part("name") String name,
            @Part("image") String image,
            @Part("message") String message,
            @Part("date") String date,
            @Part("otherName") String otherName
    );
}
