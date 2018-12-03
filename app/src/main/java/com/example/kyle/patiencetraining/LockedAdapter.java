package com.example.kyle.patiencetraining;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

public class LockedAdapter extends RecyclerView.Adapter<LockedViewHolder> {
    private List<Reward> mRewards;
    private LockedViewHolder.LockedClickListener mLockedClickListener;
    private Context mContext;

    public LockedAdapter(Context context, List<Reward> mRewards, LockedViewHolder.LockedClickListener mLockedClickListener) {
        this.mRewards = mRewards;
        this.mLockedClickListener = mLockedClickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public LockedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.locked_grid_cell, viewGroup, false);

        return new LockedViewHolder(view,mLockedClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LockedViewHolder lockedViewHolder, int i) {
        Reward reward = mRewards.get(i);

        lockedViewHolder.nameTextView.setText(reward.getName());
        Date date = reward.getFinish();
        String timeString = TimeString.getTimeString(new Date(), date, mContext);
        String time = mContext.getString(R.string.countdown,timeString);
        lockedViewHolder.timeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
