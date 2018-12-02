package com.example.kyle.patiencetraining;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

public class ClickedRewardDialog extends Dialog {
    /**
     * Todo: send to URL when button is clicked
     * Todo: once user comes back ask if the reward should be removed from list
     * Todo: edit/delete button
     */
    public ClickedRewardDialog(@NonNull Context context, Reward reward) {
        super(context);
        setContentView(R.layout.dialog_clicked_reward);
        ImageView imageView = findViewById(R.id.clickedImageView);
        imageView.setImageURI(null);
        imageView.setImageURI(reward.getImagePath());

        Date now = new Date();

        TextView textView = findViewById(R.id.descriptionTextView);
        Button button = findViewById(R.id.collectButton);
        if(!now.before(reward.getFinish())){
            String time = TimeString.getTimeString(reward.getStart(), reward.getFinish(),context);
            String description = context.getString(R.string.waited_unlocked, time, reward.getName(), reward.getPrice());
            button.setText(R.string.collect_button);
            textView.setText(description);
        }else{
            String totalTime = TimeString.getTimeString(reward.getStart(), reward.getFinish(), context);
            String time = TimeString.getTimeString(reward.getStart(), now, context);
            String description = context.getString(R.string.waited_locked, time, totalTime, reward.getName());
            button.setText(R.string.wait_button);
            button.setEnabled(false);
            textView.setText(description);
        }
    }
}
