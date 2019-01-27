package com.example.kyle.patiencetraining.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.kyle.patiencetraining.reward.MainViewModel;
import com.example.kyle.patiencetraining.reward.RewardDao;
import com.example.kyle.patiencetraining.reward.locked.LockedFragment;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.reward.Reward;
import com.example.kyle.patiencetraining.util.AppDatabase;
import com.example.kyle.patiencetraining.util.NotificationService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{

    /**
     *
     * Todo: add observers to the 'get before reward' and 'get after reward' queries so the recyclerViews update without reloading
     * Todo: add timeline to each reward
     *

     * Todo: Make activities/layouts for each option
     * Todo: Start theming/dimens sorting
     */

    private static final int ADD_REQUEST = 0;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";
    private static final int[] ACTIONBAR_TITLES = {R.string.locked_title,R.string.unlocked_title, R.string.leaderboard_title};

    private Menu menu;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainViewModel = new MainViewModel(getApplicationContext());
        Handler handler = new Handler();
        int delay = 10000;
        handler.postDelayed(new Runnable(){
            public void run(){
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> sortRewards());
                handler.postDelayed(this, delay);
            }
        }, delay);

        Intent intent = getIntent();
        long rewardId = intent.getLongExtra(NotificationService.REWARD_ID_EXTRA,-1);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3, rewardId);
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ActionBar ab = getSupportActionBar();
                if(ab != null)
                    ab.setTitle(ACTIONBAR_TITLES[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(rewardId != -1)
            mViewPager.setCurrentItem(1);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, ModifyRewardActivity.class), ADD_REQUEST));
    }

    private void sortRewards(){
        AppDatabase database = AppDatabase.getInstance(this);
        RewardDao dao = database.rewardDao();
        List<Reward> rewards = dao.getRewardsToSort();
        for (Reward reward:rewards) {
            if(reward.getFinish() <= new Date().getTime()){
                reward.setFinished(true);
                mainViewModel.update(reward);
            }
        }
    }

    private void signOut(){
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.putExtra(LoginActivity.LOGIN_TASK_EXTRA, LoginActivity.LOGOUT_TASK);
        startActivityForResult(signInIntent, LoginActivity.LOGOUT_TASK);
    }

    private void signIn() {
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.putExtra(LoginActivity.LOGIN_TASK_EXTRA, LoginActivity.LOGIN_TASK);
        startActivityForResult(signInIntent, LoginActivity.LOGIN_TASK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        //Unmask fragment request code
        requestCode &= 0x0000ffff;
        for (Fragment frag:frags) {
            frag.onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode){
            case ADD_REQUEST:
                if(resultCode == RESULT_OK)
                    mainViewModel.insert(data.getParcelableExtra(REWARD_EXTRA));
                break;
            case LockedFragment.MOD_REQUEST:
                if(resultCode == Activity.RESULT_OK)
                    mainViewModel.update(data.getParcelableExtra(MainActivity.REWARD_EXTRA));
                break;
            case LoginActivity.LOGIN_TASK:
                if(resultCode == Activity.RESULT_OK){
                    menu.findItem(R.id.action_signout).setVisible(true);
                    menu.findItem(R.id.action_signin).setVisible(false);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.mainLayout), R.string.success, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.signout, view -> signOut());
                    snackbar.show();
                }else{
                    Snackbar.make(findViewById(R.id.mainLayout), R.string.goog_error, Snackbar.LENGTH_LONG).show();
                }
                break;
            case LoginActivity.LOGOUT_TASK:
                if(resultCode == Activity.RESULT_OK){
                    menu.findItem(R.id.action_signout).setVisible(false);
                    menu.findItem(R.id.action_signin).setVisible(true);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.mainLayout), R.string.success_signout, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.common_signin_button_text, view -> signIn());
                    snackbar.show();
                }else{
                    Snackbar.make(findViewById(R.id.mainLayout), R.string.serv_error, Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            menu.findItem(R.id.action_signout).setVisible(true);
            menu.findItem(R.id.action_signin).setVisible(false);
        }else{
            menu.findItem(R.id.action_signout).setVisible(false);
            menu.findItem(R.id.action_signin).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.action_signin:
                // Configure Google Sign In
                signIn();
                return true;
            case R.id.action_signout:
                signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
