package com.example.kyle.patiencetraining;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Todo: make longpress edit the reward (dialog shows up to ask if you wish to edit or remove)
     * Todo: update adapter to show hours,days,weeks?
     * Todo: make press launch dialog to show reward info (including edit and delete options)
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
    public static final String REWARD_EXTRA = "PatienceTrainingReward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,ModifyRewardActivity.class), ADD_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ADD_REQUEST){
            if(resultCode == RESULT_OK){
                assignRewardToList((Reward)data.getParcelableExtra(REWARD_EXTRA));
                updateUI();
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

    public void assignRewardToList(Reward reward){
        Date now = new Date();
        if(now.after(reward.getFinish()))
            mUnlockedRewards.add(reward);
        else
            mLockedRewards.add(reward);
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
