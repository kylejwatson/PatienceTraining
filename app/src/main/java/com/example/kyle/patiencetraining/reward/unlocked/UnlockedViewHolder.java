package com.example.kyle.patiencetraining.reward.unlocked;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;

public class UnlockedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    final TextView nameTextView;
    final TextView timeTextView;
    final TextView dateTextView;
    final ImageView iconImageView;
    private final UnlockedClickListener unlockedClickListener;

    UnlockedViewHolder(@NonNull View itemView, UnlockedClickListener unlockedClickListener) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameUCellTextView);
        timeTextView = itemView.findViewById(R.id.timeUCellTextView);
        dateTextView = itemView.findViewById(R.id.dateUCellTextView);
        iconImageView = itemView.findViewById(R.id.UCellImageView);
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
