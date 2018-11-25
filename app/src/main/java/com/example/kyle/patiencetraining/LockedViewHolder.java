package com.example.kyle.patiencetraining;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LockedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView nameTextView;
    public final TextView timeTextView;
    public final LockedClickListener lockedClickListener;

    public LockedViewHolder(@NonNull View itemView, LockedClickListener lockedClickListener) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameCellTextView);
        timeTextView = itemView.findViewById(R.id.timeCellTextView);
        this.lockedClickListener = lockedClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        lockedClickListener.rewardOnClick(getAdapterPosition());
    }

    public interface LockedClickListener{
        void rewardOnClick(int i);
    }
}
