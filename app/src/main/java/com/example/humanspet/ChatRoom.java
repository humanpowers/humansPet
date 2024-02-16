package com.example.humanspet;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.ChatInterface;
import com.example.humanspet.Interface.ChattingImageSendInterface;
import com.example.humanspet.Interface.ChattingRoomInterface;
import com.example.humanspet.Interface.GetChatting;
import com.example.humanspet.Interface.MyInfoInterface;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoom extends AppCompatActivity {
    String TAG = "소켓";
    private EditText messageEditText;
    private ImageButton sendButton;
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
    String message;
    ApiClient apiClient = new ApiClient();
    String image;
    Boolean entranceBoolean=false;
    ImageButton imageSelectBtn;
    String messageDataUri;
    ImageView selectImage;
    ImageButton imageSendBtn;
    LinearLayout textLinear,imageLinear;
    String imageName;
    ImageButton imageCancelBtn;
    PhotoView imagePhotoView;
    Boolean imageBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Log.d(TAG,"onCreate호출");

        imageBoolean=false;
        preferences = getSharedPreferences("USER",MODE_PRIVATE);
        userName=preferences.getString("USERNAME","");

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        otherName=intent.getStringExtra("otherName");

        imageSelectBtn=findViewById(R.id.chattingImageSelectButton);
        selectImage=findViewById(R.id.chattingSelectImage);
        imageSendBtn = findViewById(R.id.chattingImageSendButton);
        textLinear = findViewById(R.id.chattingTextLinear);
        imageLinear = findViewById(R.id.chattingImageLinear);
        imagePhotoView = findViewById(R.id.chattingPhotoView);
        imageCancelBtn = findViewById(R.id.chattingPhotoViewCancelBtn);


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
                Log.d(TAG, "onResponse: 채팅내용 가져옴 "+response.body());
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
                    String timeSt="";
                    String[] timeSp=getChatSp[6].split("");
                    for(int j=0;j<timeSp.length-1;j++){
                        timeSt+=timeSp[j];
                    }
                    if(getChatSp[3].equals("null")){
                        if(nameSt.equals(userName)){
                            ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(getChatSp[1]),nameSt,"http://"+apiClient.goUri(getChatSp[5]),timeSt,getChatSp[2],getChatSp[4],"1");
                            chatArrayList.add(chatItem);
                        }else{
                            ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(getChatSp[1]),nameSt,"http://"+apiClient.goUri(getChatSp[5]),timeSt,getChatSp[2],getChatSp[4],"1");
                            chatArrayList.add(chatItem);
                        }
                    }else{
                        if(nameSt.equals(userName)){
                            ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(getChatSp[1]),nameSt,getChatSp[3],timeSt,getChatSp[2],getChatSp[4],"0");
                            chatArrayList.add(chatItem);
                        }else{
                            ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(getChatSp[1]),nameSt,getChatSp[3],timeSt,getChatSp[2],getChatSp[4],"0");
                            chatArrayList.add(chatItem);
                        }
                    }

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

        new ConnectToServerTask().execute();

        recyclerView=findViewById(R.id.chattingRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        chattingAdapter =new ChattingAdapter(chatArrayList,userName);
        recyclerView.setAdapter(chattingAdapter);

        TextView title = findViewById(R.id.chatTitle);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        title.setText(otherName);

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

        ActivityResultLauncher<Intent> chattingLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        imageLinear.setVisibility(View.VISIBLE);
                        textLinear.setVisibility(View.GONE);
                        Log.e(TAG, "result : " + result);
                        Intent getIntent = result.getData();
                        Log.e(TAG, "intent : " + getIntent);
                        Uri messageUri = getIntent.getData();
                        Log.e(TAG, "uri : " + messageUri);
                        String messageRealUri=getRealPathFromUri(messageUri);
                        Log.e(TAG,"realUri"+messageRealUri);
                        messageDataUri=messageRealUri;
                        Glide.with(this)
                                .load(messageRealUri)
                                .into(selectImage);
                    }
                });

        imageSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                chattingLauncher.launch(intent);
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                chattingLauncher.launch(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageEditText.getText().toString().equals("")){
                    Toast.makeText(ChatRoom.this,"메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                    String todayDate=format.format(date);
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
                    String read = "";
                    if(entranceBoolean){
                        read="읽음";
                    }else{
                        read="않읽음";
                    }
                    Log.d(TAG, "onClick: "+read);

                    ChatInterface api = ApiClient.getApiClient().create(ChatInterface.class);
                    Call<String> call = api.ChattingAdd(userId,userName,userImage,messageEditText.getText().toString(),todayDate,otherName,realTime,read);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            image=response.body();
                            sendMessage();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }

            }
        });

        chattingAdapter.setOnClickListener(new ChattingAdapter.RecyclerViewClickListener() {
            @Override
            public void onImageButtonClicker(int position) {
                Log.d(TAG, "onImageButtonClicker: 사진클릭됨");
                imageClick();
                imageBoolean=true;
                Glide.with(ChatRoom.this).load(chatArrayList.get(position).getMessage()).centerCrop().thumbnail(Glide.with(ChatRoom.this).load(R.raw.loadinggif)).override(1000, 1000).into(imagePhotoView);

            }
        });

        imageCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageCancel();
                imageBoolean=false;
            }
        });

        imageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                String todayDate=format.format(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                String todayTime = outputFormat.format(date);
                SimpleDateFormat imageFormat = new SimpleDateFormat("HH:mm:ss");
                String imageTime = imageFormat.format(date);
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
                String read = "";
                if(entranceBoolean){
                    read="읽음";
                }else{
                    read="않읽음";
                }

                File messageDataFile= new File(messageDataUri);
                Log.d(TAG, "onClick: dataFile: "+messageDataFile);
                String messageFileName=imageTime+".jpg";
                Log.d(TAG, "onClick: fileName: "+messageFileName);

                RequestBody messageRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),messageDataFile);
                MultipartBody.Part messageBody = MultipartBody.Part.createFormData("uploaded_file",messageFileName,messageRequestBody);
                Log.d(TAG, "onClick: body: "+messageBody);

                ChattingImageSendInterface chattingImageSendApi = ApiClient.getApiClient().create(ChattingImageSendInterface.class);
                Call<String> chattingImageCall = chattingImageSendApi.chattingImageSend(userId,userName,userImage,todayDate,otherName,realTime,read,messageBody);
                chattingImageCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "onResponse: 이미지 보내짐"+response.body());
                        imageName=response.body();
                        sendImage();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
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
        new Thread(() -> {
        out.println("소켓종료"+"!@!"+otherName);
        }).start();
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

        new ConnectToServerTask().execute();
    }
    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if (!message.isEmpty() && out != null) {
            Log.d(TAG, "sendMessage: 들어옴"+message);
            new Thread(){
                public void run(){
                    out.println(userName+"!@!"+message+"!@!"+otherName+"!@!"+userImage+"!@!채팅");
                }
            }.start();
            messageEditText.setText("");
        }
    }

    private void sendImage() {
        textLinear.setVisibility(View.VISIBLE);
        imageLinear.setVisibility(View.GONE);
        String message = imageName;
        if (!message.isEmpty() && out != null) {
            Log.d(TAG, "sendMessage: 들어옴"+message);
            new Thread(){
                public void run(){
                    out.println(userName+"!@!"+message+"!@!"+otherName+"!@!"+userImage+"!@!이미지");
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
                socket = new Socket("3.16.161.235", 8888); // 서버 IP 주소와 포트 번호를 서버에 맞게 변경
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + ": 소켓아래");
                out = new PrintWriter(socket.getOutputStream(), true);
                Log.d(TAG, "doInBackground: " + Thread.currentThread().getName() + out);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(userName+"!!@!!"+otherName);
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
                String[] messageSp=message.split("!!@!!");
                if(messageSp.length==2){
                    Log.d(TAG, "run:길이가 2일때 "+messageSp[0]+"!!!"+messageSp[1]);
                    if(messageSp[0].equals(otherName)&&messageSp[1].equals(userName)){
                        Log.d(TAG, "run: 상대 접속");
                        entranceBoolean=true;
                        for(int i=0;i<chatArrayList.size();i++){
                            chatArrayList.get(i).setRead("1");
                        }
                        chattingAdapter.notifyDataSetChanged();
                    }
                }else if(messageSp.length==1){
                    Log.d(TAG, "run: 1자리"+message);
                    if(message.equals("이미접속")){
                        Log.d(TAG, "run: 이미 접속중이다.");
                        entranceBoolean=true;
                    }else{
                        Log.d(TAG, "run: 소켓종료 들어옴");
                        entranceBoolean=false;
                    }
                }else{ //채팅 일때
                    if(messageSp[3].equals("채팅")){
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                        String time=outputFormat.format(date);
                        String[] timeSp=time.split("");
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
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                        String todayDate=format.format(date);
                        Log.d(TAG, "run 이름: "+messageSp[0]);
                        Log.d(TAG, "run 메시지: "+messageSp[1]);
                        Log.d(TAG, "run 시간: "+time);
                        if(messageSp[0].equals(userName)){
                            if(entranceBoolean){
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],messageSp[1],realTime,todayDate,"1","0");
                                chatArrayList.add(chatItem);
                            }else{
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],messageSp[1],realTime,todayDate,"0","0");
                                chatArrayList.add(chatItem);
                            }
                        }else{
                            if(otherName.equals(messageSp[0])){
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],messageSp[1],realTime,todayDate,"1","0");
                                chatArrayList.add(chatItem);
                            }else{
                                Log.d(TAG, "run: 상대가 아님");
                            }

                        }
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
                    }else{ // 이미지 일때
                        Log.d(TAG, "run: 이미지임");
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                        String time=outputFormat.format(date);
                        String[] timeSp=time.split("");
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
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
                        String todayDate=format.format(date);
                        Log.d(TAG, "run 이름: "+messageSp[0]);
                        Log.d(TAG, "run 메시지: "+messageSp[1]);
                        Log.d(TAG, "run 시간: "+time);
                        if(messageSp[0].equals(userName)){
                            if(entranceBoolean){
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],"http://"+apiClient.goUri(messageSp[1]),realTime,todayDate,"1","1");
                                chatArrayList.add(chatItem);
                            }else{
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],"http://"+apiClient.goUri(messageSp[1]),realTime,todayDate,"0","1");
                                chatArrayList.add(chatItem);
                            }
                        }else{
                            if(otherName.equals(messageSp[0])){
                                ChatItem chatItem= new ChatItem("http://"+apiClient.goUri(messageSp[2]),messageSp[0],"http://"+apiClient.goUri(messageSp[1]),realTime,todayDate,"1","1");
                                chatArrayList.add(chatItem);
                            }else{
                                Log.d(TAG, "run: 상대가 아님");
                            }

                        }
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
                }
            }
        });
    }

    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    public void imageClick(){
        TextView titleText = findViewById(R.id.chatTitle);
        RecyclerView chatRecyclerView = findViewById(R.id.chattingRecyclerView);
        LinearLayout chattingLinear = findViewById(R.id.chattingTextLinear);
        LinearLayout chattingPhotoLinear = findViewById(R.id.chattingPhotoViewLinear);
        titleText.setVisibility(View.GONE);
        chatRecyclerView.setVisibility(View.GONE);
        chattingLinear.setVisibility(View.GONE);
        chattingPhotoLinear.setVisibility(View.VISIBLE);
    }

    public void imageCancel(){
        TextView titleText = findViewById(R.id.chatTitle);
        RecyclerView chatRecyclerView = findViewById(R.id.chattingRecyclerView);
        LinearLayout chattingLinear = findViewById(R.id.chattingTextLinear);
        LinearLayout chattingPhotoLinear = findViewById(R.id.chattingPhotoViewLinear);
        titleText.setVisibility(View.VISIBLE);
        chatRecyclerView.setVisibility(View.VISIBLE);
        chattingLinear.setVisibility(View.VISIBLE);
        chattingPhotoLinear.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed(){
        if(imageBoolean==true){
            imageCancel();
            imageBoolean=false;
        }else{
            finish();
        }
    }
}
