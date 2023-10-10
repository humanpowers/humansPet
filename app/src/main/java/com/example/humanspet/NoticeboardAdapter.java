package com.example.humanspet;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NoticeboardAdapter extends RecyclerView.Adapter<NoticeboardAdapter.ViewHolder> {

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
