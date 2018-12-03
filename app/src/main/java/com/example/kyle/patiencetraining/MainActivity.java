package com.example.kyle.patiencetraining;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
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
     * Todo: update adapter to show hours,days,weeks?
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
    public static final String REWARD_POSITION_EXTRA = "PatienceTrainingRewardPosition";
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

        //Example 'unlocked' reward
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DAY_OF_YEAR, -20);
        Date startTime = startCalendar.getTime();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_YEAR, -3);
        Date endTime = endCalendar.getTime();
        assignRewardToList(new Reward("test",200,startTime, endTime,"https://www.google.com",null,true));
        updateUI();
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

    public void editReward(int position){
        Intent intent = new Intent(MainActivity.this,ModifyRewardActivity.class);
        intent.putExtra(REWARD_EXTRA, mLockedRewards.get(position));
        intent.putExtra(REWARD_POSITION_EXTRA, position);
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
                                            mLockedRewards.remove(position);
                                            updateUI();
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
                    ClickedRewardDialog dialog = new UnlockedClickedReward(MainActivity.this, mUnlockedRewards.get(i), i, new ClickedRewardDialog.OnDeleteListener(){
                        @Override
                        public void onDelete(final int position) {
                            deleteWarning.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mUnlockedRewards.remove(position);
                                            updateUI();
                                        }
                                    }).show();
                        }
                    });
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
