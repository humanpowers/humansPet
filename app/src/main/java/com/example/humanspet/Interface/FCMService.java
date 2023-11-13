package com.example.humanspet.Interface;

import com.example.humanspet.FCMNotification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAMzgJ_eQ:APA91bEZPmq8k1DBkg-_h9IyA5CS4-l0ZBaD4LCcOCgvrFzqkr2_EpvMWt-Y3NccttDXP6YgdlHwU5CdWiLNBhrUMCJzNRxUvU1xW-tq4M3cnQolFiB27XAQTotkF5a5SNExf2Qknepl"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body FCMNotification notification);
}
