package com.example.humanspet.Interface;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ChattingRoomInterface {
    @Multipart
    @POST("chatRoom.php")
    Call<String> ChattingRoomAdd(
            @Part("name") String name,
            @Part("otherName") String otherName
    );
}
