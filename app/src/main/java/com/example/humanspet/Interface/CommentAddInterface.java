package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CommentAddInterface {
    @Multipart
    @POST("commentAdd.php")
    Call<String> CommentAdd(
            @Part("name") String name,
            @Part("image") String image,
            @Part("comment") String comment,
            @Part("title") String title,
            @Part("writer") String writer
    );
}
