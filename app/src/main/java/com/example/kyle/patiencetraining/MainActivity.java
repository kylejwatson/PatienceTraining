package com.example.kyle.patiencetraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Reward> mLockedRewards;
    private List<Reward> mUnlockedRewards;
    private LockedAdapter mLockedAdapter;
    private UnlockedAdapter mUnlockedAdapter;
    private static final int ADD_REQUEST = 0;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar cal = Calendar.getInstance();
        List<Reward> testMixedList = new ArrayList<>();
        cal.add(Calendar.HOUR, 3);
        testMixedList.add(new Reward("Get a coffee", 2f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        cal.add(Calendar.DAY_OF_YEAR, 3);
        testMixedList.add(new Reward("Get some beer", 8f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        cal.add(Calendar.DAY_OF_YEAR, 20);
        testMixedList.add(new Reward("Get a new shirt", 10f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        cal.add(Calendar.DAY_OF_YEAR, 15);
        testMixedList.add(new Reward("Buy a new phone", 100f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        cal.add(Calendar.DAY_OF_YEAR, -50);
        testMixedList.add(new Reward("Go on holiday", 1000f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        testMixedList.add(new Reward("Have a slice of that lemon drizzle cake", 0f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        testMixedList.add(new Reward("Get my nails done", 20f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        testMixedList.add(new Reward("Get a cute pair of shoes", 60f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));
        cal.add(Calendar.DAY_OF_YEAR, 1000);
        testMixedList.add(new Reward("Buy a car", 10000f, cal.getTime(), cal.getTime(),
                "http://","res/image",true));

        separateList(testMixedList);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,ModifyRewardActivity.class), ADD_REQUEST);
            }
        });
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
        if(mLockedRewards == null)
            mLockedRewards = new ArrayList<>();
        if(mUnlockedRewards == null)
            mUnlockedRewards = new ArrayList<>();
        mLockedRewards.clear();
        mUnlockedRewards.clear();
        Date now = new Date();
        for (Reward reward : rewardList) {
            if(now.after(reward.getFinish()))
                mUnlockedRewards.add(reward);
            else
                mLockedRewards.add(reward);
        }
        updateUI();
    }

    public void updateUI(){
        if(mLockedAdapter == null) {
            mLockedAdapter = new LockedAdapter(this, mLockedRewards, new LockedViewHolder.LockedClickListener() {
                @Override
                public void rewardOnClick(int i) {
                    //Launch locked fragment
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
                    //launch unlocked fragment
                }
            });
            RecyclerView unlockedRecyclerView = findViewById(R.id.unlockedRecyclerView);
            unlockedRecyclerView.setAdapter(mUnlockedAdapter);
            unlockedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mUnlockedAdapter.notifyDataSetChanged();
    }

}
