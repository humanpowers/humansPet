package com.example.humanspet;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance=this;
        KakaoSdk.init(this,"1408735266089265aa75c705e6224a1b");
    }

}
