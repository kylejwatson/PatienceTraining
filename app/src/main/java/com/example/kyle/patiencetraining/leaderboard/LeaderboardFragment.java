package com.example.kyle.patiencetraining.leaderboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kyle.patiencetraining.main.LoginActivity;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.util.Score;
import com.example.kyle.patiencetraining.util.ScoreAsyncTask;
import com.example.kyle.patiencetraining.util.TimeString;
import com.example.kyle.patiencetraining.util.User;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class LeaderboardFragment extends Fragment {

    /**
     * TODO: add load more buttons for top and bottom ranges
     */

    private Intent loginIntent;
    private TextView error;
    private SignInButton button;
    private LeaderboardAdapter adapter;
    private RecyclerView recyclerView;
    private TextView yourScore;
    private User user;
    private List<User> mUsers = new ArrayList<>();
    private ProgressBar progressBar;
    private final static int RANGE = 10;

    public LeaderboardFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        yourScore = view.findViewById(R.id.yourScore);
        recyclerView = view.findViewById(R.id.leaderRecyclerView);
        adapter = new LeaderboardAdapter(mUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.leaderProgressBar);

        error = view.findViewById(R.id.signInError);
        button = view.findViewById(R.id.sign_in_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(loginIntent, LoginActivity.LOGIN_TASK);
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            error.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            getCurrentUser(mAuth.getUid());
        }else{
            error.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getOtherUsers(long rank){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("rank", Query.Direction.ASCENDING)
                .whereGreaterThanOrEqualTo("rank",rank-RANGE)
                .whereLessThanOrEqualTo("rank", rank+RANGE)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots != null) {
                            List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                            mUsers.clear();
                            for (DocumentSnapshot document : snapshots) {
                                User tempUser = document.toObject(User.class);
                                assert tempUser != null;
                                if (tempUser.userName == null)
                                    tempUser.userName = document.getId();
                                mUsers.add(tempUser);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void createUser(final String uID){
        user = new User();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null)
            user.userName = fUser.getDisplayName();
        user.totalTime = 0;
        addScore(uID);
    }

    private void addScore(final String uID){
        new ScoreAsyncTask(getContext(), ScoreAsyncTask.TASK_GET_ALL_SCORES, new ScoreAsyncTask.OnPostExecuteListener() {
            @Override
            public void onPostExecute(final List<Score> list) {
                long millis = 0;
                for (Score score:list) {
                    millis += score.getTime();
                }
                user.totalTime += millis;
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(uID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Score[] scoreList = new Score[list.size()];
                            for (int i = 0; i < scoreList.length; i++) {
                                scoreList[i] = list.get(i);
                                scoreList[i].setUploaded(1);
                            }

                            new ScoreAsyncTask(getContext(), ScoreAsyncTask.TASK_UPDATE_SCORE).execute(scoreList);
                            yourScore.setVisibility(View.VISIBLE);
                            if(getContext() != null) {
                                String timeString = TimeString.getTimeFromLong(user.totalTime, getContext());
                                yourScore.setText(getString(R.string.user_score, user.rank, user.userName, timeString));
                            }
                            setCurrentUserListener(uID);
                            getOtherUsers(user.rank);
                        }
                    }
                });
            }
        }).execute();
    }

    private void setCurrentUserListener(final String uID){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot != null) {
                            user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                String timeString = TimeString.getTimeFromLong(user.totalTime, getContext());
                                yourScore.setText(getString(R.string.user_score, user.rank, user.userName, timeString));
                            }
                        }
                    }
                });
    }

    private void getCurrentUser(final String uID){
        progressBar.setVisibility(View.VISIBLE);
        // Access a Cloud Firestore instance from your Activity

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uID).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            user = task.getResult().toObject(User.class);
                            if(user != null) {
                                addScore(uID);
                            }else{
                                createUser(uID);
                            }
                        }else{
                            createUser(uID);
                        }
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case LoginActivity.LOGIN_TASK:
                if(resultCode == Activity.RESULT_OK){
                    error.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    String uID = data.getStringExtra(LoginActivity.UID_EXTRA);
                    getCurrentUser(uID);
                }
                break;
            case LoginActivity.LOGOUT_TASK:
                if(resultCode == Activity.RESULT_OK){
                    error.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    yourScore.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.putExtra(LoginActivity.LOGIN_TASK_EXTRA, LoginActivity.LOGIN_TASK);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
