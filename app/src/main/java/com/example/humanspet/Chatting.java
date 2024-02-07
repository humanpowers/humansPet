package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
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
import java.util.Collections;
import java.util.Date;

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
    ApiClient apiClient;

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
        apiClient=new ApiClient();

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
                for(int j=0;j<otherNameList.size();j++){
                    for(int i=0;i<otherNameList.size()-1;i++){
                        String firstList = String.valueOf(otherNameList.get(i));
                        String[] firstListSp = firstList.split(", ");
                        String firstDate = firstListSp[2];
                        String[] firstDateSp = firstDate.split("");
                        String firstYear = firstDateSp[0]+firstDateSp[1]+firstDateSp[2]+firstDateSp[3];
                        String firstMonth = firstDateSp[6]+firstDateSp[7];
                        String firstDay = firstDateSp[10]+firstDateSp[11];
                        String firstTime = firstListSp[3];
                        String[] firstTimeSp = firstTime.split("");
                        String firstOh = firstTimeSp[0]+firstTimeSp[1];
                        String firstHour;
                        String firstMinute;
                        if(firstTimeSp.length==8){
                            firstHour = firstTimeSp[3]+firstTimeSp[4];
                            firstMinute = firstTimeSp[6]+firstTimeSp[7];
                        }else{
                            firstHour = firstTimeSp[3];
                            firstMinute = firstTimeSp[5]+firstTimeSp[6];
                        }

                        String secondList = String.valueOf(otherNameList.get(i+1));
                        String[] secondListSp = secondList.split(", ");
                        String secondDate = secondListSp[2];
                        String[] secondDateSp = secondDate.split("");
                        String secondYear = secondDateSp[0]+secondDateSp[1]+secondDateSp[2]+secondDateSp[3];
                        String secondMonth = secondDateSp[6]+secondDateSp[7];
                        String secondDay = secondDateSp[10]+secondDateSp[11];
                        String secondTime = secondListSp[3];
                        String[] secondTimeSp = secondTime.split("");
                        String secondOh = secondTimeSp[0]+secondTimeSp[1];
                        String secondHour;
                        String secondMinute;
                        if(firstTimeSp.length==8){
                            secondHour = secondTimeSp[3]+secondTimeSp[4];
                            secondMinute = secondTimeSp[6]+secondTimeSp[7];
                        }else{
                            secondHour = secondTimeSp[3];
                            secondMinute = secondTimeSp[5]+secondTimeSp[6];
                        }

                        if(Integer.parseInt(firstYear)<Integer.parseInt(secondYear)){
                            Collections.swap(otherNameList,i,i+1);
                        }else if(Integer.parseInt(firstYear)==Integer.parseInt(secondYear)){
                            if(Integer.parseInt(firstMonth)<Integer.parseInt(secondMonth)){
                                Collections.swap(otherNameList,i,i+1);
                            }else if(Integer.parseInt(firstMonth)==Integer.parseInt(secondMonth)){
                                if(Integer.parseInt(firstDay)<Integer.parseInt(secondDay)){
                                    Collections.swap(otherNameList,i,i+1);
                                }else if(Integer.parseInt(firstDay)==Integer.parseInt(secondDay)){
                                    if(firstOh.equals(secondOh)){
                                        if(Integer.parseInt(firstHour)<Integer.parseInt(secondHour)){
                                            Collections.swap(otherNameList,i,i+1);
                                        }else if(Integer.parseInt(firstHour)==Integer.parseInt(secondHour)){
                                            if(Integer.parseInt(firstMinute)<Integer.parseInt(secondMinute)){
                                                Collections.swap(otherNameList,i,i+1);
                                            }
                                        }
                                    }else if(firstOh.equals("오전")){
                                        Collections.swap(otherNameList,i,i+1);
                                    }
                                }
                            }
                        }

                    }
                }
                for(int i=0;i<otherNameList.size();i++){
                    String otherNameListSt = String.valueOf(otherNameList.get(i));
                    Log.d(TAG, "onResponse otherNameListSt: "+otherNameListSt);
                    String[] otherNameListSp = otherNameListSt.split(", ");
                    String listName = otherNameListSp[0];
                    String[] listNameSp = listName.split("");
                    String realName="";
                    for(int j=1;j<listNameSp.length;j++){
                        realName+=listNameSp[j];
                    }

                    String listImage = otherNameListSp[6];
                    String[] listImageSp = listImage.split("");
                    String realImage = "";
                    for(int j=0;j< listImageSp.length-1;j++){
                        realImage+=listImageSp[j];
                    }
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                    String todayDate=format.format(date);
                    float readCount = Float.parseFloat(otherNameListSp[4]);
                    int readCountInt = (int) readCount;

                    String getDate = otherNameListSp[2];
                    String[] getDateSp = getDate.split("");
                    String realDate = getDateSp[2]+getDateSp[3]+"."+getDateSp[6]+getDateSp[7]+"."+getDateSp[10]+getDateSp[11];

                    if(otherNameListSp[1].equals("null")){
                        if(!realName.equals(userName)){
                            if(otherNameListSp[2].equals(todayDate)){
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem("http://"+apiClient.goUri(realImage), realName,"사진을 보냈습니다.",otherNameListSp[3],readCountInt);
                                chatItemArrayList.add(chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }else{
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem("http://"+apiClient.goUri(realImage), realName,"사진을 보냈습니다.",realDate,readCountInt);
                                chatItemArrayList.add(chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
                    }else{
                        if(!realName.equals(userName)){
                            if(otherNameListSp[2].equals(todayDate)){
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem("http://"+apiClient.goUri(realImage), realName,otherNameListSp[1],otherNameListSp[3],readCountInt);
                                chatItemArrayList.add(chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }else{
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem("http://"+apiClient.goUri(realImage), realName,otherNameListSp[1],realDate,readCountInt);
                                chatItemArrayList.add(chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
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
                String[] messageSp=message.split("!!@!!");
                Log.d(TAG, "run: message"+message);
                int count=0;
                if(messageSp.length>=3){
                    Log.d(TAG, "run: 여기도 로그가 찍혀요");
                    for(int i=0;i<chatItemArrayList.size();i++){
                        if(chatItemArrayList.get(i).getTitle().equals(messageSp[0])){
                            count=i;
                        }
                    }
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                    String todayTime = outputFormat.format(date);
                    String[] timeSp=todayTime.split("");
                    String hour = timeSp[0]+timeSp[1];
                    String realTime="";
                    if(Integer.parseInt(hour)>12){
                        realTime="오후"+" "+Integer.toString(Integer.parseInt(hour)-12)+timeSp[2]+timeSp[3]+timeSp[4];
                    }else if(Integer.parseInt(hour)==12){
                        realTime="오후"+" "+timeSp[0]+timeSp[1]+timeSp[2]+timeSp[3]+timeSp[4];
                    }else if(Integer.parseInt(hour)==24){
                        realTime="오전"+" "+Integer.toString(Integer.parseInt(hour)-12)+timeSp[2]+timeSp[3]+timeSp[4];
                    }else{
                        realTime="오전"+" "+timeSp[0]+timeSp[1]+timeSp[2]+timeSp[3]+timeSp[4];
                    }
                    GetChattingRoomInterface api=ApiClient.getApiClient().create(GetChattingRoomInterface.class);
                    Call<ArrayList> call = api.GetChattingRoom(userName);
                    int finalCount = count;
                    String finalRealTime = realTime;
                    call.enqueue(new Callback<ArrayList>() {
                        @Override
                        public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                            otherNameList=response.body();
                            String otherNameListSt = String.valueOf(otherNameList.get(finalCount));
                            String[] otherNameListSp = otherNameListSt.split(", ");
                            float readCount = Float.parseFloat(otherNameListSp[4]);
                            int readCountInt = (int) readCount;
                            Log.d(TAG, "onResponse: readCountInt"+readCountInt);
                            if(messageSp[3].equals("이미지")) {
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem(chatItemArrayList.get(finalCount).getImage(), chatItemArrayList.get(finalCount).getTitle(), "사진을 보냈습니다.", finalRealTime, readCountInt);
                                chatItemArrayList.remove(finalCount);
                                chatItemArrayList.add(0, chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }else{
                                ChattingRoomItem chattingRoomItem = new ChattingRoomItem(chatItemArrayList.get(finalCount).getImage(), chatItemArrayList.get(finalCount).getTitle(), messageSp[1], finalRealTime, readCountInt);
                                chatItemArrayList.remove(finalCount);
                                chatItemArrayList.add(0,chattingRoomItem);
                                chatAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onFailure(Call<ArrayList> call, Throwable t) {

                        }
                    });

                }
            }
        });
    }
}