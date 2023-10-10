package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserImageChangeInterface {
    @Multipart
    @POST("userImageAdd.php")
    Call<String> UserImageChange(
            @Part("id") String id,
            @Part MultipartBody.Part file
    );
}
