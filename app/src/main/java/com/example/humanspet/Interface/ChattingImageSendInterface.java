package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ChattingImageSendInterface {
    @Multipart
    @POST("chattingImageSend.php")
    Call<String> chattingImageSend(
            @Part("id") String id,
            @Part("name") String name,
            @Part("image") String image,
            @Part("date") String date,
            @Part("otherName") String otherName,
            @Part("time") String time,
            @Part("read") String read,
            @Part MultipartBody.Part file
    );
}
