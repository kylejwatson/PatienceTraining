package com.example.kyle.patiencetraining;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LockedAdapter extends RecyclerView.Adapter<LockedViewHolder> {
    private List<Reward> mRewards;
    private LockedViewHolder.LockedClickListener mLockedClickListener;
    private Context mContext;

    public LockedAdapter(Context context, List<Reward> mRewards, LockedViewHolder.LockedClickListener mLockedClickListener) {
        this.mRewards = mRewards;
        this.mLockedClickListener = mLockedClickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public LockedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.locked_grid_cell, viewGroup, false);

        return new LockedViewHolder(view,mLockedClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LockedViewHolder lockedViewHolder, int i) {
        Reward reward = mRewards.get(i);

        lockedViewHolder.nameTextView.setText(reward.getName());
        Date date = reward.getFinish();
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.setTime(date);
        long msDiff = testCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        testCalendar.setTimeInMillis(msDiff);
        int years = testCalendar.get(Calendar.YEAR) - 1970;
        int months = testCalendar.get(Calendar.MONTH);
        int weeks = testCalendar.get(Calendar.WEEK_OF_YEAR) - 1;
        int days = testCalendar.get(Calendar.DAY_OF_YEAR)-1;
        int hours = testCalendar.get(Calendar.HOUR);
        Log.d(reward.getName(), testCalendar.toString());
        float quantifier;
        int qualifier;
        if (years > 0) {
            qualifier = R.string.year;
            quantifier = years + (float) months / 12;
        } else if (months > 0) {
            qualifier = R.string.month;
            quantifier = months + (float) weeks / 4;
        } else if (weeks > 0) {
            qualifier = R.string.week;
            quantifier = weeks + (float) days / 7;
        } else{
            qualifier = R.string.day;
            quantifier = days + (float) hours / 24;
        }

        String qString = mContext.getString(qualifier);
        String time = mContext.getString(R.string.countdown,quantifier,qString);
        lockedViewHolder.timeTextView.setText(time);
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
