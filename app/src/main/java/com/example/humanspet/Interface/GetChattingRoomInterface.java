package com.example.humanspet.Interface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetChattingRoomInterface {
    @Multipart
    @POST("getChatRoom.php")
    Call<ArrayList> GetChattingRoom(
            @Part("name") String name
    );
}
