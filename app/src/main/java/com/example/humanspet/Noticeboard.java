package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.humanspet.Interface.NoticeboardShowInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Noticeboard extends Fragment {
    String TAG="게시판";
    private SharedPreferences preferences;
    private String userId;
    ImageButton noticeboardAddBtn,noticeboardLikesBtn;
    LottieAnimationView animationView;
    ArrayList responseArray;
    ArrayList<NoticeboardItem> noticeboardItemArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    NoticeboardAdapter noticeboardAdapter;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragment1, container, false);

        preferences = getActivity().getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");

        if (!NotificationPermissionChecker.isNotificationPermissionGranted(getActivity())) {
            // 알림 권한이 없는 경우, 설정 화면으로 이동합니다.
            NotificationPermissionChecker.openNotificationSettings(getActivity());
        }

        recyclerView=v.findViewById(R.id.noticeboardRecyclerView);
        linearLayoutManager=new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        noticeboardAdapter =new NoticeboardAdapter(noticeboardItemArrayList);
        recyclerView.setAdapter(noticeboardAdapter);

        noticeboardAddBtn=v.findViewById(R.id.noticeboardAdd);
        noticeboardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NoticeboardAdd.class);
                startActivity(intent);
            }
        });

        noticeboardAdapter.setOnClickListener(new NoticeboardAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                String responseSt= String.valueOf(responseArray.get(responseArray.size()-position-1));
                String[] responseSp=responseSt.split(", ");
                String[] nameSp=responseSp[0].split("");
                String nameSt="";
                for(int j=1;j<responseSp[0].length();j++){
                    nameSt+=nameSp[j];
                }
                Intent intent = new Intent(getActivity(),NoticeboardShow.class);
                intent.putExtra("name",nameSt);
                intent.putExtra("title",responseSp[3]);
                intent.putExtra("id",responseSp[8]);
                intent.putExtra("type","select");
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        animationView = v.findViewById(R.id.noticeboardLottie);
        animationView.loop(true);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        noticeboardItemArrayList.clear();

        NoticeboardShowInterface showApi=ApiClient.getApiClient().create(NoticeboardShowInterface.class);
        Call<ArrayList> call = showApi.noticeboardShow(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                ViewStart(v);
                Log.d(TAG, "onResponse: "+response.body());
                animationView.setVisibility(View.GONE);
                responseArray=response.body();
                for(int i=responseArray.size()-1;i>=0;i--){
                    String responseSt= String.valueOf(responseArray.get(i));
                    String[] responseSp=responseSt.split(", ");
                    String[] nameSp=responseSp[0].split("");
                    String nameSt="";
                    for(int j=1;j<responseSp[0].length();j++){
                        nameSt+=nameSp[j];
                    }
                    String[] imageSp=responseSp[10].split("");
                    String imageSt="";
                    for(int j=0;j<responseSp[10].length()-1;j++){
                        imageSt+=imageSp[j];
                    }
                    NoticeboardItem noticeboardItem =new NoticeboardItem(imageSt,nameSt,responseSp[2],responseSp[1],responseSp[5],Integer.valueOf(responseSp[6]),Integer.valueOf(responseSp[7]),responseSp[4],responseSp[3],responseSp[9]);
                    noticeboardItemArrayList.add(noticeboardItem);
                    noticeboardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });
    }

    public void ViewStart(View v){
        ImageButton addBtn=v.findViewById(R.id.noticeboardAdd);
        View noticeboardContour = v.findViewById(R.id.noticeboardMainContour);
        noticeboardContour.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.VISIBLE);
    }
}
