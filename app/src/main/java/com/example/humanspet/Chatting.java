package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanspet.Interface.GetChattingRoomInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chatting extends Fragment {
    View v;
    String TAG="채팅";
    ChattingRoomAdapter chatAdapter;
    private RecyclerView recyclerView;
    ArrayList<ChattingRoomItem> chatItemArrayList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences preferences;
    String userName;
    String userId;
    ArrayList otherNameList;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragment3, container, false);
        preferences = getActivity().getSharedPreferences("USER",MODE_PRIVATE);
        userName=preferences.getString("USERNAME","");
        userId=preferences.getString("USERID","");
        Log.d(TAG, "onCreateView: userName: "+userName);

        recyclerView=v.findViewById(R.id.chatRecyclerView);
        linearLayoutManager=new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatAdapter =new ChattingRoomAdapter(chatItemArrayList);
        recyclerView.setAdapter(chatAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        chatItemArrayList.clear();

        GetChattingRoomInterface api=ApiClient.getApiClient().create(GetChattingRoomInterface.class);
        Call<ArrayList> call = api.GetChattingRoom(userName);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                Log.d(TAG, "onResponse: "+response.body());
                otherNameList=response.body();
                for(int i=0;i<otherNameList.size();i++){
                    if(!otherNameList.get(i).equals(userName)){
                        ChattingRoomItem chattingRoomItem = new ChattingRoomItem(R.drawable.basic_profile, (String) otherNameList.get(i),"메시지","2023-10-25");
                        chatItemArrayList.add(chattingRoomItem);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });

        chatAdapter.setOnClickListener(new ChattingRoomAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                Intent intent = new Intent(getActivity(),ChatRoom.class);
                intent.putExtra("userId",userId);
                intent.putExtra("otherName",chatItemArrayList.get(position).getTitle());
                startActivity(intent);
            }
        });
        new ConnectToServerTask().execute();
    }

    @Override
    public void onStop() {
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

    private class ConnectToServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + ": 소켓쓰레드");
                socket = new Socket("18.225.32.239", 8888); // 서버 IP 주소와 포트 번호를 서버에 맞게 변경
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + ": 소켓아래");
                out = new PrintWriter(socket.getOutputStream(), true);
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + out);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(userName);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] messageSp=message.split(":");
                Log.d(TAG, "run: message"+message);
                int count=0;
                if(messageSp.length>=3){
                    Log.d(TAG, "run: 여기도 로그가 찍혀요");
                    for(int i=0;i<chatItemArrayList.size();i++){
                        if(chatItemArrayList.get(i).getTitle().equals(messageSp[0])){
                            count=i;
                        }
                    }
                    ChattingRoomItem chattingRoomItem = new ChattingRoomItem(R.drawable.basic_profile,chatItemArrayList.get(count).getTitle(),"메시지","2023-10-25");
                    chatItemArrayList.remove(count);
                    chatItemArrayList.add(0,chattingRoomItem);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}