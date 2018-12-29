package com.example.kyle.patiencetraining.leaderboard;

import android.view.View;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class LeaderboardViewHolder extends RecyclerView.ViewHolder {

    TextView userNameTextView;
    LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.userNameTextView);
    }
}
