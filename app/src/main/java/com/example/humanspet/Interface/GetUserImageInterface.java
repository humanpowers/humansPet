package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetUserImageInterface {
    @Multipart
    @POST("getUserImage.php")
    Call<String> getUserImage(
            @Part("name") String name
    );
}
