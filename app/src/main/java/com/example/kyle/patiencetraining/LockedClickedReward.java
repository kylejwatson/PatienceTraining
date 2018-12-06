package com.example.kyle.patiencetraining;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import androidx.annotation.NonNull;

public class LockedClickedReward extends ClickedRewardDialog {

    public LockedClickedReward(@NonNull Context context, Reward reward, final int position, OnDeleteListener deleteListener, final OnEditListener editListener) {
        super(context, reward, position, deleteListener);

        TextView textView = findViewById(R.id.descriptionTextView);
        Button button = findViewById(R.id.collectButton);
        Date start = new Date(reward.getStart());
        Date finish = new Date(reward.getStart());
        String totalTime = TimeString.getTimeString(start, finish, context);
        String time = TimeString.getTimeString(start, new Date(), context);
        String description = context.getString(R.string.waited_locked, time, totalTime, reward.getName());
        button.setText(R.string.wait_button);
        button.setEnabled(false);
        textView.setText(description);
        Button editButton = findViewById(R.id.editButton);
        editButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editListener.onEdit(position);
                dismiss();
            }
        });
    }

    public interface OnEditListener{
        void onEdit(int position);
    }
}
