package com.example.kyle.patiencetraining.Reward.LockedReward;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Reward.Reward;
import com.example.kyle.patiencetraining.Util.TimeString;

import java.util.Date;
import java.util.List;

public class LockedAdapter extends RecyclerView.Adapter<LockedViewHolder> {
    private List<Reward> mRewards;
    private LockedViewHolder.LockedClickListener mLockedClickListener;
    private Context mContext;

    LockedAdapter(List<Reward> mRewards, LockedViewHolder.LockedClickListener mLockedClickListener) {
        this.mRewards = mRewards;
        this.mLockedClickListener = mLockedClickListener;
    }

    @NonNull
    @Override
    public LockedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.locked_grid_cell, viewGroup, false);

        return new LockedViewHolder(view,mLockedClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LockedViewHolder lockedViewHolder, int i) {
        Reward reward = mRewards.get(i);

        lockedViewHolder.nameTextView.setText(reward.getName());
        Date date = new Date(reward.getFinish());
        String timeString = TimeString.getTimeString(new Date(), date, mContext);
        String time = mContext.getString(R.string.countdown,timeString);
        lockedViewHolder.timeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
