package com.example.kyle.patiencetraining.MainUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.kyle.patiencetraining.Reward.LockedReward.LockedFragment;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Reward.Reward;
import com.example.kyle.patiencetraining.Reward.RewardAsyncTask;
import com.example.kyle.patiencetraining.Util.NotificationService;
import com.example.kyle.patiencetraining.Util.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    /**
     * Todo: make rank server-side functions
     *
     * Todo: update adapter to show hours,days,weeks?
     * Todo: show 'no rewards added' page if empty
     * Todo: pass action bar arguments to fragments
     *
     * Todo: make settings menu get all options needed
     * Todo: Make activities/layouts for each option
     * Todo: Start theming/dimens sorting
     */

    private static final int ADD_REQUEST = 0;
    public static final String REWARD_EXTRA = "PatienceTrainingReward";
    private static final int[] ACTIONBAR_TITLES = {R.string.locked_title,R.string.unlocked_title, R.string.leaderboard_title};
    private User user;


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Menu menu;
    private RewardAsyncTask.OnPostExecuteListener listener = new RewardAsyncTask.OnPostExecuteListener() {
        @Override
        public void onPostExecute(List<Reward> list) {
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        long rewardId = intent.getLongExtra(NotificationService.REWARD_ID_EXTRA,-1);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3, rewardId);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ModifyRewardActivity.class), ADD_REQUEST);
            }
        });
    }

    private void firestoreDB(final String uID){
        // Access a Cloud Firestore instance from your Activity

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uID).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            user = task.getResult().toObject(User.class);
                            if(user != null) {
                                user.totalTime += 22;
                                db.collection("users").document(uID)
                                        .set(user);
                            }
                        }
                    }
                }

        );
        db.collection("users")
                .orderBy("totalTime")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User tempUser = document.toObject(User.class);
                                if(document.getId().equals(uID))
                                    user = document.toObject(User.class);
                            }
                        } else {
                            //UpdateUI "Error getting documents"
                            user = new User();
                        }
                    }
                });

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
                    new RewardAsyncTask(this,RewardAsyncTask.TASK_INSERT_REWARDS,listener).execute((Reward)data.getParcelableExtra(REWARD_EXTRA));
                break;
            case LockedFragment.MOD_REQUEST:
                if(resultCode == Activity.RESULT_OK)
                    new RewardAsyncTask(this,RewardAsyncTask.TASK_UPDATE_REWARDS,listener).execute((Reward)data.getParcelableExtra(MainActivity.REWARD_EXTRA));
                break;
            case LoginActivity.LOGIN_TASK:
                if(resultCode == Activity.RESULT_OK){
                    String uID = data.getStringExtra(LoginActivity.UID_EXTRA);
                    firestoreDB(uID);
                    menu.findItem(R.id.action_signout).setVisible(true);
                    menu.findItem(R.id.action_signin).setVisible(false);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.mainLayout), R.string.success, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.signout, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signOut();
                        }
                    });
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
                    snackbar.setAction(R.string.common_signin_button_text, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signIn();
                        }
                    });
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
