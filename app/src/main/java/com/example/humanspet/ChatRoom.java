package com.example.humanspet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.humanspet.Interface.ChatInterface;
import com.example.humanspet.Interface.ChattingRoomInterface;
import com.example.humanspet.Interface.GetChatting;
import com.example.humanspet.Interface.MyInfoInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoom extends AppCompatActivity {
    String TAG = "소켓";
    private EditText messageEditText;
    private Button sendButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private SharedPreferences preferences;
    private String userId,userName,otherName,userImage;
    private RecyclerView recyclerView;
    ArrayList<ChatItem> chatArrayList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    ChattingAdapter chattingAdapter;
    ArrayList getChatList;
    ApiClient apiClient = new ApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Log.d(TAG,"onCreate호출");

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userName=preferences.getString("USERNAME","");

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        otherName=intent.getStringExtra("otherName");

        MyInfoInterface getImage = ApiClient.getApiClient().create(MyInfoInterface.class);
        Call<String> getImageApi=getImage.getUserInfo(userId);
        getImageApi.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String userInfo=response.body();
                String[] userInfoSp=userInfo.split("!!@!!");
                userImage=userInfoSp[2];
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        GetChatting chatApi=ApiClient.getApiClient().create(GetChatting.class);
        Call<ArrayList> chatCall = chatApi.GetChatting(userName,otherName);
        chatCall.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                Log.d(TAG, "onResponse: "+response.body());
                getChatList=response.body();
                for(int i=0;i<getChatList.size();i++){
                    String getChatSt= String.valueOf(getChatList.get(i));
                    Log.d(TAG, "onResponse: chat"+getChatSt);
                    String[] getChatSp=getChatSt.split(", ");
                    String nameSt="";
                    String[] nameSp=getChatSp[0].split("");
                    for(int j=1;j<nameSp.length;j++){
                        nameSt+=nameSp[j];
                    }
                    String messageSt="";
                    String[] messageSp=getChatSp[3].split("");
                    for(int j=0;j<messageSp.length-1;j++){
                        messageSt+=messageSp[j];
                    }
                    ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(getChatSp[1]),nameSt,messageSt,getChatSp[2]);
                    chatArrayList.add(chatItem);
                    chattingAdapter.notifyDataSetChanged();
                }
                recyclerView.scrollToPosition(chatArrayList.size()-1);
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });

        Log.d(TAG, "onCreate id: "+userId);
        Log.d(TAG, "onCreate name: "+userName);

        recyclerView=findViewById(R.id.chattingRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        chattingAdapter =new ChattingAdapter(chatArrayList,userName);
        recyclerView.setAdapter(chattingAdapter);

        TextView title = findViewById(R.id.chatTitle);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        title.setText(otherName);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        new ConnectToServerTask().execute();

        MyInfoInterface infoApi=ApiClient.getApiClient().create(MyInfoInterface.class);
        Call<String> infoCall = infoApi.getUserInfo(userId);
        infoCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart호출");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume호출");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause호출");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop호출");
        super.onStop();
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy호출");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG,"onRestart호출");
        super.onRestart();
    }
    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if (!message.isEmpty() && out != null) {
            Log.d(TAG, "sendMessage: 들어옴"+message);
            new Thread(){
                public void run(){
                    out.println(userName+"!@!"+message+"!@!"+otherName);
                }
            }.start();
            messageEditText.setText("");
        }
    }

    private class ConnectToServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + ": 소켓쓰레드");
                socket = new Socket("3.22.63.114", 8888); // 서버 IP 주소와 포트 번호를 서버에 맞게 변경
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + ": 소켓아래");
                out = new PrintWriter(socket.getOutputStream(), true);
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + out);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(userName);
                String message;
                while ((message = in.readLine()) != null) {
                    Log.d(TAG, "doInBackground: in됨: "+in);
                    updateChatView(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: 소켓연결실패");
            }
            return null;
        }
    }

    private void updateChatView(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] messageSp=message.split(":");
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat format = new SimpleDateFormat("MM:dd");
                String todayDate=format.format(date);
                Log.d(TAG, "run 이름: "+messageSp[0]);
                Log.d(TAG, "run 메시지: "+messageSp[1]);
                Log.d(TAG, "run 날짜: "+todayDate);

                ChatInterface api = ApiClient.getApiClient().create(ChatInterface.class);
                Call<String> call = api.ChattingAdd(userId,userName,userImage,messageSp[1],todayDate,otherName);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(userImage),messageSp[0],messageSp[1],todayDate);
                        chatArrayList.add(chatItem);
                        Log.d(TAG, "run: "+chatArrayList);
                        recyclerView.scrollToPosition(chatArrayList.size()-1);
                        chattingAdapter.notifyDataSetChanged();
                        ChattingRoomInterface roomApi=ApiClient.getApiClient().create(ChattingRoomInterface.class);
                        Call<String> roomCall=roomApi.ChattingRoomAdd(userName,otherName);
                        roomCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });



            }
        });
    }
}
