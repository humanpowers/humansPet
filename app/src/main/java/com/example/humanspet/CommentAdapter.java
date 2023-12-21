package com.example.humanspet;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<CommentItem> commentItemsArrayList;

    public CommentAdapter(ArrayList<CommentItem> commentItems){
        this.commentItemsArrayList = commentItems;
    }

    public  interface RecyclerViewClickListener{
        void onImageButtonClicker(int position);
    }

    private CommentAdapter.RecyclerViewClickListener mListener;

    public void setOnClickListener(CommentAdapter.RecyclerViewClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(commentItemsArrayList.get(position).getImage()).centerCrop().override(200, 200).into(holder.image);
        holder.comment.setText(commentItemsArrayList.get(position).getComment());
        holder.name.setText(commentItemsArrayList.get(position).getName());
        if(commentItemsArrayList.get(position).isPush()){
            animateItemBackgroundColor(holder.itemView,Color.parseColor("#3303DAC5"),Color.parseColor("#0503DAC5"));
        }
        if (mListener != null) {
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animateItemBackgroundColor(holder.itemView, Color.parseColor("#dddddddd"),Color.parseColor("#0503DAC5"));
                    mListener.onImageButtonClicker(pos);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentItemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView comment;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.noticeboardCommentName);
            comment=itemView.findViewById(R.id.noticeboardCommentContent);
            image=itemView.findViewById(R.id.noticeboardCommentImage);
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

    private void animateItemBackgroundColor(final View view, int startColor, int endColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                startColor,
                endColor
        );
        colorAnimation.setDuration(3000); // 1초 동안 애니메이션 실행
        colorAnimation.setInterpolator(new DecelerateInterpolator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
