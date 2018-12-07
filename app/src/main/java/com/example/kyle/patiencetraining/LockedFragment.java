package com.example.kyle.patiencetraining;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class LockedFragment extends Fragment implements RewardFragment {


    public final static int TASK_GET_ALL_REWARDS = 0;
    public final static int TASK_DELETE_REWARDS = 1;

    public static final String REWARD_NAME_BUNDLE = "RewardName";
    public static final String REWARD_ID_BUNDLE = "RewardID";

    public static final int MOD_REQUEST = 1;
    private static AppDatabase sDatabase;
    private AlertDialog.Builder deleteWarning;
    private LockedAdapter mLockedAdapter;
    private List<Reward> mLockedRewards = new ArrayList<>();
    private View view;


    private LockedClickedReward.OnEditListener editListener = new LockedClickedReward.OnEditListener() {
        @Override
        public void onEdit(int position) {
            editReward(position);
        }
    };

    public LockedFragment() {
        // Required empty public constructor
    }

    public void editReward(int position){
        Intent intent = new Intent(getContext(),ModifyRewardActivity.class);
        intent.putExtra(MainActivity.REWARD_EXTRA, mLockedRewards.get(position));
        Log.d("RequestFragment", ""+MOD_REQUEST);
        getActivity().startActivityForResult(intent, MOD_REQUEST);
    }

    public void updateUI(){
        if(mLockedAdapter == null) {
            mLockedAdapter = new LockedAdapter(getContext(), mLockedRewards, new LockedViewHolder.LockedClickListener() {
                @Override
                public void rewardOnClick(int i) {
                    ClickedRewardDialog dialog = new LockedClickedReward(getContext(), mLockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener() {
                        @Override
                        public void onDelete(final int position) {
                            deleteWarning.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new RewardAsyncTask(TASK_DELETE_REWARDS).execute(mLockedRewards.get(position));
                                }
                            }).show();
                        }
                    },editListener);
                    dialog.show();
                }
            });
            RecyclerView lockedRecyclerView = view.findViewById(R.id.lockedRecyclerView);
            lockedRecyclerView.setAdapter(mLockedAdapter);
            lockedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        mLockedAdapter.notifyDataSetChanged();
    }



    public void setNotification(Reward reward){
        Log.d("Main Notif", "Set");
        JobScheduler jobScheduler =
                (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        if(reward.isNotificationSet()) {
            Log.d("Main Notif", "true");
            Date now = new Date();
            long millis = reward.getFinish() - now.getTime();
            Log.d("Main Notif",""+millis);
            Log.d("Main Notif", "ID: " + reward.getNotificationJobId());
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(REWARD_NAME_BUNDLE, reward.getName());
            bundle.putLong(REWARD_ID_BUNDLE, reward.getId());
            jobScheduler.schedule(new JobInfo.Builder(reward.getNotificationJobId(),
                    new ComponentName(getContext(), NotificationService.class))
                    .setMinimumLatency(millis)
                    .setExtras(bundle)
                    .build());
        }else{
            jobScheduler.cancel(reward.getNotificationJobId());
        }
    }

    public void assignRewardToList(Reward reward){
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_locked, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sDatabase = AppDatabase.getInstance(context);

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
