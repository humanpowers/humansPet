package com.example.humanspet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChattingRoomAdapter extends RecyclerView.Adapter<ChattingRoomAdapter.ViewHolder>{
    private ArrayList<ChattingRoomItem> chattingRoomItemArrayList;
    public ChattingRoomAdapter(ArrayList<ChattingRoomItem> chatItems){
        this.chattingRoomItemArrayList = chatItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private ChattingRoomAdapter.RecyclerViewClickListener mListener;

    public void setOnClickListener(ChattingRoomAdapter.RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public ChattingRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ChattingRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingRoomAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(chattingRoomItemArrayList.get(position).getImage()).centerCrop().override(255,255).into(holder.image);
        holder.title.setText(chattingRoomItemArrayList.get(position).getTitle());
        holder.content.setText(chattingRoomItemArrayList.get(position).getContent());
        holder.date.setText(chattingRoomItemArrayList.get(position).getDate());
        if(chattingRoomItemArrayList.get(position).getRead()!=0){
            holder.readCount.setText(Integer.toString(chattingRoomItemArrayList.get(position).getRead()));
            holder.readCount.setVisibility(View.VISIBLE);
        }else{
            holder.readCount.setText(Integer.toString(chattingRoomItemArrayList.get(position).getRead()));
            holder.readCount.setVisibility(View.GONE);
        }
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
        return chattingRoomItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView content;
        TextView date;
        TextView readCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.chatImage);
            title=itemView.findViewById(R.id.chatTitleText);
            content=itemView.findViewById(R.id.chatContentText);
            date=itemView.findViewById(R.id.chatDateText);
            readCount=itemView.findViewById(R.id.chatReadCount);
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
