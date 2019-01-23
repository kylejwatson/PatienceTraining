package com.example.kyle.patiencetraining.reward;


import android.content.Context;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private RewardRepository mRepository;
    private LiveData<List<Reward>> mRewards;

    public MainViewModel(Context context) {
        mRepository = new RewardRepository(context);
        mRewards = mRepository.getAllRewards();
    }

    public LiveData<List<Reward>> getRewards() {
        return mRewards;
    }

    public LiveData<List<Reward>> getRewardsBefore(long time) {
        return mRepository.getRewardsBefore(time);
    }

    public LiveData<List<Reward>> getRewardsAfter(long time) {
        return mRepository.getRewardsAfter(time);
    }

    public void insert(Reward reward) {
        mRepository.insert(reward);
    }

    public void update(Reward reward) {
        mRepository.update(reward);
    }

    public void delete(Reward reward) {
        mRepository.delete(reward);
    }
}