package com.example.humanspet.Interface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CommentShowInterface {
    @Multipart
    @POST("commentShow.php")
    Call<ArrayList> CommentShow(
            @Part("title") String title,
            @Part("writer") String writer
    );
}
