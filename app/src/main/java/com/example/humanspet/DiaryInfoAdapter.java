package com.example.humanspet;

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

public class DiaryInfoAdapter extends RecyclerView.Adapter<DiaryInfoAdapter.ViewHolder> {
    private int selectedItem = 0;
    private ArrayList<DiaryInfoItem> diaryInfoItemArrayList;
    public DiaryInfoAdapter(ArrayList<DiaryInfoItem> diaryInfoItems){
        this.diaryInfoItemArrayList = diaryInfoItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private RecyclerViewClickListener mListener;
    ApiClient apiClient=new ApiClient();

    public void setOnClickListener(RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public DiaryInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_info_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryInfoAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load("http://"+apiClient.goUri(diaryInfoItemArrayList.get(position).getImage())).thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).centerCrop().override(100,100).into(holder.image);
        holder.name.setText(diaryInfoItemArrayList.get(position).getName());
        if (position == selectedItem) {
            holder.itemView.setBackgroundColor(Color.parseColor("#2503DAC5")); // 선택한 아이템의 배경색 설정
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#0B03DAC5")); // 다른 아이템의 배경색은 투명으로 설정
        }
        if(mListener!=null){
            final int pos=position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItem = pos;
                    notifyDataSetChanged();
                    mListener.onImageButtonClicker(pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return diaryInfoItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.diaryItemImage);
            name=itemView.findViewById(R.id.diaryItemName);
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
