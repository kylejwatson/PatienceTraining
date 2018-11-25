package com.example.kyle.patiencetraining;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class UnlockedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView nameTextView;
    public final UnlockedClickListener unlockedClickListener;

    public UnlockedViewHolder(@NonNull View itemView, UnlockedClickListener unlockedClickListener) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameUCellTextView);
        this.unlockedClickListener = unlockedClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        unlockedClickListener.rewardOnClick(getAdapterPosition());
    }

    public interface UnlockedClickListener{
        void rewardOnClick(int i);
    }
}
