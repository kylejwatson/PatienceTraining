package com.example.kyle.patiencetraining.reward.unlocked;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;

public class UnlockedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    final TextView nameTextView;
    private final UnlockedClickListener unlockedClickListener;

    UnlockedViewHolder(@NonNull View itemView, UnlockedClickListener unlockedClickListener) {
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
