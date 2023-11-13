package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LikesBtnClickInterface {
    @Multipart
    @POST("likesClick.php")
    Call<String> LikesChick(
            @Part("userId") String userId,
            @Part("title") String title
    );
}
