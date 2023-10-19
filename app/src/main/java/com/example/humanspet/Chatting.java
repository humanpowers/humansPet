package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.humanspet.Interface.GetChattingRoomInterface;

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
                String title=chatItemArrayList.get(position).getTitle();
                Intent intent = new Intent(getActivity(),ChatRoom.class);
                intent.putExtra("userId",userId);
                intent.putExtra("otherName",chatItemArrayList.get(position).getTitle());
                startActivity(intent);
            }
        });





        return v;
    }

}