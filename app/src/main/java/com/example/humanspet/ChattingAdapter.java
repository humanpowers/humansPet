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
        if(chatItemArrayList.get(position).getName().equals(myName)) { // email과 stMyEmail과 같으면
            return 1; // 내 메시지
        } else { // stMyEail과 다르면
            return 2;  // 내 메시지가 아님
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
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat, parent, false);
        }
        return new ChattingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(chatItemArrayList.get(position).getImage()).centerCrop().override(100, 100).into(holder.image);
        holder.name.setText(chatItemArrayList.get(position).getName());
        holder.message.setText(chatItemArrayList.get(position).getMessage());
        holder.date.setText(chatItemArrayList.get(position).getDate());

        if (mListener != null) {
            final int pos = position;
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
        return chatItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView message;
        TextView date;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.chatItemImage);
            name=itemView.findViewById(R.id.chatName);
            message=itemView.findViewById(R.id.chatMessage);
            date=itemView.findViewById(R.id.chatDate);
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
