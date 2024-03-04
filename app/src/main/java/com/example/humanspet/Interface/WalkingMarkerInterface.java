package com.example.humanspet.Interface;

import com.example.humanspet.WalkingMarkerItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WalkingMarkerInterface {

    @FormUrlEncoded
    @POST("walkingMarker.php")
    Call<ArrayList<WalkingMarkerItem>> walkingMarker(
            @Field("userId") String userId
    );
}
