package com.example.kyle.patiencetraining.reward.unlocked;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kyle.patiencetraining.main.SectionsPagerAdapter;
import com.example.kyle.patiencetraining.reward.MainViewModel;
import com.example.kyle.patiencetraining.reward.ClickedRewardDialog;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.reward.Reward;
import com.example.kyle.patiencetraining.util.Score;
import com.example.kyle.patiencetraining.util.ScoreAsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UnlockedFragment extends Fragment {


    private List<Reward> mUnlockedRewards = new ArrayList<>();
    private UnlockedAdapter mUnlockedAdapter;
    private long rewardId;
    private AlertDialog.Builder deleteWarning;
    private JobScheduler jobScheduler;
    private LinearLayoutManager linearLayoutManager;
    private TextView emptyTextView;
    private MainViewModel mainViewModel;

    public UnlockedFragment() {
        // Required empty public constructor
    }

    private void updateUI(){
        mUnlockedAdapter.notifyDataSetChanged();
        for(int i = 0; i < mUnlockedRewards.size(); i++){
            if(mUnlockedRewards.get(i).getId() == rewardId){
                mUnlockedAdapter.setClickOnBuild(i);
                linearLayoutManager.scrollToPosition(i);
                rewardId = -1;
            }
        }
        if(mUnlockedRewards.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void separateList(List<Reward> rewardList){
        //Make sure list is ordered by date
        mUnlockedRewards.clear();
        for (Reward reward : rewardList) {
            assignRewardToList(reward);
        }
        Score[] scores = new Score[mUnlockedRewards.size()];
        for (int i = 0; i < scores.length; i++) {
            long millis = mUnlockedRewards.get(i).getFinish() - mUnlockedRewards.get(i).getStart();
            scores[i] = new Score(mUnlockedRewards.get(i).getId(), millis, 0);
        }
        new ScoreAsyncTask(getContext(), ScoreAsyncTask.TASK_INSERT_SCORE).execute(scores);
        updateUI();
    }

    private void setNotification(Reward reward){
        jobScheduler.cancel(reward.getNotificationJobId());
    }

    private void assignRewardToList(Reward reward){
            mUnlockedRewards.add(reward);
            reward.setNotificationSet(false);
            setNotification(reward);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unlocked, container, false);
        RecyclerView unlockedRecyclerView = view.findViewById(R.id.unlockedRecyclerView);
        unlockedRecyclerView.setAdapter(mUnlockedAdapter);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        unlockedRecyclerView.setLayoutManager(linearLayoutManager);
        emptyTextView = view.findViewById(R.id.emptyUnlockedTextView);
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if(args != null)
            rewardId = args.getLong(SectionsPagerAdapter.ARG_REWARD_ID, -1);

        jobScheduler = (JobScheduler) context.getSystemService(Activity.JOB_SCHEDULER_SERVICE);

        deleteWarning = new AlertDialog.Builder(context)
                .setTitle(R.string.delete_confirmation).setCancelable(true).setIcon(R.drawable.ic_warning)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });

        mUnlockedAdapter = new UnlockedAdapter(mUnlockedRewards, new UnlockedViewHolder.UnlockedClickListener() {
            @Override
            public void rewardOnClick(int i) {
                ClickedRewardDialog dialog = new UnlockedClickedReward(context, mUnlockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener(){
                    @Override
                    public void onDelete(final int position) {
                        deleteWarning.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mainViewModel.delete(mUnlockedRewards.get(position));
                            }
                        }).show();
                    }
                });
                dialog.show();
            }
        });

        mainViewModel = new MainViewModel(context.getApplicationContext());
        mainViewModel.getRewardsBefore(new Date().getTime()).observe(this, new Observer<List<Reward>>() {
            @Override
            public void onChanged(List<Reward> rewards) {
                separateList(rewards);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
