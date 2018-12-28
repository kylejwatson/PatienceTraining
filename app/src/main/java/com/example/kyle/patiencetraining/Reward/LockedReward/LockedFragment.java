package com.example.kyle.patiencetraining.Reward.LockedReward;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kyle.patiencetraining.Reward.RewardAsyncTask;
import com.example.kyle.patiencetraining.Reward.ClickedRewardDialog;
import com.example.kyle.patiencetraining.MainUI.MainActivity;
import com.example.kyle.patiencetraining.MainUI.ModifyRewardActivity;
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

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class LockedFragment extends Fragment implements RewardFragment {

    /**
     * Todo: change rewardDAO so that theres two functions, one to get rewards that havent finished and one that have so list sorting isn't needed.
     */

    public static final String REWARD_NAME_BUNDLE = "RewardName";
    public static final String REWARD_ID_BUNDLE = "RewardID";

    public static final int MOD_REQUEST = 1;
    private AlertDialog.Builder deleteWarning;
    private LockedAdapter mLockedAdapter;
    private List<Reward> mLockedRewards = new ArrayList<>();
    private JobScheduler jobScheduler;
    private ComponentName componentName;
    private TextView emptyTextView;
    private RewardAsyncTask.OnPostExecuteListener listener = new RewardAsyncTask.OnPostExecuteListener() {
        @Override
        public void onPostExecute(List<Reward> list) {
            onRewardDbUpdated(list);
        }
    };

    private LockedClickedReward.OnEditListener editListener = new LockedClickedReward.OnEditListener() {
        @Override
        public void onEdit(int position) {
            editReward(position);
        }
    };

    public LockedFragment() {
        // Required empty public constructor
    }

    private void editReward(int position){
        Intent intent = new Intent(getContext(),ModifyRewardActivity.class);
        intent.putExtra(MainActivity.REWARD_EXTRA, mLockedRewards.get(position));
        startActivityForResult(intent, MOD_REQUEST);
    }

    private void updateUI(){
        mLockedAdapter.notifyDataSetChanged();

        if(mLockedRewards.isEmpty()){
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setNotification(Reward reward){
        if(reward.isNotificationSet()) {
            Date now = new Date();
            long millis = reward.getFinish() - now.getTime();
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(REWARD_NAME_BUNDLE, reward.getName());
            bundle.putLong(REWARD_ID_BUNDLE, reward.getId());
            jobScheduler.schedule(new JobInfo.Builder(reward.getNotificationJobId(),
                    componentName)
                    .setMinimumLatency(millis)
                    .setExtras(bundle)
                    .build());
        }else{
            jobScheduler.cancel(reward.getNotificationJobId());
        }
    }

    private void assignRewardToList(Reward reward){
        Date now = new Date();
        if(now.before(new Date(reward.getFinish()))) {
            mLockedRewards.add(reward);
            setNotification(reward);
        }
    }

    public void separateList(List<Reward> rewardList){
        //Make sure list is ordered by date
        mLockedRewards.clear();
        for (Reward reward : rewardList) {
            assignRewardToList(reward);
        }
        updateUI();
    }

    private void onRewardDbUpdated(List<Reward> list) {
        separateList(list);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locked, container, false);
        RecyclerView lockedRecyclerView = view.findViewById(R.id.lockedRecyclerView);
        lockedRecyclerView.setAdapter(mLockedAdapter);
        lockedRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        emptyTextView = view.findViewById(R.id.emptyLockedTextView);
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        deleteWarning = new AlertDialog.Builder(context)
                .setTitle(R.string.delete_confirmation).setCancelable(true).setIcon(R.drawable.ic_warning)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });

        mLockedAdapter = new LockedAdapter(mLockedRewards, new LockedViewHolder.LockedClickListener() {
            @Override
            public void rewardOnClick(int i) {
                ClickedRewardDialog dialog = new LockedClickedReward(context, mLockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener() {
                    @Override
                    public void onDelete(final int position) {
                        deleteWarning.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new RewardAsyncTask(getContext(), RewardAsyncTask.TASK_DELETE_REWARDS, listener).execute(mLockedRewards.get(position));
                            }
                        }).show();
                    }
                },editListener);
                dialog.show();
            }
        });

        jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        componentName = new ComponentName(context, NotificationService.class);

        new RewardAsyncTask(context, RewardAsyncTask.TASK_GET_ALL_REWARDS, listener).execute();
    }
}
