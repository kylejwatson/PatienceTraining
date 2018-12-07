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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
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
     * Todo: pass arguments to fragments
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
    private static final int RC_SIGN_IN = 3;
    private User user;
    private FirebaseAuth mAuth;
    private static AppDatabase sDatabase;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Menu menu;
    private GoogleSignInOptions gso;

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

    private void signIn() {
        GoogleSignInClient client = GoogleSignIn.getClient(this,gso);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("MainActivity", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firestoreDB(final String uID){
        // Access a Cloud Firestore instance from your Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(uID))
                                    user = document.toObject(User.class);
                                Log.d("Success", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("Unsuccess", "Error getting documents.", task.getException());
                        }

                        user.totalTime += 22;
                        db.collection("users").document(uID)
                                .set(user);
                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Log.d("MainActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            menu.findItem(R.id.action_signout).setVisible(true);
                            menu.findItem(R.id.action_signin).setVisible(false);
                            firestoreDB(user.getUid());
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity", "signInWithCredential:failure", task.getException());

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signOut(){
        menu.findItem(R.id.action_signout).setVisible(false);
        menu.findItem(R.id.action_signin).setVisible(true);
        if(mAuth != null)
            mAuth.signOut();
        mAuth = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
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
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                signIn();
                return true;
            case R.id.action_signout:
                signOut();
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
