package com.example.kyle.patiencetraining;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    /**
     * Todo: make main activity use fragments, one with main page one with leaderboard, could potentially split locked and unlocked into there own fragments
     *
     * Todo: update adapter to show hours,days,weeks?
     * Todo: show 'no rewards added' page if empty
     *
     * Todo: make settings menu get all options needed
     * Todo: Make activities/layouts for each option
     * Todo: Start theming/dimens sorting
     */

    private List<Reward> mLockedRewards = new ArrayList<>();
    private List<Reward> mUnlockedRewards = new ArrayList<>();
    private LockedAdapter mLockedAdapter;
    private UnlockedAdapter mUnlockedAdapter;
    private static final int ADD_REQUEST = 0;
    public static final int MOD_REQUEST = 1;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";
    public static final String REWARD_NAME_BUNDLE = "RewardName";
    public static final String REWARD_ID_BUNDLE = "RewardID";
    public final static int TASK_GET_ALL_REWARDS = 0;
    public final static int TASK_DELETE_REWARDS = 1;
    public final static int TASK_UPDATE_REWARDS = 2;
    public final static int TASK_INSERT_REWARDS = 3;
    private static AppDatabase sDatabase;
    private long rewardId;

    private LockedClickedReward.OnEditListener editListener = new LockedClickedReward.OnEditListener() {
        @Override
        public void onEdit(int position) {
            editReward(position);
        }
    };
    private AlertDialog.Builder deleteWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        rewardId = intent.getLongExtra(NotificationService.REWARD_ID_EXTRA,-1);
        Log.d("ID", "onCreate: " + rewardId);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,ModifyRewardActivity.class), ADD_REQUEST);
            }
        });
        deleteWarning = new AlertDialog.Builder(this)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ADD_REQUEST){
            if(resultCode == RESULT_OK){
                new RewardAsyncTask(TASK_INSERT_REWARDS).execute((Reward)data.getParcelableExtra(REWARD_EXTRA));
            }
        }else if(requestCode == MOD_REQUEST){
            if(resultCode == RESULT_OK){
                new RewardAsyncTask(TASK_UPDATE_REWARDS).execute((Reward)data.getParcelableExtra(REWARD_EXTRA));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void separateList(List<Reward> rewardList){
        //Make sure list is ordered by date
        mLockedRewards.clear();
        mUnlockedRewards.clear();
        for (Reward reward : rewardList) {
            assignRewardToList(reward);
        }
        updateUI();
    }

    public void setNotification(Reward reward){

        JobScheduler jobScheduler =
                Objects.requireNonNull((JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE));
        if(reward.isNotificationSet()) {
            Date now = new Date();
            long millis = reward.getFinish() - now.getTime();
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(REWARD_NAME_BUNDLE, reward.getName());
            bundle.putLong(REWARD_ID_BUNDLE, reward.getId());
            jobScheduler.schedule(new JobInfo.Builder(reward.getNotificationJobId(),
                    new ComponentName(this, NotificationService.class))
                    .setMinimumLatency(millis)
                    .setExtras(bundle)
                    .build());
        }else{
            jobScheduler.cancel(reward.getNotificationJobId());
        }
    }

    public void assignRewardToList(Reward reward){
        Date now = new Date();
        if(now.after(new Date(reward.getFinish()))) {
            mUnlockedRewards.add(reward);
            reward.setNotificationSet(false);
        }else {
            mLockedRewards.add(reward);
        }
        setNotification(reward);
    }

    public void editReward(int position){
        Intent intent = new Intent(MainActivity.this,ModifyRewardActivity.class);
        intent.putExtra(REWARD_EXTRA, mLockedRewards.get(position));
        startActivityForResult(intent, MOD_REQUEST);
    }

    public void updateUI(){
        if(mLockedAdapter == null) {
            mLockedAdapter = new LockedAdapter(this, mLockedRewards, new LockedViewHolder.LockedClickListener() {
                @Override
                public void rewardOnClick(int i) {
                    ClickedRewardDialog dialog = new LockedClickedReward(MainActivity.this, mLockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener() {
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
            RecyclerView lockedRecyclerView = findViewById(R.id.lockedRecyclerView);
            lockedRecyclerView.setAdapter(mLockedAdapter);
            lockedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mLockedAdapter.notifyDataSetChanged();
        if(mUnlockedAdapter == null) {
            mUnlockedAdapter = new UnlockedAdapter(mUnlockedRewards, new UnlockedViewHolder.UnlockedClickListener() {
                @Override
                public void rewardOnClick(int i) {
                    onUnlockedClicked(i);
                }
            });
            RecyclerView unlockedRecyclerView = findViewById(R.id.unlockedRecyclerView);
            unlockedRecyclerView.setAdapter(mUnlockedAdapter);
            unlockedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mUnlockedAdapter.notifyDataSetChanged();
        for(int i = 0; i < mUnlockedRewards.size(); i++){
            if(mUnlockedRewards.get(i).getId() == rewardId){
                onUnlockedClicked(i);
                rewardId = -1;
            }
        }
    }

    private void onUnlockedClicked(int i){
        ClickedRewardDialog dialog = new UnlockedClickedReward(MainActivity.this, mUnlockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener(){
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


    private void onRewardDbUpdated(List<Reward> list) {
        separateList(list);
    }

    public class RewardAsyncTask extends AsyncTask<Reward, Void, List<Reward>>{

        private int task;
        public RewardAsyncTask(int task){
            this.task = task;
        }
        @Override
        protected List<Reward> doInBackground(Reward... rewards) {
            switch (task){
                case TASK_INSERT_REWARDS:
                    sDatabase.rewardDao().insertRewards(rewards[0]);
                    break;
                case TASK_UPDATE_REWARDS:
                    sDatabase.rewardDao().updateRewards(rewards[0]);
                    break;
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
