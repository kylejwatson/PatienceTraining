package com.example.kyle.patiencetraining.Reward.UnlockedReward;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Reward.Reward;

import java.util.List;

public class UnlockedAdapter extends RecyclerView.Adapter<UnlockedViewHolder> {
    private List<Reward> mRewards;
    private UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener;
    private int clickOnBuild = -1;

    UnlockedAdapter(List<Reward> mRewards, UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener) {
        this.mRewards = mRewards;
        this.mUnlockedClickListener = mUnlockedClickListener;
    }

    void setClickOnBuild(int clickOnBuild){
        this.clickOnBuild = clickOnBuild;
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

        if(clickOnBuild == i) {
            clickOnBuild = -1;
            unlockedViewHolder.onClick(unlockedViewHolder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
