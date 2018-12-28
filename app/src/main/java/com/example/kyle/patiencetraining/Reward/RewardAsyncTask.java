package com.example.kyle.patiencetraining.Reward;

import android.content.Context;
import android.os.AsyncTask;
import com.example.kyle.patiencetraining.Util.AppDatabase;

import java.util.Date;
import java.util.List;

public class RewardAsyncTask extends AsyncTask<Reward, Void, List<Reward>> {

    public final static int TASK_GET_ALL_REWARDS = 0;
    public final static int TASK_DELETE_REWARDS = 1;
    public final static int TASK_INSERT_REWARDS = 3;
    public final static int TASK_UPDATE_REWARDS = 2;
    public final static int REWARDS_BEFORE = 0;
    public final static int REWARDS_AFTER = 1;
    private int task;
    private int filter;
    private static AppDatabase sDatabase;
    private OnPostExecuteListener listener;
    public RewardAsyncTask(Context context, int task, int filter, OnPostExecuteListener listener){
        this.task = task;
        this.listener = listener;
        this.filter = filter;
        sDatabase = AppDatabase.getInstance(context);
    }
    @Override
    protected List<Reward> doInBackground(Reward... rewards) {
        switch (task){
            case TASK_INSERT_REWARDS:
                sDatabase.rewardDao().insertRewards(rewards[0]);
                break;
            case TASK_DELETE_REWARDS:
                sDatabase.rewardDao().deleteRewards(rewards[0]);
                break;
            case TASK_UPDATE_REWARDS:
                sDatabase.rewardDao().updateRewards(rewards[0]);
                break;
        }
        long now = new Date().getTime();
        if(filter == REWARDS_AFTER)
            return sDatabase.rewardDao().getRewardsAfter(now);
        return sDatabase.rewardDao().getRewardsBefore(now);
    }

    @Override
    protected void onPostExecute(List<Reward> list) {
        super.onPostExecute(list);
        listener.onPostExecute(list);
    }

    public interface OnPostExecuteListener{
        void onPostExecute(List<Reward> list);
    }
}
