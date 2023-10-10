package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NoticeboardAddInterface {

    @Multipart
    @POST("noticeboardAdd.php")
    Call<String> noticeboardAdd(
            @Part("id") String id,
            @Part("name") String name,
            @Part("si") String siName,
            @Part("do") String doName,
            @Part("title") String title,
            @Part("content") String content,
            @Part MultipartBody.Part file
    );
}
