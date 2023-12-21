package com.example.humanspet.Interface;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WalkingPetInterface {
    @Multipart
    @POST("walkingPet.php")
    Call<String> walkingPet(
            @Part("userId") String userId,
            @Part("petName") String petName,
            @Part("distance") String distance,
            @Part("time") String time,
            @Part("calorie") String calorie,
            @Part("date") String date,
            @Part MultipartBody.Part file
    );
}
