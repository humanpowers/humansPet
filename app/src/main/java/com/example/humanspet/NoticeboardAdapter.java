package com.example.humanspet;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.humanspet.Interface.LikesBtnClickInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeboardAdapter extends RecyclerView.Adapter<NoticeboardAdapter.ViewHolder> {

    private SharedPreferences preferences;
    String userId;
    private ArrayList<NoticeboardItem> noticeboardItemsArrayList;

    public NoticeboardAdapter(ArrayList<NoticeboardItem> noticeboardItems){
        this.noticeboardItemsArrayList = noticeboardItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private NoticeboardAdapter.RecyclerViewClickListener mListener;
    ApiClient apiClient=new ApiClient();

    public void setOnClickListener(NoticeboardAdapter.RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public NoticeboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noticeboard_item,parent,false);
        return new NoticeboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeboardAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load("http://"+apiClient.goUri(noticeboardItemsArrayList.get(position).getUserImage())).thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).override(300,300).into(holder.userImage);
        holder.userName.setText(noticeboardItemsArrayList.get(position).getUserName());
        holder.likesCount.setText(String.valueOf(noticeboardItemsArrayList.get(position).getLikeCount()));
        holder.commentCount.setText(String.valueOf(noticeboardItemsArrayList.get(position).getCommentCount()));
        if(noticeboardItemsArrayList.get(position).getComment().equals("null")){
            holder.comment.setText("댓글이 없습니다.");
        }else{
            holder.comment.setText(noticeboardItemsArrayList.get(position).getComment());
        }
        holder.title.setText(noticeboardItemsArrayList.get(position).getTitle());
        holder.address.setText(noticeboardItemsArrayList.get(position).getDoName()+" "+noticeboardItemsArrayList.get(position).getSiName());
        Glide.with(holder.itemView.getContext()).load("http://"+apiClient.goUri(noticeboardItemsArrayList.get(position).getUploadImage())).thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).override(500,500).into(holder.uploadImage);
        if(mListener!=null){
            final int pos=position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onImageButtonClicker(pos);
                }
            });
            holder.likesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences = holder.itemView.getContext().getSharedPreferences("USER",MODE_PRIVATE);
                    userId=preferences.getString("USERID","");
                    LikesBtnClickInterface api = ApiClient.getApiClient().create(LikesBtnClickInterface.class);
                    Call<String> call = api.LikesChick(userId,noticeboardItemsArrayList.get(pos).getTitle());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("어댑터", "onResponse: "+response.body());
                            String[] responseSp=response.body().split("!!@!!");
                            if(response.body().equals("본인계정")){
                                Toast.makeText(holder.itemView.getContext(), "본인 게시물입니다.", Toast.LENGTH_SHORT).show();
                            }else if(responseSp[0].equals("취소")){
                                holder.likesBtn.setImageResource(R.drawable.heart_none);
                                holder.likesCount.setText(responseSp[1]);
                            }else if(responseSp[0].equals("확인")){
                                holder.likesCount.setText(responseSp[1]);
                                holder.likesBtn.setImageResource(R.drawable.heart_yes);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noticeboardItemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName;
        ImageView uploadImage;
        TextView likesCount;
        TextView commentCount;
        TextView comment;
        TextView title;
        TextView address;
        ImageButton likesBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.noticeboardItemUserImage);
            userName=itemView.findViewById(R.id.noticeboardItemUserName);
            uploadImage=itemView.findViewById(R.id.noticeboardItemImage);
            likesCount=itemView.findViewById(R.id.noticeboardItemLikesCount);
            commentCount=itemView.findViewById(R.id.noticeboardItemCommentCount);
            comment=itemView.findViewById(R.id.noticeboardItemCommentText);
            address= itemView.findViewById(R.id.noticeboardItemAddressText);
            title = itemView.findViewById(R.id.noticeboardTitleText);
            likesBtn=itemView.findViewById(R.id.noticeboardItemLikesImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onImageButtonClicker(position);
                        }
                    }
                }
            });
        }
    }
}
