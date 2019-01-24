package com.example.kyle.patiencetraining.reward;

import android.app.Dialog;
import android.content.Context;
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


    private void requestData(String url, final Context context){
        UrlApiService service = UrlApiService.retrofit.create(UrlApiService.class);

        Call<UrlInfo> call = service.getUrlInfo(url);

        call.enqueue(new Callback<UrlInfo>() {
            @Override
            public void onResponse(Call<UrlInfo> call, Response<UrlInfo> response) {
                UrlInfo info = response.body();
                if(info != null){
                    List<UrlImage> images = info.getUrlImages();
                    if(!images.isEmpty()){
                        Glide.with(context).load(images.get(0).getSrc()).into(imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<UrlInfo> call, Throwable t) {
                Log.d("error",t.toString());
            }
        });
    }

    private void setImage(Reward reward, Context context){
        imageView.setImageURI(null);
        if(reward.getImagePath().isEmpty() && !reward.getLink().isEmpty()){
            requestData(reward.getLink(), context);
        }else if(!reward.getImagePath().isEmpty()){
            imageView.setImageURI(Uri.parse(reward.getImagePath()));
        }
    }

    public interface OnDeleteListener{
        void onDelete(final int position);
    }
}
