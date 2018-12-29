package com.example.kyle.patiencetraining.main;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.kyle.patiencetraining.R;

import androidx.annotation.NonNull;

class PictureLocationDialog extends Dialog {
    private Button cameraButton;
    private Button galleryButton;
    PictureLocationDialog(@NonNull final Context context) {
        super(context);
        setContentView(R.layout.dialog_picture_location);

        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
    }

    void setCameraButtonListener(View.OnClickListener listener){
        cameraButton.setOnClickListener(listener);
    }
    void setGalleryButtonListener(View.OnClickListener listener){
        galleryButton.setOnClickListener(listener);
    }
}
