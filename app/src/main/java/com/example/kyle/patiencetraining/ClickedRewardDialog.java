package com.example.kyle.patiencetraining;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.URI;

import androidx.annotation.NonNull;

public abstract class ClickedRewardDialog extends Dialog {

    public ClickedRewardDialog(@NonNull final Context context, final Reward reward, final int position, final OnDeleteListener deleteListener){
        super(context);
        setContentView(R.layout.dialog_clicked_reward);
        setImage(reward);
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteListener.onDelete(position);
                dismiss();
            }
        });
    }

    private void setImage(Reward reward){
        ImageView imageView = findViewById(R.id.clickedImageView);
        imageView.setImageURI(null);
        imageView.setImageURI(Uri.parse(reward.getImagePath()));
    }

    public interface OnDeleteListener{
        void onDelete(final int position);
    }
}
