package com.example.kyle.patiencetraining.reward.unlocked;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.reward.Reward;
import com.example.kyle.patiencetraining.util.TimeString;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class UnlockedAdapter extends RecyclerView.Adapter<UnlockedViewHolder> {
    private List<Reward> mRewards;
    private UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener;
    private int clickOnBuild = -1;
    private Context context;

    UnlockedAdapter(List<Reward> mRewards, UnlockedViewHolder.UnlockedClickListener mUnlockedClickListener) {
        this.mRewards = mRewards;
        this.mUnlockedClickListener = mUnlockedClickListener;
    }

    void setClickOnBuild(int clickOnBuild){
        this.clickOnBuild = clickOnBuild;
    }

    @NonNull
    @Override
    public UnlockedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.unlocked_grid_cell, viewGroup, false);

        return new UnlockedViewHolder(view, mUnlockedClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UnlockedViewHolder unlockedViewHolder, int i) {
        Reward reward = mRewards.get(i);
        unlockedViewHolder.nameTextView.setText(reward.getName());
        String totalTime = TimeString.getTimeStringBetween(new Date(reward.getStart()), new Date(reward.getFinish()),context);
        unlockedViewHolder.timeTextView.setText(totalTime);
        Date endDate = new Date(reward.getFinish());
        String timeString = DateFormat.getDateInstance(DateFormat.SHORT).format(endDate);
        unlockedViewHolder.dateTextView.setText(timeString);
        setImage(reward,unlockedViewHolder.iconImageView);

        if(clickOnBuild == i) {
            clickOnBuild = -1;
            unlockedViewHolder.onClick(unlockedViewHolder.itemView);
        }
    }


    private void setImage(Reward reward, ImageView imageView){
        imageView.setImageURI(null);
        Boolean dataSaved = true;
        if(context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            dataSaved = sharedPreferences.getBoolean(context.getString(R.string.data_saver_key), false);
        }
        if(reward.getImageLink() == null)
            reward.setImageLink("");
        if(reward.getImagePath() == null)
            reward.setImagePath("");
        if(reward.getImagePath().isEmpty() && reward.getImageLink().isEmpty() && !reward.getLink().isEmpty())
            reward.setLink(reward.getLink());
        if(!dataSaved && reward.getImagePath().isEmpty() && !reward.getImageLink().isEmpty()){
            Glide.with(context).load(reward.getImageLink()).into(imageView);
        }else if(!reward.getImagePath().isEmpty()){
            imageView.setImageURI(Uri.parse(reward.getImagePath()));
        }
    }

    @Override
    public int getItemCount() {
        return mRewards.size();
    }
}
