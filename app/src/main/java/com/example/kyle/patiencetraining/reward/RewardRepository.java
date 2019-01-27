package com.example.kyle.patiencetraining.reward;

import android.content.Context;

import com.example.kyle.patiencetraining.util.AppDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class RewardRepository {

    private RewardDao mRewardDao;
    private LiveData<List<Reward>> mRewards;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    RewardRepository(Context context) {
        AppDatabase mAppDatabase = AppDatabase.getInstance(context);
        mRewardDao = mAppDatabase.rewardDao();
        mRewards = mRewardDao.getAllRewards();
    }

    LiveData<List<Reward>> getAllRewards() {
        return mRewards;
    }

    LiveData<List<Reward>> getRewardsBefore() {
        return mRewardDao.getRewardsBefore();
    }

    LiveData<List<Reward>> getRewardsAfter() {
        return mRewardDao.getRewardsAfter();
    }

    public void insert(final Reward reward) {
        mExecutor.execute(() -> mRewardDao.insertRewards(reward));
    }

    public void update(final Reward reward) {
        mExecutor.execute(() -> mRewardDao.updateRewards(reward));
    }

    public void delete(final Reward reward) {
        mExecutor.execute(() -> mRewardDao.deleteRewards(reward));
    }
}