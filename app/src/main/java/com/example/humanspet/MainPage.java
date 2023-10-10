package com.example.humanspet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainPage extends AppCompatActivity {
    String tag="메인";
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        bottomNavigationView=findViewById(R.id.bottomNavi);

       getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,new Noticeboard()).commit();

       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.btn_fragmentA:
                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Noticeboard()).commit();
                       break;
                   case R.id.btn_fragmentB:
                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Diary()).commit();
                       break;
                   case R.id.btn_fragmentC:
                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Chatting()).commit();
                       break;
                   case R.id.btn_fragmentD:
                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Walking()).commit();
                       break;
                   case R.id.btn_fragmentE:
                       getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new MyPage()).commit();
                       break;
               }

               return true;
           }
       });

    }

}