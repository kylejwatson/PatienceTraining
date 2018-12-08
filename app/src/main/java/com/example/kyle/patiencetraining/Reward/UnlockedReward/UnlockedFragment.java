package com.example.kyle.patiencetraining.Reward.UnlockedReward;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.Reward.RewardAsyncTask;
import com.example.kyle.patiencetraining.Reward.ClickedRewardDialog;
import com.example.kyle.patiencetraining.Util.NotificationService;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Reward.Reward;
import com.example.kyle.patiencetraining.Reward.RewardFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UnlockedFragment extends Fragment implements RewardFragment {


    private List<Reward> mUnlockedRewards = new ArrayList<>();
    private UnlockedAdapter mUnlockedAdapter;
    private long rewardId;
    private AlertDialog.Builder deleteWarning;
    private View view;
    private RewardAsyncTask.OnPostExecuteListener listener = new RewardAsyncTask.OnPostExecuteListener() {
        @Override
        public void onPostExecute(List<Reward> list) {
            onRewardDbUpdated(list);
        }
    };

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
                        new RewardAsyncTask(getContext(), RewardAsyncTask.TASK_DELETE_REWARDS, listener).execute(mUnlockedRewards.get(position));
                    }
                }).show();
            }
        });
        dialog.show();
    }

    private void updateUI(){
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

    private void setNotification(Reward reward){
        JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Activity.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(reward.getNotificationJobId());
    }

    private void assignRewardToList(Reward reward){
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_unlocked, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
        new RewardAsyncTask(context, RewardAsyncTask.TASK_GET_ALL_REWARDS, listener).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
