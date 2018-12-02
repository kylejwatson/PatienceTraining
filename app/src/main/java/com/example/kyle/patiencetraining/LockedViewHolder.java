package com.example.kyle.patiencetraining;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LockedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    public final TextView nameTextView;
    public final TextView timeTextView;
    public final LockedClickListener lockedClickListener;
    public final LongClickListener longClickListener;

    public LockedViewHolder(@NonNull View itemView, LockedClickListener lockedClickListener, LongClickListener longClickListener) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameCellTextView);
        timeTextView = itemView.findViewById(R.id.timeCellTextView);
        this.lockedClickListener = lockedClickListener;
        this.longClickListener = longClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        lockedClickListener.rewardOnClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        return longClickListener.rewardOnLongClick(getAdapterPosition());
    }

    public interface LongClickListener{
        boolean rewardOnLongClick(int i);
    }

    public interface LockedClickListener{
        void rewardOnClick(int i);
    }
}
