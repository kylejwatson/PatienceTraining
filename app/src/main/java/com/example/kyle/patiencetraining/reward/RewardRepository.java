package com.example.kyle.patiencetraining.reward;

import android.content.Context;

import com.example.kyle.patiencetraining.util.AppDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class RewardRepository {

    private AppDatabase mAppDatabase;
    private RewardDao mRewardDao;
    private LiveData<List<Reward>> mRewards;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public RewardRepository (Context context) {
        mAppDatabase = AppDatabase.getInstance(context);
        mRewardDao = mAppDatabase.rewardDao();
        mRewards = mRewardDao.getAllRewards();
    }

    public LiveData<List<Reward>> getAllRewards() {
        return mRewards;
    }

    public LiveData<List<Reward>> getRewardsBefore(long time) {
        return mRewardDao.getRewardsBefore(time);
    }

    public LiveData<List<Reward>> getRewardsAfter(long time) {
        return mRewardDao.getRewardsAfter(time);
    }

    public void insert(final Reward reward) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRewardDao.insertRewards(reward);
            }
        });
    }

    public void update(final Reward reward) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRewardDao.updateRewards(reward);
            }
        });
    }

    public void delete(final Reward reward) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRewardDao.deleteRewards(reward);
            }
        });
    }
}