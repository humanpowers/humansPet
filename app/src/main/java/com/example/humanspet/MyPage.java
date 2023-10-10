package com.example.humanspet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.humanspet.Interface.DiaryDiaryRecyclerViewInterface;
import com.example.humanspet.Interface.DiaryRecyclerViewInterface;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyPage extends Fragment {
    String TAG="마이페이지";
    String userId;
    Button logoutBtn,cancelBtn,myInfoBtn;
    private SharedPreferences preferences;
    LottieAnimationView animationView;
    View v;
    ArrayList<DiaryInfoItem> diaryInfoItemArrayList = new ArrayList<>();
    ArrayList responseArray;
    DiaryInfoAdapter diaryInfoAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    TextView nameEdit,sexEdit,birthEdit,weightEdit,kindEdit,typesEdit,registrationEdit;
    String name,sex,birth,weight,types,kind,registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences= getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        SharedPreferences.Editor editor = preferences.edit();
        v = inflater.inflate(R.layout.fragment_fragment5, container, false);

        recyclerView=v.findViewById(R.id.myPageRecyclerView);
        linearLayoutManager=new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        diaryInfoAdapter =new DiaryInfoAdapter(diaryInfoItemArrayList);
        recyclerView.setAdapter(diaryInfoAdapter);

        logoutBtn = v.findViewById(R.id.mainLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패 , SDK에서 토큰 삭제됨", error);
                    } else {
                        Log.e(TAG, "로그아웃 성공, SDK에서 토큰 삭제됨");
                    }
                    return null;
                });
                editor.remove("USERID");
                editor.remove("AUTOLOGIN");
                editor.commit();
                Intent intent = new Intent(getActivity(),Login.class); //fragment라서 activity intent와는 다른 방식
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                requireActivity().finish();

            }
        });

        cancelBtn = v.findViewById(R.id.mainCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().unlink(error -> {
                    if (error != null) {
                        Log.e(TAG, "회원탈퇴 실패", error);
                    } else {
                        Log.e(TAG, "회원 탈퇴 성공");
                        Intent intent = new Intent(getActivity(),Login.class); //fragment라서 activity intent와는 다른 방식
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                    return null;
                });
            }
        });

        myInfoBtn = v.findViewById(R.id.mainMyInfoBtn);
        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyInfo.class); //fragment라서 activity intent와는 다른 방식
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        diaryInfoAdapter.setOnClickListener(new DiaryInfoAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                String responseSt= String.valueOf(responseArray.get(position));
                String[] responseSp=responseSt.split(", ");
                String[] nameSp=responseSp[0].split("");
                String nameSt="";
                for(int j=1;j<responseSp[0].length();j++){
                    nameSt+=nameSp[j];
                }
                String[] registrationNumberSp=responseSp[7].split("");
                String registrationNumberSt="";
                for(int j=0;j<registrationNumberSp.length-1;j++){
                    registrationNumberSt+=registrationNumberSp[j];
                }
                nameEdit=v.findViewById(R.id.myPageNameEdit);
                nameEdit.setText(nameSt);
                sexEdit=v.findViewById(R.id.myPageSexEdit);
                sexEdit.setText(responseSp[2]);
                birthEdit=v.findViewById(R.id.myPageBirthEdit);
                birthEdit.setText(responseSp[3]);
                weightEdit=v.findViewById(R.id.myPageWeightEdit);
                weightEdit.setText(responseSp[4]);
                typesEdit=v.findViewById(R.id.myPageTypesEdit);
                typesEdit.setText(responseSp[5]);
                kindEdit=v.findViewById(R.id.myPageKindEdit);
                kindEdit.setText(responseSp[6]);
                registrationEdit=v.findViewById(R.id.myPageRegistrationNumberEdit);
                registrationEdit.setText(registrationNumberSt);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 호출");

        SharedPreferences.Editor editor = preferences.edit();

        animationView = v.findViewById(R.id.myPageLottie);
        animationView.loop(true);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        diaryInfoItemArrayList.clear();

        DiaryRecyclerViewInterface api = ApiClient.getApiClient().create(DiaryRecyclerViewInterface.class);
        Call<ArrayList> call= api.getUserDiary(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                Log.d(TAG, "onResponse: "+response.body());
                animationView.setVisibility(View.GONE);
                startView();
                if(response.body().size()==0){
                    TextView noPet = v.findViewById(R.id.myPageNoPetText);
                    noPet.setVisibility(View.VISIBLE);
                }
                responseArray=response.body();
                for(int i=0;i<responseArray.size();i++){
                    String responseSt= String.valueOf(responseArray.get(i));
                    String[] responseSp=responseSt.split(", ");
                    String[] nameSp=responseSp[0].split("");
                    String nameSt="";
                    for(int j=1;j<responseSp[0].length();j++){
                        nameSt+=nameSp[j];
                    }
                    String[] registrationNumberSp=responseSp[7].split("");
                    String registrationNumberSt="";
                    for(int j=0;j<registrationNumberSp.length-1;j++){
                        registrationNumberSt+=registrationNumberSp[j];
                    }
                    DiaryInfoItem diaryInfoItem =new DiaryInfoItem(responseSp[1],nameSt);
                    diaryInfoItemArrayList.add(diaryInfoItem);
                    diaryInfoAdapter.notifyDataSetChanged();
                    if(i==0){
                        nameEdit=v.findViewById(R.id.myPageNameEdit);
                        nameEdit.setText(nameSt);
                        sexEdit=v.findViewById(R.id.myPageSexEdit);
                        sexEdit.setText(responseSp[2]);
                        birthEdit=v.findViewById(R.id.myPageBirthEdit);
                        birthEdit.setText(responseSp[3]);
                        weightEdit=v.findViewById(R.id.myPageWeightEdit);
                        weightEdit.setText(responseSp[4]);
                        typesEdit=v.findViewById(R.id.myPageTypesEdit);
                        typesEdit.setText(responseSp[5]);
                        kindEdit=v.findViewById(R.id.myPageKindEdit);
                        kindEdit.setText(responseSp[6]);
                        registrationEdit=v.findViewById(R.id.myPageRegistrationNumberEdit);
                        registrationEdit.setText(registrationNumberSt);
                    }
                }
            }


            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });
    }

    public void startView(){
        nameEdit=v.findViewById(R.id.myPageNameEdit);
        sexEdit=v.findViewById(R.id.myPageSexEdit);
        birthEdit=v.findViewById(R.id.myPageBirthEdit);
        weightEdit=v.findViewById(R.id.myPageWeightEdit);
        typesEdit=v.findViewById(R.id.myPageTypesEdit);
        kindEdit=v.findViewById(R.id.myPageKindEdit);
        registrationEdit=v.findViewById(R.id.myPageRegistrationNumberEdit);
        TextView nameText=v.findViewById(R.id.myPageNameText);
        TextView sexText=v.findViewById(R.id.myPageSexText);
        TextView birthText=v.findViewById(R.id.myPageBirthText);
        TextView weightText=v.findViewById(R.id.myPageWeightText);
        TextView typesText=v.findViewById(R.id.myPageTypesText);
        TextView kindText=v.findViewById(R.id.myPageKindText);
        TextView registrationText=v.findViewById(R.id.myPageRegistrationNumberText);
        View recyclerView=v.findViewById(R.id.diaryRecyclerViewContour);
        View contour=v.findViewById(R.id.diaryContour);
        View nameView=v.findViewById(R.id.diaryNameContour);
        View sexView=v.findViewById(R.id.diarySexContour);
        View birthView=v.findViewById(R.id.diaryBirthContour);
        View weightView=v.findViewById(R.id.diaryWeightContour);
        View typesView=v.findViewById(R.id.diaryTypesContour);
        View kindView=v.findViewById(R.id.diaryKindContour);
        View registrationView=v.findViewById(R.id.diaryRegistrationContour);
        Button logOutBtn=v.findViewById(R.id.mainLogoutBtn);
        Button cancelBtn=v.findViewById(R.id.mainCancelBtn);
        Button myInfoBtn=v.findViewById(R.id.mainMyInfoBtn);
        recyclerView.setVisibility(View.VISIBLE);
        contour.setVisibility(View.VISIBLE);
        logOutBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        myInfoBtn.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.VISIBLE);
        sexView.setVisibility(View.VISIBLE);
        birthView.setVisibility(View.VISIBLE);
        weightView.setVisibility(View.VISIBLE);
        typesView.setVisibility(View.VISIBLE);
        kindView.setVisibility(View.VISIBLE);
        registrationView.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);
        sexEdit.setVisibility(View.VISIBLE);
        sexText.setVisibility(View.VISIBLE);
        birthText.setVisibility(View.VISIBLE);
        birthEdit.setVisibility(View.VISIBLE);
        weightText.setVisibility(View.VISIBLE);
        weightEdit.setVisibility(View.VISIBLE);
        typesText.setVisibility(View.VISIBLE);
        typesEdit.setVisibility(View.VISIBLE);
        kindText.setVisibility(View.VISIBLE);
        kindEdit.setVisibility(View.VISIBLE);
        registrationText.setVisibility(View.VISIBLE);
        registrationEdit.setVisibility(View.VISIBLE);
    }
}