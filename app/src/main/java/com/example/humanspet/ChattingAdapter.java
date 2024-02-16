package com.example.humanspet;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {
    private SharedPreferences preferences;
    String myName = "";

    private ArrayList<ChatItem> chatItemArrayList;

    public ChattingAdapter(ArrayList<ChatItem> chatItems, String myName){
        this.chatItemArrayList = chatItems;
        this.myName=myName;

    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if(position>0){
            if(chatItemArrayList.get(position).getDate().equals(chatItemArrayList.get(position-1).getDate())){//날짜 같음
                if(chatItemArrayList.get(position).getName().equals(myName)) { // email과 stMyEmail과 같으면
                    return 1; // 내 메시지
                } else { // stMyEmail과 다르면
                    return 2;  // 내 메시지가 아님
                }
            }else{//날짜 다름
                if(chatItemArrayList.get(position).getName().equals(myName)) { // email과 stMyEmail과 같으면
                    return 3; // 내 메시지
                } else { // stMyEmail과 다르면
                    return 4;  // 내 메시지가 아님
                }
            }
        }else{//position==0일때
            if(chatItemArrayList.get(position).getName().equals(myName)) { // email과 stMyEmail과 같으면
                return 3; // 내 메시지
            } else { // stMyEmail과 다르면
                return 4;  // 내 메시지가 아님
            }
        }

    }

    private ChattingAdapter.RecyclerViewClickListener mListener;

    public void setOnClickListener(ChattingAdapter.RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public ChattingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==1){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat,parent,false);
        }else if(viewType==2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat, parent, false);
        }else if(viewType==3){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_change_date,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat_change_date, parent, false);
        }
        return new ChattingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(chatItemArrayList.get(position).getImage()).centerCrop().thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).override(100, 100).into(holder.image);
        holder.name.setText(chatItemArrayList.get(position).getName());
        if(chatItemArrayList.get(position).getImageChatting().equals("1")){
            holder.message.setVisibility(View.GONE);
            holder.sendImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(chatItemArrayList.get(position).getMessage()).centerCrop().thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).override(300, 300).into(holder.sendImage);
        }else{
            holder.message.setVisibility(View.VISIBLE);
            holder.sendImage.setVisibility(View.GONE);
            holder.message.setText(chatItemArrayList.get(position).getMessage());
        }
        holder.time.setText(chatItemArrayList.get(position).getTime());
        if(holder.getItemViewType()==3||holder.getItemViewType()==4){
            holder.date.setText(chatItemArrayList.get(position).getDate());
        }
        if(chatItemArrayList.get(position).getRead().equals("1")){
            holder.read.setVisibility(View.GONE);
        }else{
            holder.read.setVisibility(View.VISIBLE);
        }
        if(position!=chatItemArrayList.size()-1){
            if(chatItemArrayList.get(position).getTime().equals(chatItemArrayList.get(position+1).getTime())&&chatItemArrayList.get(position).getName().equals(chatItemArrayList.get(position+1).getName())){
                holder.time.setVisibility(View.GONE);
            }else{
                holder.time.setVisibility(View.VISIBLE);
            }
        }else{
            holder.time.setVisibility(View.VISIBLE);
        }
        if(position!=0){
            if(chatItemArrayList.get(position).getName().equals(chatItemArrayList.get(position-1).getName())){
                holder.image.setVisibility(View.INVISIBLE);
                holder.image.getLayoutParams().height=0;
                holder.name.setVisibility(View.GONE);
            }else{
                holder.image.setVisibility(View.VISIBLE);
                holder.image.getLayoutParams().height=90;
                holder.name.setVisibility(View.VISIBLE);
            }
        }else{
            holder.image.setVisibility(View.VISIBLE);
            holder.image.getLayoutParams().height=90;
            holder.name.setVisibility(View.VISIBLE);
        }
        if (mListener != null) {
            final int pos = position;
            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onImageButtonClicker(pos);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return chatItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView message;
        TextView time;
        ImageView image;
        TextView date;
        TextView read;
        ImageView sendImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.chatItemImage);
            name=itemView.findViewById(R.id.chatName);
            message=itemView.findViewById(R.id.chatMessage);
            time=itemView.findViewById(R.id.chatTime);
            date=itemView.findViewById(R.id.chatDate);
            read=itemView.findViewById(R.id.chatRead);
            sendImage=itemView.findViewById(R.id.chatSendImage);
        }
    }
}
