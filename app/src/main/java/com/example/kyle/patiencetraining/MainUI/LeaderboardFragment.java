package com.example.kyle.patiencetraining.MainUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

public class LeaderboardFragment extends Fragment {

    private Intent loginIntent;
    private TextView error;
    private SignInButton button;
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
        }else{
            error.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case LoginActivity.LOGIN_TASK:
                if(resultCode == Activity.RESULT_OK){
                    error.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }
                break;
            case LoginActivity.LOGOUT_TASK:
                if(resultCode == Activity.RESULT_OK){
                    error.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
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
