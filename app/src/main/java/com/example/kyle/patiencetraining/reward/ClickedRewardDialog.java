package com.example.kyle.patiencetraining.reward;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.util.UrlApiService;
import com.example.kyle.patiencetraining.util.UrlImage;
import com.example.kyle.patiencetraining.util.UrlInfo;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public abstract class ClickedRewardDialog extends Dialog {

    private ImageView imageView;

    public ClickedRewardDialog(@NonNull final Context context, final Reward reward, final int position, final OnDeleteListener deleteListener){
        super(context);
        setContentView(R.layout.dialog_clicked_reward);

        imageView = findViewById(R.id.clickedImageView);
        setImage(reward, context);
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteListener.onDelete(position);
                dismiss();
            }
        });
    }

    private void setImage(Reward reward, Context context){
        imageView.setImageURI(null);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Boolean dataSaved = sharedPreferences.getBoolean(context.getString(R.string.data_saver_key),false);
        if(!dataSaved && reward.getImagePath().isEmpty() && !reward.getImageLink().isEmpty()){
            Glide.with(context).load(reward.getImageLink()).into(imageView);
        }else if(!reward.getImagePath().isEmpty()){
            imageView.setImageURI(Uri.parse(reward.getImagePath()));
        }
    }

    public interface OnDeleteListener{
        void onDelete(final int position);
    }
}
