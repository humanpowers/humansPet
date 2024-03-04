package com.example.humanspet;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryDiaryRecyclerViewInterface;
import com.example.humanspet.Interface.DiaryRecyclerViewInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Diary extends Fragment  {
    String TAG="다이어리";
    String userId;
    int selectPosition;
    ApiClient apiClient=new ApiClient();
    ArrayList<DiaryInfoItem> diaryInfoItemArrayList = new ArrayList<>();
    ArrayList<DiaryDiaryItem> diaryDiaryItemArrayList = new ArrayList<>();
    private SharedPreferences preferences;
    ImageButton calendarBtn,diarySelectButton;
    String petName="";
    String dataUri,diaryDataUri;
    DiaryInfoAdapter diaryInfoAdapter;
    DiaryDiaryAdapter diaryDiaryAdapter;
    private RecyclerView recyclerView,diaryRecyclerView;
    private LinearLayoutManager linearLayoutManager,diaryLinearLayoutManager;
    ArrayList responseArray,petResponseArray;
    ArrayList diaryArrayList=new ArrayList();
    LottieAnimationView animationView;
    private boolean fabMain_status = false;
    private SharedPreferences sharedPreferences,diaryPreferences;
    TextView diaryCalendarText,diaryGoneText;
    private FloatingActionButton fabMain;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabEdit;
    private long selectedDate;
    View v;
    GridLayoutManager gridLayoutManager;
    ImageButton gridBtn,verticalBtn;
    SharedPreferences.Editor diaryEditor;
    String petImage;
    private Handler handler;
    private final int animationInterval = 4000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: 호출");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 호출");
        v = inflater.inflate(R.layout.fragment_fragment2, container, false);


        fabMain = v.findViewById(R.id.fabMain);
        fabCamera = v.findViewById(R.id.fabCamera);
        fabEdit = v.findViewById(R.id.fabEdit);


        preferences = getActivity().getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        Log.d(TAG, "onCreateView: userid"+userId);

        diaryPreferences = getActivity().getSharedPreferences("DIARY",MODE_PRIVATE);
        diaryEditor = diaryPreferences.edit();

        diaryCalendarText=v.findViewById(R.id.diaryCalendarText);

        recyclerView=v.findViewById(R.id.diaryRecyclerView);
        linearLayoutManager=new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(linearLayoutManager);

        diaryInfoAdapter =new DiaryInfoAdapter(diaryInfoItemArrayList);
        recyclerView.setAdapter(diaryInfoAdapter);

        ActivityResultLauncher<Intent> diaryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri diaryUri = intent.getData();
                        Log.e(TAG, "uri : " + diaryUri);
                        String diaryRealUri=getRealPathFromUri(diaryUri);
                        Log.e(TAG,"realUri"+diaryRealUri);
                        diaryDataUri=diaryRealUri;
                        Glide.with(getActivity())
                                .load(diaryRealUri)
                                .into(diarySelectButton);
                    }
                });

        diaryInfoAdapter.setOnClickListener(new DiaryInfoAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                diaryCalendarText.setText("원하는 날짜를 선택해주세요.");
                diaryGoneText.setVisibility(View.GONE);
                selectPosition=position;
                diaryArrayList.clear();
                String responseSt= String.valueOf(responseArray.get(position));
                String[] responseSp=responseSt.split(", ");
                String[] nameSp=responseSp[0].split("");
                String nameSt="";
                for(int j=1;j<responseSp[0].length();j++){
                    nameSt+=nameSp[j];
                }
                petName=nameSt;
                petImage=responseSp[1];
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("PETNAME");
                editor.putString("PETNAME",petName);
                editor.commit();
                diaryDiaryItemArrayList=new ArrayList<>();

                diaryRecyclerView=v.findViewById(R.id.diaryDiaryRecyclerView);
                diaryLinearLayoutManager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                String diaryType = diaryPreferences.getString("TYPE","1");
                if(diaryType.equals("1")){
                    diaryRecyclerView.setLayoutManager(diaryLinearLayoutManager);
                }else{
                    diaryRecyclerView.setLayoutManager(gridLayoutManager);
                }

                diaryDiaryAdapter=new DiaryDiaryAdapter(diaryDiaryItemArrayList);
                diaryRecyclerView.setAdapter(diaryDiaryAdapter);

                int diaryCount=0;
                for(int i=petResponseArray.size()-1;i>=0;i--){
                    String petResponseSt= String.valueOf(petResponseArray.get(i));
                    String[] petResponseSp=petResponseSt.split(", ");
                    String[] petTitleSp=petResponseSp[0].split("");
                    String petTitleSt="";
                    for(int j=1;j<petTitleSp.length;j++){
                        petTitleSt+=petTitleSp[j];
                    }
                    String[] petNameSp=petResponseSp[4].split("");

                    String petNameSt="";
                    for(int j=0;j<petNameSp.length-1;j++){
                        petNameSt+=petNameSp[j];
                    }
                    Log.d(TAG, "onResponse: date: "+petResponseSp[3]);
                    Log.d(TAG, "onResponse: title: "+petTitleSt);
                    Log.d(TAG, "onResponse: image: "+petResponseSp[2]);
                    Log.d(TAG, "onResponse: name: "+petNameSt);
                    Log.d(TAG, "onResponse: content: "+petResponseSp[1]);
                    if(petNameSt.equals(petName)){
                        diaryType = diaryPreferences.getString("TYPE","1");
                        DiaryDiaryItem diaryDiaryItem =new DiaryDiaryItem(petResponseSp[2],petResponseSp[3],petTitleSt,petResponseSp[1],diaryType);
                        diaryDiaryItemArrayList.add(diaryDiaryItem);
                        diaryArrayList.add(String.valueOf(petResponseArray.get(i)));
                        Log.d(TAG, "onResponse: 다이어리어레이리스트"+diaryDiaryItemArrayList);
                        diaryDiaryAdapter.notifyDataSetChanged();
                        diaryCount++;
                    }
                }
                if(diaryCount==0){
                    diaryGoneText.setVisibility(View.VISIBLE);
                }

                diaryDiaryAdapter.setOnClickListener(new DiaryDiaryAdapter.RecyclerViewClickListener() {
                    @Override
                    public void onImageButtonClicker(int position) {
                        String petResponSt=String.valueOf(diaryArrayList.get(position));
                        String[] petResponSp=petResponSt.split(",");
                        Log.d(TAG, "petResponSt"+petResponSt);
                        Log.d(TAG, "petResponSp"+petResponSp[0]);
                        String petResponName="";
                        for(int i=1;i<petResponSp[0].length();i++){
                            String[] petNameSp=petResponSp[0].split("");
                            petResponName+=petNameSp[i];
                        }
                        final String realTitle=petResponName;
                        String petResponImage="";
                        for(int i=1;i<petResponSp[2].length();i++){
                            String[] petImageSp=petResponSp[2].split("");
                            petResponImage+=petImageSp[i];
                        }
                        Log.d(TAG, "petResopnsp[2]: "+petResponSp[2]);
                        Log.d(TAG, "petResponName: "+petResponName);
                        Log.d(TAG, "petResponImage: "+petResponImage);

                        endView(v);

                        Intent intent = new Intent(getActivity(),DiaryShow.class);
                        intent.putExtra("title",petResponName);
                        intent.putExtra("content",petResponSp[1]);
                        intent.putExtra("image",petResponImage);
                        startActivity(intent);
                    }
                });
            }
        });

        // 메인플로팅 버튼 클릭
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
            }
        });

        // 다이어리 추가 버튼 클릭
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView noPetGone=v.findViewById(R.id.diaryNoPetText);
                if(noPetGone.getVisibility()==View.VISIBLE){
                    Toast.makeText(getActivity(),"등록된 펫이 없습니다.\n펫을 등록후 사용해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getActivity(),DiaryAdd.class);
                    intent.putExtra("petName",petName);
                    intent.putExtra("petImage","http://"+apiClient.goUri(petImage));
                    endView(v);
                    startActivity(intent);
                }
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PetAdd.class);
                intent.putExtra("USERID",userId);
                endView(v);
                startActivity(intent);
            }
        });

        diaryGoneText=v.findViewById(R.id.diaryGoneText);
        ImageButton calendarBackBtn=v.findViewById(R.id.diaryCalendarBack);

        calendarBtn=v.findViewById(R.id.diaryCalendarButton);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                ConstraintLayout diaryCalendar = (ConstraintLayout) View.inflate(getActivity(), R.layout.diary_calendar, null);
                AlertDialog.Builder calendarDlg=new AlertDialog.Builder(getActivity());
                AlertDialog calendarDialog = calendarDlg.create();
                calendarDialog.setView(diaryCalendar);

                MaterialCalendarView calendarView=diaryCalendar.findViewById(R.id.diaryCalendar);

                DayViewDecorator weekendDecorator = new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        // Calendar 객체를 생성하고 해당 날짜로 설정합니다.
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(day.getYear(), day.getMonth() - 1, day.getDay()); // 월은 0부터 시작하므로 -1을 해줍니다.

                        // 요일을 확인하고 토요일 (Calendar.SATURDAY) 또는 일요일 (Calendar.SUNDAY)인 경우에만 스타일을 적용합니다.
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        // 토요일의 글꼴 색상을 파랑색으로 변경합니다.
                        view.addSpan(new ForegroundColorSpan(Color.BLUE));
                    }
                };

                DayViewDecorator sundayDecorator = new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        // Calendar 객체를 생성하고 해당 날짜로 설정합니다.
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(day.getYear(), day.getMonth() - 1, day.getDay()); // 월은 0부터 시작하므로 -1을 해줍니다.

                        // 요일을 확인하고 일요일 (Calendar.SUNDAY)인 경우에만 스타일을 적용합니다.
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        return dayOfWeek == Calendar.SUNDAY;
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        // 일요일의 글꼴 색상을 빨강색으로 변경합니다.
                        view.addSpan(new ForegroundColorSpan(Color.RED));
                    }
                };
                calendarView.addDecorators(weekendDecorator, sundayDecorator);

                for(int i=0;i<petResponseArray.size();i++){
                    String petResponseSt= String.valueOf(petResponseArray.get(i));
                    String[] petResponseSp=petResponseSt.split(", ");
                    String[] petNameSp=petResponseSp[4].split("");
                    String petNameSt="";
                    for(int j=0;j<petNameSp.length-1;j++){
                        petNameSt+=petNameSp[j];
                    }
                    if(petNameSt.equals(petName)){
                        String[] dateSp=petResponseSp[3].split("-");
                        CalendarDay specialDate = CalendarDay.from(Integer.valueOf(dateSp[0]), Integer.valueOf(dateSp[1]), Integer.valueOf(dateSp[2]));
                        DayViewDecorator specialDateDecorator = new DayViewDecorator() {
                            @Override
                            public boolean shouldDecorate(CalendarDay day) {
                                // 특정 날짜와 현재 날짜를 비교하여 같으면 점을 표시합니다.
                                return day.equals(specialDate);
                            }

                            @Override
                            public void decorate(DayViewFacade view) {
                                // 특정 날짜에 점을 표시하는 스타일을 설정합니다.
                                view.addSpan(new DotSpan(5, Color.RED)); // 5px 크기의 빨간색 점을 표시
                                // 다른 스타일 변경도 가능
                            }
                        };
                        calendarView.addDecorators(specialDateDecorator);
                    }
                }
                CalendarDay today = CalendarDay.today();
                calendarView.addDecorators(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        // 오늘 날짜와 현재 날짜가 같은지 확인합니다.
                        return day.equals(today);
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        // 오늘 날짜의 배경색을 변경합니다.
                        view.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.box_mint));
                    }
                });

                calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                        calendarBackBtn.setVisibility(View.VISIBLE);
                        diaryGoneText.setVisibility(View.GONE);
                        int year=date.getYear();
                        int month=date.getMonth();
                        int dayOfMonth=date.getDay();
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(year,month,dayOfMonth);
                        selectedDate=calendar.getTimeInMillis();
                        sharedPreferences.edit().putLong("selectedDate", selectedDate).apply();
                        String day;
                        int realMonth=month;
                        String selectDateSt="";
                        if(realMonth<10&&dayOfMonth<10){
                            selectDateSt=year+"-0"+realMonth+"-0"+dayOfMonth;
                        }else if(realMonth<10){
                            selectDateSt=year+"-0"+realMonth+"-"+dayOfMonth;
                        }else if(dayOfMonth<10){
                            selectDateSt=year+"-"+realMonth+"-0"+dayOfMonth;
                        }else{
                            selectDateSt=year+"-"+realMonth+"-"+dayOfMonth;
                        }
                        diaryCalendarText.setText(selectDateSt);
                        calendarDialog.dismiss();
                        int size=diaryDiaryItemArrayList.size();
                        for(int i=0;i<size;i++){
                            diaryDiaryItemArrayList.remove(0);
                            diaryDiaryAdapter.notifyDataSetChanged();
                        }
                        int diaryCount=0;
                        for(int i=petResponseArray.size()-1;i>=0;i--){
                            String petResponseSt= String.valueOf(petResponseArray.get(i));
                            String[] petResponseSp=petResponseSt.split(", ");
                            String[] petTitleSp=petResponseSp[0].split("");
                            String petTitleSt="";
                            for(int j=1;j<petTitleSp.length;j++){
                                petTitleSt+=petTitleSp[j];
                            }
                            String[] petNameSp=petResponseSp[4].split("");

                            String petNameSt="";
                            for(int j=0;j<petNameSp.length-1;j++){
                                petNameSt+=petNameSp[j];
                            }
                            Log.d(TAG, "dialogDateSt: "+i+petResponseSp[3]);
                            Log.d(TAG, "selectDateSt: "+i+selectDateSt);
                            if(petResponseSp[3].equals(selectDateSt)&&petNameSt.equals(petName)){
                                String diaryType = diaryPreferences.getString("TYPE","1");
                                DiaryDiaryItem diaryDiaryItem =new DiaryDiaryItem(petResponseSp[2],petResponseSp[3],petTitleSt,petResponseSp[1],diaryType);
                                diaryDiaryItemArrayList.add(diaryDiaryItem);
                                diaryCount++;
                                diaryDiaryAdapter.notifyDataSetChanged();
                            }

                        }
                        if(diaryCount==0){
                            diaryGoneText.setVisibility(View.VISIBLE);
                        }
                    }
                });
                calendarDialog.show();
            }
        });

        calendarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size=diaryDiaryItemArrayList.size();
                for(int i=0;i<size;i++){
                    diaryDiaryItemArrayList.remove(0);
                    diaryDiaryAdapter.notifyDataSetChanged();
                }
                int diaryCount=0;
                for(int i=petResponseArray.size()-1;i>=0;i--){
                    String petResponseSt= String.valueOf(petResponseArray.get(i));
                    String[] petResponseSp=petResponseSt.split(", ");
                    String[] petTitleSp=petResponseSp[0].split("");
                    String petTitleSt="";
                    for(int j=1;j<petTitleSp.length;j++){
                        petTitleSt+=petTitleSp[j];
                    }
                    String[] petNameSp=petResponseSp[4].split("");

                    String petNameSt="";
                    for(int j=0;j<petNameSp.length-1;j++){
                        petNameSt+=petNameSp[j];
                    }
                    Log.d(TAG, "onResponse: date: "+petResponseSp[3]);
                    Log.d(TAG, "onResponse: title: "+petTitleSt);
                    Log.d(TAG, "onResponse: image: "+petResponseSp[2]);
                    Log.d(TAG, "onResponse: name: "+petNameSt);
                    Log.d(TAG, "onResponse: content: "+petResponseSp[1]);
                    if(petNameSt.equals(petName)){
                        String diaryType = diaryPreferences.getString("TYPE","1");
                        DiaryDiaryItem diaryDiaryItem =new DiaryDiaryItem(petResponseSp[2],petResponseSp[3],petTitleSt,petResponseSp[1],diaryType);
                        diaryDiaryItemArrayList.add(diaryDiaryItem);
                        diaryArrayList.add(String.valueOf(petResponseArray.get(i)));
                        Log.d(TAG, "onResponse: 다이어리어레이리스트"+diaryDiaryItemArrayList);
                        diaryDiaryAdapter.notifyDataSetChanged();
                        diaryCount++;
                    }
                }
                diaryCalendarText.setText("원하는 날짜를 선택해주세요.");
                if(diaryCount==0){
                    TextView diaryGoneText=v.findViewById(R.id.diaryGoneText);
                    diaryGoneText.setVisibility(View.VISIBLE);
                }else{
                    TextView diaryGoneText=v.findViewById(R.id.diaryGoneText);
                    diaryGoneText.setVisibility(View.GONE);
                }
                calendarBackBtn.setVisibility(View.GONE);
            }
        });

        verticalBtn = v.findViewById(R.id.diaryDiaryVerticalButton);
        gridBtn = v.findViewById(R.id.diaryDiaryGridButton);

        verticalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaryRecyclerView.setLayoutManager(diaryLinearLayoutManager);
                for(int i=0;i<diaryDiaryItemArrayList.size();i++){
                    diaryDiaryItemArrayList.get(i).setType("1");
                }
                diaryEditor.putString("TYPE","1");
                diaryEditor.commit();
                diaryDiaryAdapter.notifyDataSetChanged();
                verticalBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.human_color));
                gridBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.gray));
            }
        });

        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaryRecyclerView.setLayoutManager(gridLayoutManager);
                for(int i=0;i<diaryDiaryItemArrayList.size();i++){
                    diaryDiaryItemArrayList.get(i).setType("2");
                }
                diaryEditor.putString("TYPE","2");
                diaryEditor.commit();
                diaryDiaryAdapter.notifyDataSetChanged();
                gridBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.human_color));
                verticalBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.gray));
            }
        });

        handler = new Handler();
        handler.postDelayed(animationRunnable, animationInterval);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: 호출");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 호출");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 호출");
        animationView = v.findViewById(R.id.lottie);
        animationView.loop(true);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        diaryArrayList.clear();
        diaryInfoItemArrayList.clear();

        diaryGoneText=v.findViewById(R.id.diaryGoneText);
        diaryGoneText.setVisibility(View.GONE);

        SharedPreferences.Editor editor = preferences.edit();

        DiaryRecyclerViewInterface api = ApiClient.getApiClient().create(DiaryRecyclerViewInterface.class);
        Call<ArrayList> call= api.getUserDiary(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                Log.d(TAG, "onResponse: "+response.body());
                TextView noPet = v.findViewById(R.id.diaryNoPetText);
                if(response.body().size()==0){
                    noPet.setVisibility(View.VISIBLE);
                }else{
                    noPet.setVisibility(View.GONE);
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
                    DiaryInfoItem diaryInfoItem =new DiaryInfoItem(responseSp[1],nameSt);
                    diaryInfoItemArrayList.add(diaryInfoItem);

                    if(petName.equals("")){
                        petName=nameSt;
                        petImage=responseSp[1];
                        editor.putString("PETNAME",petName);
                        editor.commit();
                    }
                }
                diaryDiaryItemArrayList=new ArrayList<>();

                diaryRecyclerView=v.findViewById(R.id.diaryDiaryRecyclerView);
                diaryLinearLayoutManager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                String diaryType = diaryPreferences.getString("TYPE","1");
                if(diaryType.equals("1")){
                    diaryRecyclerView.setLayoutManager(diaryLinearLayoutManager);
                    Log.d(TAG, "onImageButtonClicker: vertical이야");
                    verticalBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.human_color));
                }else{
                    diaryRecyclerView.setLayoutManager(gridLayoutManager);
                    Log.d(TAG, "onImageButtonClicker: grid야");
                    gridBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.human_color));
                }

                diaryDiaryAdapter=new DiaryDiaryAdapter(diaryDiaryItemArrayList);
                diaryRecyclerView.setAdapter(diaryDiaryAdapter);

                DiaryDiaryRecyclerViewInterface diaryApi = ApiClient.getApiClient().create(DiaryDiaryRecyclerViewInterface.class);
                Call<ArrayList> call2= diaryApi.getUserPetDiary(userId);
                call2.enqueue(new Callback<ArrayList>() {
                    @Override
                    public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
                        Log.d(TAG, "onResponse: "+response.body());
                        animationView.setVisibility(View.GONE);
                        startView(v);
                        diaryInfoAdapter.notifyDataSetChanged();
                        petResponseArray=response.body();
                        int diaryCount=0;
                        for(int i=petResponseArray.size()-1;i>=0;i--){
                            String petResponseSt= String.valueOf(petResponseArray.get(i));
                            String[] petResponseSp=petResponseSt.split(", ");
                            String[] petTitleSp=petResponseSp[0].split("");
                            String petTitleSt="";
                            for(int j=1;j<petTitleSp.length;j++){
                                petTitleSt+=petTitleSp[j];
                            }
                            String[] petNameSp=petResponseSp[4].split("");

                            String petNameSt="";
                            for(int j=0;j<petNameSp.length-1;j++){
                                petNameSt+=petNameSp[j];
                            }
                            Log.d(TAG, "onResponse: date: "+petResponseSp[3]);
                            Log.d(TAG, "onResponse: title: "+petTitleSt);
                            Log.d(TAG, "onResponse: image: "+petResponseSp[2]);
                            Log.d(TAG, "onResponse: name: "+petNameSt);
                            Log.d(TAG, "onResponse: content: "+petResponseSp[1]);
                            if(petNameSt.equals(petName)){
                                String diaryType = diaryPreferences.getString("TYPE","1");
                                DiaryDiaryItem diaryDiaryItem =new DiaryDiaryItem(petResponseSp[2],petResponseSp[3],petTitleSt,petResponseSp[1],diaryType);
                                diaryDiaryItemArrayList.add(diaryDiaryItem);
                                diaryArrayList.add(String.valueOf(petResponseArray.get(i)));
                                Log.d(TAG, "onResponse: 다이어리어레이리스트"+diaryDiaryItemArrayList);
                                diaryDiaryAdapter.notifyDataSetChanged();
                                diaryCount++;
                            }
                        }
                        if(diaryCount==0){
                            TextView diaryGoneText=v.findViewById(R.id.diaryGoneText);
                            diaryGoneText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList> call, Throwable t) {

                    }
                });
                diaryDiaryAdapter.setOnClickListener(new DiaryDiaryAdapter.RecyclerViewClickListener() {
                    @Override
                    public void onImageButtonClicker(int position) {
                        String petResponSt=String.valueOf(diaryArrayList.get(position));
                        String[] petResponSp=petResponSt.split(",");
                        Log.d(TAG, "petResponSt"+petResponSt);
                        Log.d(TAG, "petResponSp"+petResponSp[0]);
                        String petResponName="";
                        for(int i=1;i<petResponSp[0].length();i++){
                            String[] petNameSp=petResponSp[0].split("");
                            petResponName+=petNameSp[i];
                        }
                        final String realTitle=petResponName;
                        String petResponImage="";
                        for(int i=1;i<petResponSp[2].length();i++){
                            String[] petImageSp=petResponSp[2].split("");
                            petResponImage+=petImageSp[i];
                        }
                        Log.d(TAG, "petResopnsp[2]: "+petResponSp[2]);
                        Log.d(TAG, "petResponName: "+petResponName);
                        Log.d(TAG, "petResponImage: "+petResponImage);

                        editor.putString("PETNAME",petName);
                        editor.commit();
                        endView(v);

                        Intent intent = new Intent(getActivity(),DiaryShow.class);
                        intent.putExtra("title",petResponName);
                        intent.putExtra("content",petResponSp[1]);
                        intent.putExtra("image",petResponImage);
                        startActivity(intent);
                    }
                });
            }


            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: 호출");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: 호출");
        handler.removeCallbacks(animationRunnable);
    }

    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    public void startView(View v){
        TextView calendarText=v.findViewById(R.id.diaryCalendarText);
        RecyclerView diaryRecyclerView=v.findViewById(R.id.diaryDiaryRecyclerView);
        RecyclerView recyclerView=v.findViewById(R.id.diaryRecyclerView);
        View diaryContour = v.findViewById(R.id.diaryContour);
        ImageButton calendarImage=v.findViewById(R.id.diaryCalendarButton);
        ImageButton diaryGridBtn = v.findViewById(R.id.diaryDiaryGridButton);
        ImageButton diaryVerticalBtn = v.findViewById(R.id.diaryDiaryVerticalButton);
        View diaryMainContour = v.findViewById(R.id.diaryMainContour);
        diaryMainContour.setVisibility(View.VISIBLE);
        diaryGridBtn.setVisibility(View.VISIBLE);
        diaryVerticalBtn.setVisibility(View.VISIBLE);
        calendarImage.setVisibility(View.VISIBLE);
        diaryContour.setVisibility(View.VISIBLE);
        calendarText.setVisibility(View.VISIBLE);
        diaryRecyclerView.setVisibility(View.VISIBLE);
        fabMain=v.findViewById(R.id.fabMain);
        fabCamera=v.findViewById(R.id.fabCamera);
        fabEdit=v.findViewById(R.id.fabEdit);
        fabMain.setVisibility(View.VISIBLE);
        fabCamera.setVisibility(View.VISIBLE);
        fabEdit.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void endView(View v){
        TextView calendarText=v.findViewById(R.id.diaryCalendarText);
        RecyclerView diaryRecyclerView=v.findViewById(R.id.diaryDiaryRecyclerView);
        View diaryContour = v.findViewById(R.id.diaryContour);
        RecyclerView recyclerView=v.findViewById(R.id.diaryRecyclerView);
        ImageButton calendarImage=v.findViewById(R.id.diaryCalendarButton);
        ImageButton diaryGridBtn = v.findViewById(R.id.diaryDiaryGridButton);
        ImageButton diaryVerticalBtn = v.findViewById(R.id.diaryDiaryVerticalButton);
        View diaryMainContour = v.findViewById(R.id.diaryMainContour);
        diaryMainContour.setVisibility(View.GONE);
        diaryGridBtn.setVisibility(View.GONE);
        diaryVerticalBtn.setVisibility(View.GONE);
        calendarImage.setVisibility(View.GONE);
        diaryContour.setVisibility(View.GONE);
        calendarText.setVisibility(View.GONE);
        diaryRecyclerView.setVisibility(View.GONE);
        fabMain=v.findViewById(R.id.fabMain);
        fabCamera=v.findViewById(R.id.fabCamera);
        fabEdit=v.findViewById(R.id.fabEdit);
        fabMain.setVisibility(View.GONE);
        fabCamera.setVisibility(View.GONE);
        fabEdit.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    public void toggleFab() {
        if(fabMain_status) {
            // 플로팅 액션 버튼 닫기
            // 애니메이션 추가
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabCamera, "translationY", 0f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabEdit, "translationY", 0f);
            fe_animation.start();
            // 메인 플로팅 이미지 변경
            fabMain.setImageResource(R.drawable.human_plus);

        }else {
            // 플로팅 액션 버튼 열기
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabCamera, "translationY", -200f);
            fc_animation.start();
            ObjectAnimator fe_animation = ObjectAnimator.ofFloat(fabEdit, "translationY", -400f);
            fe_animation.start();
            // 메인 플로팅 이미지 변경
            fabMain.setImageResource(R.drawable.human_cancel);
        }
        // 플로팅 버튼 상태 변경
        fabMain_status = !fabMain_status;
    }

    private Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return compressedBitmap;
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            // 애니메이션 실행
            Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
            final Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

            fabMain.startAnimation(scaleUp);

            scaleUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabMain.startAnimation(scaleDown);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            // 다음 애니메이션을 위해 타이머 재설정
            handler.postDelayed(this, animationInterval);
        }
    };
}