package com.example.kyle.patiencetraining;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;

public class UnlockedClickedReward extends  ClickedRewardDialog{

    public UnlockedClickedReward(@NonNull final Context context, final Reward reward, final int position, final OnDeleteListener deleteListener){
        super(context, reward, position, deleteListener);
        TextView textView = findViewById(R.id.descriptionTextView);
        Button button = findViewById(R.id.collectButton);
        String time = TimeString.getTimeString(reward.getStart(), reward.getFinish(),context);
        String description = context.getString(R.string.waited_unlocked, time, reward.getName(), reward.getPrice());
        textView.setText(description);
        button.setText(R.string.collect_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reward.getLink().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reward.getLink()));
                    context.startActivity(browserIntent);
                }
                dismiss();
                deleteListener.onDelete(position);
            }
        });

    }
}
