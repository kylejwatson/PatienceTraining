package com.example.kyle.patiencetraining.Leaderboard;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Util.TimeString;
import com.example.kyle.patiencetraining.Util.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

    private List<User> mUsers;
    private Context context;
    LeaderboardAdapter(List<User> mUsers){
        this.mUsers = mUsers;
    }
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leader_grid_cell, parent, false);

        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = mUsers.get(position);
        String timeString = TimeString.getTimeFromLong(user.totalTime, context);
        holder.userNameTextView.setText(context.getString(R.string.user_score,user.rank,user.userName,timeString));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
