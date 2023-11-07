package com.example.humanspet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.CommentAddInterface;
import com.example.humanspet.Interface.CommentShowInterface;
import com.example.humanspet.Interface.MyInfoInterface;
import com.example.humanspet.Interface.NoticeboardDetailShowInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeboardShow extends AppCompatActivity {
    String TAG="게시판보기";
    String noticeboardName;
    String noticeboardTitle;
    String noticeboardId;
    ImageView userImage,noticeboardImage;
    TextView nameText,addressText,likesCountText,commentCountText,titleText,contentText,commentText;
    ImageButton likesBtn,sendBtn,cancelBtn,commentBackBtn,commentSendBtn;
    LinearLayout commentLinear;
    ApiClient apiClient = new ApiClient();
    private SharedPreferences preferences;
    String userId;
    String[] responseSp;
    LottieAnimationView loadingAnimation;
    LinearLayout page;
    Animation translate_left;
    Animation translate_right;
    boolean isPageOpen = false;
    EditText commentEdit;
    String userName;
    String image;
    ArrayList<CommentItem> commentItemArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    CommentAdapter commentAdapter;
    ArrayList responseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard_show);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");
        userName=preferences.getString("USERNAME","");

        loadingAnimation = findViewById(R.id.noticeboardShowLottie);
        loadingAnimation.loop(true);
        loadingAnimation.playAnimation();
        loadingAnimation.setVisibility(View.VISIBLE);

        userImage=findViewById(R.id.noticeboardShowUserImage);
        noticeboardImage=findViewById(R.id.noticeboardShowImage);
        nameText=findViewById(R.id.noticeboardShowUserName);
        addressText=findViewById(R.id.noticeboardShowAddressText);
        likesCountText=findViewById(R.id.noticeboardShowLikesCount);
        commentCountText=findViewById(R.id.noticeboardShowCommentCount);
        titleText=findViewById(R.id.noticeboardShowTitleText);
        contentText=findViewById(R.id.noticeboardShowContentText);
        commentText=findViewById(R.id.noticeboardShowCommentText);
        likesBtn=findViewById(R.id.noticeboardShowLikesImage);
        sendBtn=findViewById(R.id.noticeboardShowSendButton);
        cancelBtn=findViewById(R.id.noticeboardShowCancelButton);
        commentLinear=findViewById(R.id.noticeboardShowCommentLinear);
        commentBackBtn=findViewById(R.id.noticeboardShowCommentBackButton);
        commentSendBtn=findViewById(R.id.noticeboardShowCommentSendButton);

        recyclerView=findViewById(R.id.noticeboardShowCommentRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentAdapter =new CommentAdapter(commentItemArrayList);
        recyclerView.setAdapter(commentAdapter);

        page=findViewById(R.id.page);
        translate_left=AnimationUtils.loadAnimation(this,R.anim.translate_left);
        translate_right=AnimationUtils.loadAnimation(this,R.anim.translate_right);

        MyInfoInterface userApi=ApiClient.getApiClient().create(MyInfoInterface.class);
        Call<String> userCall=userApi.getUserInfo(userId);
        userCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String userInfo = response.body();
                String[] userInfoSp = userInfo.split("!!@!!");
                image=userInfoSp[2];
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentEdit=findViewById(R.id.noticeboardShowCommentEdit);
                String commentSt=commentEdit.getText().toString();
                if(commentEdit.getText().toString().equals("")){
                    Toast.makeText(NoticeboardShow.this, "댓글 작성후 전송버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    CommentAddInterface commentApi= ApiClient.getApiClient().create(CommentAddInterface.class);
                    Call<String> commentCall = commentApi.CommentAdd(userName,image,commentEdit.getText().toString(),responseSp[3],responseSp[0]);
                    commentCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d(TAG, "onResponse: "+response.body());
                            commentEdit.setText("");
                            Toast.makeText(NoticeboardShow.this, "댓글을 작성하였습니다.", Toast.LENGTH_SHORT).show();
//                            CommentItem commentItem =new CommentItem(userName,"http://"+apiClient.goUri(image),commentSt);
//                            commentItemArrayList.add(commentItem);
//                            int commentCount=Integer.parseInt(commentCountText.getText().toString());
//                            int commentPlus=commentCount+1;
//                            commentCountText.setText(Integer.toString(commentPlus));
//                            commentText.setText("최신댓글: "+commentSt);
//                            recyclerView.scrollToPosition(commentItemArrayList.size()-1);
//                            commentAdapter.notifyDataSetChanged();
                            NoticeboardDetailShowInterface showApi=ApiClient.getApiClient().create(NoticeboardDetailShowInterface.class);
                            Call<String> showCall = showApi.noticeboardDetailShow(noticeboardId,noticeboardTitle);
                            showCall.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.d(TAG, "onResponse: "+response.body());
                                    responseSp=response.body().split("!!@!!");

                                    CommentShowInterface commentShowApi=ApiClient.getApiClient().create(CommentShowInterface.class);
                                    Call<ArrayList> commentCall = commentShowApi.CommentShow(noticeboardTitle,noticeboardName);
                                    commentCall.enqueue(new Callback<ArrayList>() {
                                        @Override
                                        public void onResponse(Call<ArrayList> showCall, Response<ArrayList> showResponse) {
                                            loadingAnimation.setVisibility(View.GONE);
                                            startView();
                                            commentItemArrayList.clear();
                                            nameText.setText(responseSp[0]);
                                            addressText.setText(responseSp[1]+" "+responseSp[2]);
                                            likesCountText.setText(responseSp[6]);
                                            commentCountText.setText(responseSp[7]);
                                            titleText.setText(responseSp[3]);
                                            contentText.setText(responseSp[4]);
                                            if(userId.equals(responseSp[8])){
                                                sendBtn.setVisibility(View.GONE);
                                            }
                                            Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[9])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(userImage);
                                            Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[5])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(noticeboardImage);

                                            responseArray=showResponse.body();
                                            for(int i=0;i<responseArray.size();i++){
                                                String responseSt= String.valueOf(responseArray.get(i));
                                                String[] responseSp=responseSt.split(", ");
                                                String[] imageSp=responseSp[0].split("");
                                                String imageSt="";
                                                for(int j=1;j<responseSp[0].length();j++){
                                                    imageSt+=imageSp[j];
                                                }
                                                String[] commentSp=responseSp[2].split("");
                                                String commentSt="";
                                                for(int j=0;j<responseSp[2].length()-1;j++){
                                                    commentSt+=commentSp[j];
                                                }
                                                CommentItem commentItem =new CommentItem(responseSp[1],"http://"+apiClient.goUri(imageSt),commentSt);
                                                commentText.setText("최신댓글: "+commentSt);
                                                recyclerView.scrollToPosition(responseArray.size()-1);
                                                commentItemArrayList.add(commentItem);
                                                commentAdapter.notifyDataSetChanged();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList> call, Throwable t) {

                                        }
                                    });
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
            }
        });


        commentLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(0);
                page.setVisibility(View.VISIBLE);
                page.startAnimation(translate_left);
                isPageOpen=true;
            }
        });

        commentBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page.setVisibility(View.INVISIBLE);
                page.startAnimation(translate_right);
                isPageOpen=false;
            }
        });

        Intent intent=getIntent();
        noticeboardName=intent.getStringExtra("name");
        Log.d(TAG, "onCreate: name: "+noticeboardName);
        noticeboardTitle=intent.getStringExtra("title");
        Log.d(TAG, "onCreate: title: "+noticeboardTitle);
        noticeboardId=intent.getStringExtra("id");
        Log.d(TAG, "onCreate: id: "+noticeboardId);

        NoticeboardDetailShowInterface showApi=ApiClient.getApiClient().create(NoticeboardDetailShowInterface.class);
        Call<String> call = showApi.noticeboardDetailShow(noticeboardId,noticeboardTitle);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: "+response.body());
                responseSp=response.body().split("!!@!!");

                CommentShowInterface commentShowApi=ApiClient.getApiClient().create(CommentShowInterface.class);
                Call<ArrayList> commentCall = commentShowApi.CommentShow(noticeboardTitle,noticeboardName);
                commentCall.enqueue(new Callback<ArrayList>() {
                    @Override
                    public void onResponse(Call<ArrayList> showCall, Response<ArrayList> showResponse) {
                        loadingAnimation.setVisibility(View.GONE);
                        startView();
                        nameText.setText(responseSp[0]);
                        addressText.setText(responseSp[1]+" "+responseSp[2]);
                        likesCountText.setText(responseSp[6]);
                        commentCountText.setText(responseSp[7]);
                        titleText.setText(responseSp[3]);
                        contentText.setText(responseSp[4]);
                        if(userId.equals(responseSp[8])){
                            sendBtn.setVisibility(View.GONE);
                        }
                        Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[9])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(userImage);
                        Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[5])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(noticeboardImage);

                        responseArray=showResponse.body();
                        for(int i=0;i<responseArray.size();i++){
                            String responseSt= String.valueOf(responseArray.get(i));
                            String[] responseSp=responseSt.split(", ");
                            String[] imageSp=responseSp[0].split("");
                            String imageSt="";
                            for(int j=1;j<responseSp[0].length();j++){
                                imageSt+=imageSp[j];
                            }
                            String[] commentSp=responseSp[2].split("");
                            String commentSt="";
                            for(int j=0;j<responseSp[2].length()-1;j++){
                                commentSt+=commentSp[j];
                            }
                            CommentItem commentItem =new CommentItem(responseSp[1],"http://"+apiClient.goUri(imageSt),commentSt);
                            commentText.setText("최신댓글: "+commentSt);
                            commentItemArrayList.add(commentItem);
                            commentAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeboardShow.this,ChatRoom.class);
                intent.putExtra("userId",userId);
                intent.putExtra("otherName",responseSp[0]);
                intent.putExtra("image",responseSp[9]);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed(){
        if(isPageOpen==true){
            page.setVisibility(View.INVISIBLE);
            page.startAnimation(translate_right);
            isPageOpen=false;
        }else{
            finish();
        }
    }


    public void startView(){
        TextView title = findViewById(R.id.noticeboardShowTitle);
        TextView titleText = findViewById(R.id.noticeboardShowTitleText);
        View contour = findViewById(R.id.noticeboardShowMainContour);
        View contour2 = findViewById(R.id.noticeboardShowTitleContour);
        View contour3 = findViewById(R.id.noticeboardShowContentContour);
        View contour4 = findViewById(R.id.noticeboardShowLikesContour);
        ImageButton cancelBtn = findViewById(R.id.noticeboardShowCancelButton);
        ImageButton sendBtn = findViewById(R.id.noticeboardShowSendButton);
        ImageView userImage = findViewById(R.id.noticeboardShowUserImage);
        TextView name = findViewById(R.id.noticeboardShowUserName);
        ImageView image=findViewById(R.id.noticeboardShowImage);
        TextView content = findViewById(R.id.noticeboardShowContentText);
        TextView comment =findViewById(R.id.noticeboardShowCommentText);
        ImageButton likesBtn=findViewById(R.id.noticeboardShowLikesImage);
        ImageButton commentBtn = findViewById(R.id.noticeboardShowCommentImage);
        TextView likesCount = findViewById(R.id.noticeboardShowLikesCount);
        TextView commentCount = findViewById(R.id.noticeboardShowCommentCount);
        TextView address = findViewById(R.id.noticeboardShowAddressText);
        LinearLayout likesLinear = findViewById(R.id.noticeboardShowLikesLinear);
        LinearLayout titleLinear = findViewById(R.id.linearLayout2);

        titleLinear.setVisibility(View.VISIBLE);
        likesLinear.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        content.setVisibility(View.VISIBLE);
        contour.setVisibility(View.VISIBLE);
        contour2.setVisibility(View.VISIBLE);
        contour3.setVisibility(View.VISIBLE);
        contour4.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        sendBtn.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        comment.setVisibility(View.VISIBLE);
        likesBtn.setVisibility(View.VISIBLE);
        commentBtn.setVisibility(View.VISIBLE);
        likesCount.setVisibility(View.VISIBLE);
        commentCount.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);

    }
}