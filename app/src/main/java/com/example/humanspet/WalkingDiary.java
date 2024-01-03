package com.example.humanspet;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.DiaryRecyclerViewInterface;
import com.example.humanspet.Interface.WalkingDiaryInterface;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkingDiary extends AppCompatActivity {

    String TAG = "산책다이어리";
    Button previousBtn;
    RecyclerView petRecyclerView,diaryRecyclerView;
    LinearLayoutManager linearLayoutManager,walkingDiaryManager;
    ArrayList<DiaryInfoItem> diaryInfoItemArrayList = new ArrayList<>();
    DiaryInfoAdapter diaryInfoAdapter;
    WalkingDiaryAdapter walkingDiaryAdapter;
    ArrayList<WalkingDiaryItem> walkingDiaryItems = new ArrayList<>();

    private SharedPreferences preferences;
    String userId;
    ArrayList responseArray;
    int selectPosition=0;
    ArrayList walkingResponseArray;
    String firstName;
    ImageView map;
    ImageButton mapCancelBtn;
    ApiClient apiClient;
    Boolean mapBoolean=false;
    TextView mapCalorie,mapDistance,mapTime,diaryGoneText,diaryCalendarText;
    LottieAnimationView animationView;
    ImageButton calendarBtn,calendarBackBtn;
    private SharedPreferences sharedPreferences;
    private long selectedDate;
    boolean calendarBackBoolean;
    TextView selectDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_diary);

        mapCalorie=findViewById(R.id.walkingDiaryCalories);
        mapDistance=findViewById(R.id.walkingDiaryDistance);
        mapTime=findViewById(R.id.walkingDiaryTime);
        calendarBackBtn=findViewById(R.id.walkingDiaryCalendarBack);
        diaryGoneText=findViewById(R.id.walkingDiaryGoneText);
        diaryCalendarText=findViewById(R.id.walkingDiaryCalendarText);
        selectDateText=findViewById(R.id.walkingDiarySelectDate);

        preferences=getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");

        previousBtn=findViewById(R.id.walkingDiaryPreviousButton);
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        petRecyclerView.setLayoutManager(linearLayoutManager);

        diaryInfoAdapter =new DiaryInfoAdapter(diaryInfoItemArrayList);
        petRecyclerView.setAdapter(diaryInfoAdapter);

        diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        walkingDiaryManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        diaryRecyclerView.setLayoutManager(walkingDiaryManager);

        walkingDiaryAdapter = new WalkingDiaryAdapter(walkingDiaryItems);
        diaryRecyclerView.setAdapter(walkingDiaryAdapter);

        animationView = findViewById(R.id.walkingDiaryLottie);
        animationView.loop(true);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);

        DiaryRecyclerViewInterface api = ApiClient.getApiClient().create(DiaryRecyclerViewInterface.class);
        Call<ArrayList> call= api.getUserDiary(userId);
        call.enqueue(new Callback<ArrayList>() {
            @Override
            public void onResponse(Call<ArrayList> call, Response<ArrayList> response) {
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
                    diaryInfoAdapter.notifyDataSetChanged();
                    if(i==0){
                        firstName=nameSt;
                    }
                }

                WalkingDiaryInterface walkingApi=ApiClient.getApiClient().create(WalkingDiaryInterface.class);
                Call<ArrayList> walkingCall = walkingApi.walkingDiary(userId);
                walkingCall.enqueue(new Callback<ArrayList>() {
                    @Override
                    public void onResponse(Call<ArrayList> walkingCall, Response<ArrayList> walkingResponse) {
                        walkingResponseArray=walkingResponse.body();
                        animationView.setVisibility(View.GONE);
                        mapOff();
                        calendarBackBtn.setVisibility(View.GONE);
                        for(int i=walkingResponseArray.size()-1;i>=0;i--){
                            String walkingDiary = String.valueOf(walkingResponseArray.get(i));
                            String[] walkingDiarySp = walkingDiary.split(", ");

                            String name = walkingDiarySp[0];
                            String[] nameSp = name.split("");
                            String nameSt="";
                            for(int j=1;j<nameSp.length;j++){
                                nameSt+=nameSp[j];
                            }

                            String date = walkingDiarySp[5];
                            String[] dateSp = date.split("");
                            String dateSt = "";
                            for(int j=0;j<17;j++){
                                if(j==4){
                                    dateSt+="년 ";
                                }else if(j==7){
                                    dateSt+="월 ";
                                }else if(j==10){
                                    dateSt+="일 ";
                                }else if(j==13){
                                    dateSt+="시 ";
                                }else if(j==16){
                                    dateSt+="분 ";
                                }else{
                                    dateSt+=dateSp[j];
                                }
                            }
                            if(nameSt.equals(firstName)){
                                WalkingDiaryItem walkingDiaryItem = new WalkingDiaryItem(dateSt,walkingDiarySp[2],walkingDiarySp[3],walkingDiarySp[1],walkingDiarySp[4]);
                                walkingDiaryItems.add(walkingDiaryItem);
                            }
                        }
                        walkingDiaryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ArrayList> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList> call, Throwable t) {

            }
        });

        diaryInfoAdapter.setOnClickListener(new DiaryInfoAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                selectPosition=position;
                walkingDiaryItems.clear();
                String petName = diaryInfoItemArrayList.get(position).getName();
                firstName=diaryInfoItemArrayList.get(position).getName();
                int count=0;
                for(int i=walkingResponseArray.size()-1;i>=0;i--){
                    String walkingDiary = String.valueOf(walkingResponseArray.get(i));
                    String[] walkingDiarySp = walkingDiary.split(", ");

                    String name = walkingDiarySp[0];
                    String[] nameSp = name.split("");
                    String nameSt="";
                    for(int j=1;j<nameSp.length;j++){
                        nameSt+=nameSp[j];
                    }

                    String date = walkingDiarySp[5];
                    String[] dateSp = date.split("");
                    String dateSt = "";
                    for(int j=0;j<17;j++){
                        if(j==4){
                            dateSt+="년 ";
                        }else if(j==7){
                            dateSt+="월 ";
                        }else if(j==10){
                            dateSt+="일 ";
                        }else if(j==13){
                            dateSt+="시 ";
                        }else if(j==16){
                            dateSt+="분 ";
                        }else{
                            dateSt+=dateSp[j];
                        }
                    }
                    if(nameSt.equals(petName)){
                        WalkingDiaryItem walkingDiaryItem = new WalkingDiaryItem(dateSt,walkingDiarySp[2],walkingDiarySp[3],walkingDiarySp[1],walkingDiarySp[4]);
                        walkingDiaryItems.add(walkingDiaryItem);
                        count++;
                    }
                }
                if(count==0){
                    diaryGoneText.setVisibility(View.VISIBLE);
                }else{
                    diaryGoneText.setVisibility(View.GONE);
                }
                walkingDiaryAdapter.notifyDataSetChanged();
                diaryCalendarText.setText("원하는 날짜를 선택해주세요.");
            }
        });

        apiClient=new ApiClient();
        map = findViewById(R.id.walkingDiaryMap);
        walkingDiaryAdapter.setOnClickListener(new WalkingDiaryAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                mapOn();
                mapBoolean=true;
                Log.d(TAG, "onImageButtonClicker: "+walkingDiaryItems.get(position).getImage());
                Glide.with(WalkingDiary.this).load("http://"+apiClient.goUri(walkingDiaryItems.get(position).getImage())).thumbnail(Glide.with(map).load(R.raw.loadinggif)).override(1000,1000).into(map);
                mapCalorie.setText(walkingDiaryItems.get(position).getCalorie());
                mapDistance.setText(walkingDiaryItems.get(position).getDistance());
                mapTime.setText(walkingDiaryItems.get(position).getTime());
                selectDateText.setText(walkingDiaryItems.get(position).getDate());

            }
        });

        mapCancelBtn=findViewById(R.id.walkingDiaryMapCancel);
        mapCancelBtn.bringToFront();
        mapCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapOff();
                mapBoolean=false;
                if(calendarBackBoolean==false){
                    calendarBackBtn.setVisibility(View.GONE);
                }else{
                    calendarBackBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        calendarBtn=findViewById(R.id.walkingDiaryCalendarButton);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WalkingDiary.this);
                ConstraintLayout diaryCalendar = (ConstraintLayout) View.inflate(WalkingDiary.this, R.layout.diary_calendar, null);
                AlertDialog.Builder calendarDlg=new AlertDialog.Builder(WalkingDiary.this);
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

                for(int i=0;i<walkingResponseArray.size();i++){
                    String petResponseSt= String.valueOf(walkingResponseArray.get(i));
                    String[] petResponseSp=petResponseSt.split(", ");
                    String[] petNameSp=petResponseSp[0].split("");
                    String petNameSt="";
                    for(int j=1;j<petNameSp.length;j++){
                        petNameSt+=petNameSp[j];
                    }

                    String date = petResponseSp[5];
                    String[] dateSplit = date.split(" ");
                    String dateSt = dateSplit[0];

                    if(petNameSt.equals(firstName)){
                        String[] dateSp=dateSt.split("-");
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
                        view.setBackgroundDrawable(ContextCompat.getDrawable(WalkingDiary.this, R.drawable.box_mint));
                    }
                });

                calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                        calendarBackBoolean=true;
                        calendarBackBtn.setVisibility(View.VISIBLE);
                        diaryGoneText.setVisibility(View.GONE);
                        int year=date.getYear();
                        int month=date.getMonth();
                        int dayOfMonth=date.getDay();
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(year,month,dayOfMonth);
                        selectedDate=calendar.getTimeInMillis();
                        sharedPreferences.edit().putLong("walkingSelectedDate", selectedDate).apply();
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
                        int size=walkingDiaryItems.size();
                        for(int i=0;i<size;i++){
                            walkingDiaryItems.remove(0);
                            walkingDiaryAdapter.notifyDataSetChanged();
                        }
                        int diaryCount=0;
                        for(int i=0;i<walkingResponseArray.size();i++){
                            String walkingDiary = String.valueOf(walkingResponseArray.get(i));
                            String[] walkingDiarySp = walkingDiary.split(", ");

                            String name = walkingDiarySp[0];
                            String[] nameSp = name.split("");
                            String nameSt="";
                            for(int j=1;j<nameSp.length;j++){
                                nameSt+=nameSp[j];
                            }

                            String date1 = walkingDiarySp[5];
                            String[] dateSp = date1.split("");
                            String dateSt = "";
                            for(int j=0;j<17;j++){
                                if(j==4){
                                    dateSt+="년 ";
                                }else if(j==7){
                                    dateSt+="월 ";
                                }else if(j==10){
                                    dateSt+="일 ";
                                }else if(j==13){
                                    dateSt+="시 ";
                                }else if(j==16){
                                    dateSt+="분 ";
                                }else{
                                    dateSt+=dateSp[j];
                                }
                            }

                            String date2 = walkingDiarySp[5];
                            String[] dateSplit = date2.split(" ");
                            String dateSt1 = dateSplit[0];

                            if(dateSt1.equals(selectDateSt)&&nameSt.equals(firstName)){
                                WalkingDiaryItem walkingDiaryItem = new WalkingDiaryItem(dateSt,walkingDiarySp[2],walkingDiarySp[3],walkingDiarySp[1],walkingDiarySp[4]);
                                walkingDiaryItems.add(walkingDiaryItem);
                                diaryCount++;
                                walkingDiaryAdapter.notifyDataSetChanged();
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
                calendarBackBoolean=false;
                int size=walkingDiaryItems.size();
                for(int i=0;i<size;i++){
                    walkingDiaryItems.remove(0);
                    walkingDiaryAdapter.notifyDataSetChanged();
                }
                int count=0;
                calendarBackBtn.setVisibility(View.GONE);
                for(int i=walkingResponseArray.size()-1;i>=0;i--){
                    String walkingDiary = String.valueOf(walkingResponseArray.get(i));
                    String[] walkingDiarySp = walkingDiary.split(", ");

                    String name = walkingDiarySp[0];
                    String[] nameSp = name.split("");
                    String nameSt="";
                    for(int j=1;j<nameSp.length;j++){
                        nameSt+=nameSp[j];
                    }

                    String date = walkingDiarySp[5];
                    String[] dateSp = date.split("");
                    String dateSt = "";
                    for(int j=0;j<17;j++){
                        if(j==4){
                            dateSt+="년 ";
                        }else if(j==7){
                            dateSt+="월 ";
                        }else if(j==10){
                            dateSt+="일 ";
                        }else if(j==13){
                            dateSt+="시 ";
                        }else if(j==16){
                            dateSt+="분 ";
                        }else{
                            dateSt+=dateSp[j];
                        }
                    }
                    if(nameSt.equals(firstName)){
                        WalkingDiaryItem walkingDiaryItem = new WalkingDiaryItem(dateSt,walkingDiarySp[2],walkingDiarySp[3],walkingDiarySp[1],walkingDiarySp[4]);
                        walkingDiaryItems.add(walkingDiaryItem);
                        count++;
                    }
                }
                walkingDiaryAdapter.notifyDataSetChanged();
                diaryCalendarText.setText("원하는 날짜를 선택해주세요.");
                if(count==0){
                    diaryGoneText.setVisibility(View.VISIBLE);
                }else{
                    diaryGoneText.setVisibility(View.GONE);
                }
            }
        });

    }

    public void mapOn(){
        ImageButton mapCancelBtn = findViewById(R.id.walkingDiaryMapCancel);
        ImageView map = findViewById(R.id.walkingDiaryMap);
        TextView walkingDiaryText = findViewById(R.id.walkingDiaryText);
        Button previousBtn = findViewById(R.id.walkingDiaryPreviousButton);
        RecyclerView petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        RecyclerView diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        LinearLayout diaryLinear = findViewById(R.id.walkingDiaryLinear);
        TextView calendarText = findViewById(R.id.walkingDiaryCalendarText);
        ImageButton calendarBack = findViewById(R.id.walkingDiaryCalendarBack);
        ImageButton calendarBtn = findViewById(R.id.walkingDiaryCalendarButton);
        TextView dateText = findViewById(R.id.walkingDiarySelectDate);
        dateText.setVisibility(View.VISIBLE);
        calendarText.setVisibility(View.GONE);
        calendarBack.setVisibility(View.GONE);
        calendarBtn.setVisibility(View.GONE);
        diaryLinear.setVisibility(View.VISIBLE);
        mapCancelBtn.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);
        walkingDiaryText.setVisibility(View.GONE);
        previousBtn.setVisibility(View.GONE);
        petRecyclerView.setVisibility(View.GONE);
        diaryRecyclerView.setVisibility(View.GONE);
    }

    public void mapOff(){
        ImageButton mapCancelBtn = findViewById(R.id.walkingDiaryMapCancel);
        ImageView map = findViewById(R.id.walkingDiaryMap);
        TextView walkingDiaryText = findViewById(R.id.walkingDiaryText);
        Button previousBtn = findViewById(R.id.walkingDiaryPreviousButton);
        RecyclerView petRecyclerView = findViewById(R.id.walkingDiaryPetRecyclerView);
        RecyclerView diaryRecyclerView = findViewById(R.id.walkingDiaryDiaryRecyclerView);
        LinearLayout diaryLinear = findViewById(R.id.walkingDiaryLinear);
        TextView calendarText = findViewById(R.id.walkingDiaryCalendarText);
        ImageButton calendarBack = findViewById(R.id.walkingDiaryCalendarBack);
        ImageButton calendarBtn = findViewById(R.id.walkingDiaryCalendarButton);
        TextView dateText = findViewById(R.id.walkingDiarySelectDate);
        dateText.setVisibility(View.GONE);
        calendarText.setVisibility(View.VISIBLE);
        calendarBack.setVisibility(View.VISIBLE);
        calendarBtn.setVisibility(View.VISIBLE);
        diaryLinear.setVisibility(View.GONE);
        mapCancelBtn.setVisibility(View.GONE);
        map.setVisibility(View.GONE);
        walkingDiaryText.setVisibility(View.VISIBLE);
        previousBtn.setVisibility(View.VISIBLE);
        petRecyclerView.setVisibility(View.VISIBLE);
        diaryRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed(){
        if(mapBoolean==true){
            mapOff();
            mapBoolean=false;
        }else{
            finish();
        }
    }
}