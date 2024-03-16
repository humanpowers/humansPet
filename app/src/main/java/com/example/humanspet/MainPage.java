package com.example.humanspet;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainPage extends AppCompatActivity {
    String TAG="메인";
    BottomNavigationView bottomNavigationView;
    Fragment first,second,third,fourth,fifth;
    long backPressedTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        bottomNavigationView=findViewById(R.id.bottomNavi);
        first = new Noticeboard();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,first).commit();



       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.btn_fragmentA:
                       if(first ==null){
                           first = new Noticeboard();
                           getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,first).commit();
                       }
                       if(first!=null){getSupportFragmentManager().beginTransaction().show(first).commit();}
                       if(second!=null){getSupportFragmentManager().beginTransaction().hide(second).commit();}
                       if(third!=null){getSupportFragmentManager().beginTransaction().hide(third).commit();}
                       if(fourth!=null){getSupportFragmentManager().beginTransaction().hide(fourth).commit();}
                       if(fifth!=null){getSupportFragmentManager().beginTransaction().hide(fifth).commit();}
                       break;
                   case R.id.btn_fragmentB:
                       if(second ==null){
                           second = new Diary();
                           getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,second).commit();
                       }
                       if(first!=null){getSupportFragmentManager().beginTransaction().hide(first).commit();}
                       if(second!=null){getSupportFragmentManager().beginTransaction().show(second).commit();}
                       if(third!=null){getSupportFragmentManager().beginTransaction().hide(third).commit();}
                       if(fourth!=null){getSupportFragmentManager().beginTransaction().hide(fourth).commit();}
                       if(fifth!=null){getSupportFragmentManager().beginTransaction().hide(fifth).commit();}
                       break;
                   case R.id.btn_fragmentC:
                       if(third ==null){
                           third = new Chatting();
                           getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,third).commit();
                       }
                       if(first!=null){getSupportFragmentManager().beginTransaction().hide(first).commit();}
                       if(second!=null){getSupportFragmentManager().beginTransaction().hide(second).commit();}
                       if(third!=null){getSupportFragmentManager().beginTransaction().show(third).commit();}
                       if(fourth!=null){getSupportFragmentManager().beginTransaction().hide(fourth).commit();}
                       if(fifth!=null){getSupportFragmentManager().beginTransaction().hide(fifth).commit();}
                       break;
                   case R.id.btn_fragmentD:
                       Log.d(TAG, "onNavigationItemSelected: 01111"+fourth);
                       if(fourth ==null){
                           fourth = new Walking();
                           Log.d(TAG, "onNavigationItemSelected: 22222"+fourth);
                           getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,fourth).commit();
                       }
                       if(first!=null){getSupportFragmentManager().beginTransaction().hide(first).commit();}
                       if(second!=null){getSupportFragmentManager().beginTransaction().hide(second).commit();}
                       if(third!=null){getSupportFragmentManager().beginTransaction().hide(third).commit();}
                       if(fourth!=null){getSupportFragmentManager().beginTransaction().show(fourth).commit();}
                       if(fifth!=null){getSupportFragmentManager().beginTransaction().hide(fifth).commit();}
                       break;
                   case R.id.btn_fragmentE:
                       if(fifth ==null){
                           fifth = new MyPage();
                           getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,fifth).commit();
                       }
                       if(first!=null){getSupportFragmentManager().beginTransaction().hide(first).commit();}
                       if(second!=null){getSupportFragmentManager().beginTransaction().hide(second).commit();}
                       if(third!=null){getSupportFragmentManager().beginTransaction().hide(third).commit();}
                       if(fourth!=null){getSupportFragmentManager().beginTransaction().hide(fourth).commit();}
                       if(fifth!=null){getSupportFragmentManager().beginTransaction().show(fifth).commit();}
                       break;
               }

               return true;
           }
       });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        if(backPressedTime+3000>System.currentTimeMillis()){
            super.onBackPressed();
            finish();
        }else{
            Toast.makeText(this, "한번 더 뒤로가기 버튼을 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }

}