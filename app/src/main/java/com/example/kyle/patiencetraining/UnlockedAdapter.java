package com.example.kyle.patiencetraining;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UnlockedAdapter extends RecyclerView.Adapter<UnlockedViewHolder> {
    private List<Reward> mRewards;
    private UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener;

    public UnlockedAdapter(List<Reward> mRewards, UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener) {
        this.mRewards = mRewards;
        this.mUnlockedClickListener = mUnlockedClickListener;
    }

    @NonNull
    @Override
    public UnlockedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.unlocked_grid_cell, viewGroup, false);

        return new UnlockedViewHolder(view, mUnlockedClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UnlockedViewHolder unlockedViewHolder, int i) {
        Reward reward = mRewards.get(i);
        unlockedViewHolder.nameTextView.setText(reward.getName());
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
