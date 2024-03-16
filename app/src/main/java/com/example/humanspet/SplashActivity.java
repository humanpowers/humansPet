package com.example.humanspet;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView upFirstImage,upSecondImage,upThirdImage,downFirstImage,downSecondImage,downThirdImage;
    ImageView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashInit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                downFirstImage.setVisibility(View.GONE);
                downSecondImage.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downSecondImage.setVisibility(View.GONE);
                        downThirdImage.setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upFirstImage.setVisibility(View.GONE);
                                upSecondImage.setVisibility(View.VISIBLE);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        upSecondImage.setVisibility(View.GONE);
                                        upThirdImage.setVisibility(View.VISIBLE);
                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                splashText.setVisibility(View.VISIBLE);
                                                ObjectAnimator animator = ObjectAnimator.ofFloat(splashText, "translationY", -1000, -300, 0);
                                                animator.setDuration(1000); // 애니메이션 지속 시간
                                                animator.setInterpolator(new BounceInterpolator()); // Bounce interpolator 사용
                                                animator.start();
                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(SplashActivity.this, Login.class);
                                                        startActivity(intent);

                                                        // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
                                                        // 이동한 다음 사용안함으로 finish 처리
                                                        finish();
                                                    }
                                                }, 1000); // 시간 0.5초 이후 실행


                                            }
                                        }, 500); // 시간 0.5초 이후 실행

                                    }
                                }, 500); // 시간 0.5초 이후 실행

                            }
                        }, 500); // 시간 0.5초 이후 실행

                    }
                }, 500); // 시간 0.5초 이후 실행

            }
        }, 500); // 시간 0.5초 이후 실행

    }

    private void splashInit(){
        upFirstImage = findViewById(R.id.splashUpFirstImage);
        upSecondImage = findViewById(R.id.splashUpSecondImage);
        upThirdImage = findViewById(R.id.splashUpThirdImage);
        downFirstImage = findViewById(R.id.splashDownFirstImage);
        downSecondImage = findViewById(R.id.splashDownSecondImage);
        downThirdImage = findViewById(R.id.splashDownThirdImage);
        splashText = findViewById(R.id.splashText);
    }
}