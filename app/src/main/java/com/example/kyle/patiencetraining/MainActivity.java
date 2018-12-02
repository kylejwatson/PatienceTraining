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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Todo: make longpress (dialog shows up to ask if you wish to edit or remove)
     * Todo: update adapter to show hours,days,weeks?
     * Todo: make press launch dialog (including edit and delete options)
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
    private static final int MOD_REQUEST = 1;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";
    public static final String REWARD_POSITION_EXTRA = "PatienceTrainingRewardPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DAY_OF_YEAR, -20);
        Date startTime = startCalendar.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_YEAR, -3);
        Date endTime = endCalendar.getTime();
        assignRewardToList(new Reward("test",200,startTime, endTime,"lll",null,true));
        updateUI();
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
        }else if(requestCode == MOD_REQUEST){
            if(resultCode == RESULT_OK){
                int position = data.getIntExtra(REWARD_POSITION_EXTRA, 0);
                Reward reward = data.getParcelableExtra(REWARD_EXTRA);
                mLockedRewards.set(position, reward);
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
                    ClickedRewardDialog dialog = new ClickedRewardDialog(MainActivity.this, mLockedRewards.get(i));
                    dialog.show();
                }
            }, new LockedViewHolder.LongClickListener() {
                @Override
                public boolean rewardOnLongClick(int i) {
                    Intent intent = new Intent(MainActivity.this,ModifyRewardActivity.class);
                    intent.putExtra(REWARD_EXTRA, mLockedRewards.get(i));
                    intent.putExtra(REWARD_POSITION_EXTRA, i);
                    startActivityForResult(intent, MOD_REQUEST);
                    return false;
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
                    ClickedRewardDialog dialog = new ClickedRewardDialog(MainActivity.this, mUnlockedRewards.get(i));
                    dialog.show();
                }
            });
            RecyclerView unlockedRecyclerView = findViewById(R.id.unlockedRecyclerView);
            unlockedRecyclerView.setAdapter(mUnlockedAdapter);
            unlockedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mUnlockedAdapter.notifyDataSetChanged();
    }

}
