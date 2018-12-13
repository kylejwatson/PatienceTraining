package com.example.kyle.patiencetraining.Leaderboard;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.Util.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    /**
     * Todo: convert the long time into a timestring
     */

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
        holder.userNameTextView.setText(context.getString(R.string.user_score,user.rank,user.userName,user.totalTime));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
