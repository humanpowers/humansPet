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

public class DiaryDiaryAdapter extends RecyclerView.Adapter<DiaryDiaryAdapter.ViewHolder> {

    private ArrayList<DiaryDiaryItem> diaryDiaryItemArrayList;

    public DiaryDiaryAdapter(ArrayList<DiaryDiaryItem> diaryDiaryItems){
        this.diaryDiaryItemArrayList = diaryDiaryItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private RecyclerViewClickListener mListener;
    ApiClient apiClient=new ApiClient();

    @Override
    public int getItemViewType(int position) {
        if(diaryDiaryItemArrayList.get(position).getType().equals("1")){
            return 1;
        }else{
            return 2;
        }
    }

    public void setOnClickListener(RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public DiaryDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_diary_item,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_diary_item_grid,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryDiaryAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load("http://"+apiClient.goUri(diaryDiaryItemArrayList.get(position).getImage())).thumbnail(Glide.with(holder.itemView.getContext()).load(R.raw.loadinggif)).override(200,200).into(holder.image);
        holder.title.setText(diaryDiaryItemArrayList.get(position).getTitle());
        holder.date.setText(diaryDiaryItemArrayList.get(position).getDate());
        holder.content.setText(diaryDiaryItemArrayList.get(position).getContent());
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
        return diaryDiaryItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView date;
        TextView title;
        TextView content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.diaryDiaryImage);
            date=itemView.findViewById(R.id.diaryDiaryDate);
            title=itemView.findViewById(R.id.diaryDiaryTitle);
            content=itemView.findViewById(R.id.diaryDiaryContent);
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
