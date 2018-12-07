package com.example.kyle.patiencetraining;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UnlockedFragment extends Fragment implements RewardFragment {


    private List<Reward> mUnlockedRewards = new ArrayList<>();
    private UnlockedAdapter mUnlockedAdapter;
    public final static int TASK_GET_ALL_REWARDS = 0;
    public final static int TASK_DELETE_REWARDS = 1;
    private static AppDatabase sDatabase;
    private long rewardId;
    private AlertDialog.Builder deleteWarning;
    private View view;

    public UnlockedFragment() {
        // Required empty public constructor
    }


    private void onUnlockedClicked(int i){
        ClickedRewardDialog dialog = new UnlockedClickedReward(getContext(), mUnlockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener(){
            @Override
            public void onDelete(final int position) {
                deleteWarning.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new RewardAsyncTask(TASK_DELETE_REWARDS).execute(mUnlockedRewards.get(position));
                    }
                }).show();
            }
        });
        dialog.show();
    }

    public void updateUI(){
        if(mUnlockedAdapter == null) {
            mUnlockedAdapter = new UnlockedAdapter(mUnlockedRewards, new UnlockedViewHolder.UnlockedClickListener() {
                @Override
                public void rewardOnClick(int i) {
                    onUnlockedClicked(i);
                }
            });
            RecyclerView unlockedRecyclerView = view.findViewById(R.id.unlockedRecyclerView);
            unlockedRecyclerView.setAdapter(mUnlockedAdapter);
            unlockedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        mUnlockedAdapter.notifyDataSetChanged();
        for(int i = 0; i < mUnlockedRewards.size(); i++){
            if(mUnlockedRewards.get(i).getId() == rewardId){
                onUnlockedClicked(i);
                rewardId = -1;
            }
        }
    }

    public void separateList(List<Reward> rewardList){
        //Make sure list is ordered by date
        mUnlockedRewards.clear();
        for (Reward reward : rewardList) {
            assignRewardToList(reward);
        }
        updateUI();
    }

    public void setNotification(Reward reward){
        JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(getActivity().JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(reward.getNotificationJobId());
    }

    public void assignRewardToList(Reward reward){
        Date now = new Date();
        if(now.after(new Date(reward.getFinish()))) {
            mUnlockedRewards.add(reward);
            reward.setNotificationSet(false);
            setNotification(reward);
        }
    }

    private void onRewardDbUpdated(List<Reward> list) {
        separateList(list);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_unlocked, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        sDatabase = AppDatabase.getInstance(context);
        Intent intent = getActivity().getIntent();
        rewardId = intent.getLongExtra(NotificationService.REWARD_ID_EXTRA,-1);
        Log.d("ID", "onCreate: " + rewardId);

        deleteWarning = new AlertDialog.Builder(context)
                .setTitle(R.string.delete_confirmation).setCancelable(true).setIcon(R.drawable.ic_warning)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
        new RewardAsyncTask(TASK_GET_ALL_REWARDS).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class RewardAsyncTask extends AsyncTask<Reward, Void, List<Reward>> {

        private int task;
        public RewardAsyncTask(int task){
            this.task = task;
        }
        @Override
        protected List<Reward> doInBackground(Reward... rewards) {
            switch (task){
                case TASK_DELETE_REWARDS:
                    sDatabase.rewardDao().deleteRewards(rewards[0]);
                    break;
            }
            return sDatabase.rewardDao().getAllRewards();
        }

        @Override
        protected void onPostExecute(List<Reward> list) {
            super.onPostExecute(list);
            onRewardDbUpdated(list);
        }
    }
}
