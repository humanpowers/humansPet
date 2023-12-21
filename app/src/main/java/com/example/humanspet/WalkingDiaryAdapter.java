package com.example.humanspet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WalkingDiaryAdapter extends RecyclerView.Adapter<WalkingDiaryAdapter.ViewHolder> {

    private ArrayList<WalkingDiaryItem> walkingDiaryItemArrayList;

    public WalkingDiaryAdapter(ArrayList<WalkingDiaryItem> walkingDiaryItems){
        this.walkingDiaryItemArrayList = walkingDiaryItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private WalkingDiaryAdapter.RecyclerViewClickListener mListener;
    ApiClient apiClient=new ApiClient();

    public void setOnClickListener(WalkingDiaryAdapter.RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public WalkingDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.walking_diary_item,parent,false);
        return new WalkingDiaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkingDiaryAdapter.ViewHolder holder, int position) {
        holder.date.setText(walkingDiaryItemArrayList.get(position).getDate());
        holder.calorie.setText(walkingDiaryItemArrayList.get(position).getCalorie());
        holder.distance.setText(walkingDiaryItemArrayList.get(position).getDistance());
        holder.time.setText(walkingDiaryItemArrayList.get(position).getTime());
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
        return walkingDiaryItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView distance;
        TextView time;
        TextView calorie;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.walkingItemDate);
            distance=itemView.findViewById(R.id.walkingItemDistance);
            time=itemView.findViewById(R.id.walkingItemTime);
            calorie=itemView.findViewById(R.id.walkingItemCalories);

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
