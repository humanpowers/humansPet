package com.example.humanspet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.NoticeboardDetailShowInterface;

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
    ImageButton likesBtn,sendBtn,cancelBtn;
    ApiClient apiClient = new ApiClient();
    private SharedPreferences preferences;
    String userId;
    String[] responseSp;
    LottieAnimationView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard_show);

        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userId=preferences.getString("USERID","");

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
                loadingAnimation.setVisibility(View.GONE);
                startView();
                Log.d(TAG, "onResponse: "+response.body());
                responseSp=response.body().split("!!@!!");
                nameText.setText(responseSp[0]);
                addressText.setText(responseSp[1]+" "+responseSp[2]);
                likesCountText.setText(responseSp[6]);
                commentCountText.setText(responseSp[7]);
                titleText.setText(responseSp[3]);
                contentText.setText(responseSp[4]);
                if(responseSp[8].equals("")){
                    commentText.setText("댓글이 없습니다.");
                }else{
                    commentText.setText(responseSp[8]);
                }
                if(userId.equals(responseSp[9])){
                    sendBtn.setVisibility(View.GONE);
                }

                Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[10])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(userImage);
                Glide.with(NoticeboardShow.this).load("http://"+apiClient.goUri(responseSp[5])).thumbnail(Glide.with(NoticeboardShow.this).load(R.raw.loadinggif)).into(noticeboardImage);
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
                intent.putExtra("image",responseSp[10]);
                startActivity(intent);
            }
        });

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