package com.example.kyle.patiencetraining.reward.locked;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.reward.Reward;
import com.example.kyle.patiencetraining.util.TimeString;

import java.text.DateFormat;
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
        Date nowDate = new Date();
        Date finishDate = new Date(reward.getFinish());

        double totalTime = reward.getFinish() - reward.getStart();
        double timeTilFinish = reward.getFinish() - nowDate.getTime();

        double percentageComplete = (1d - (timeTilFinish/totalTime)) ;

        lockedViewHolder.progressBar.setProgress((int)(percentageComplete*100));

        String timeString = DateFormat.getDateInstance(DateFormat.SHORT).format(finishDate);
        String time = mContext.getString(R.string.countdown,timeString);
        lockedViewHolder.timeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
