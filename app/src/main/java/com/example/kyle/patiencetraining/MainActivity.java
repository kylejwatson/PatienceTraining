package com.example.kyle.patiencetraining;

import android.app.Activity;
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
import androidx.viewpager.widget.ViewPager;

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
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity{

    /**
     * Todo: make leaderboard fragment
     *
     * Todo: update adapter to show hours,days,weeks?
     * Todo: show 'no rewards added' page if empty
     *
     * Todo: make settings menu get all options needed
     * Todo: Make activities/layouts for each option
     * Todo: Start theming/dimens sorting
     */

    private static final int ADD_REQUEST = 0;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";
    public final static int TASK_GET_ALL_REWARDS = 0;
    public final static int TASK_DELETE_REWARDS = 1;
    public final static int TASK_INSERT_REWARDS = 3;
    public final static int TASK_UPDATE_REWARDS = 2;

    private static AppDatabase sDatabase;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sDatabase = AppDatabase.getInstance(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 2);
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Intent intent = getIntent();
        long rewardId = intent.getLongExtra(NotificationService.REWARD_ID_EXTRA,-1);
        if(rewardId != -1)
            mViewPager.setCurrentItem(1);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ModifyRewardActivity.class), ADD_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Log.d("Request", ""+requestCode);
        if(requestCode == ADD_REQUEST){
            if(resultCode == RESULT_OK){
                new RewardAsyncTask(TASK_INSERT_REWARDS).execute((Reward)data.getParcelableExtra(REWARD_EXTRA));
            }
        }
        if(requestCode == LockedFragment.MOD_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                new RewardAsyncTask(TASK_UPDATE_REWARDS).execute((Reward)data.getParcelableExtra(MainActivity.REWARD_EXTRA));
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
                case TASK_DELETE_REWARDS:
                    sDatabase.rewardDao().deleteRewards(rewards[0]);
                    break;
                case TASK_UPDATE_REWARDS:
                    sDatabase.rewardDao().updateRewards(rewards[0]);
                    break;
            }
            return sDatabase.rewardDao().getAllRewards();
        }

        @Override
        protected void onPostExecute(List<Reward> list) {
            super.onPostExecute(list);
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

}
