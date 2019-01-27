package com.example.kyle.patiencetraining.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.kyle.patiencetraining.R;
import com.example.kyle.patiencetraining.reward.Reward;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModifyRewardActivity extends AppCompatActivity {
    private Uri imageUri;
    private Uri photoURI;
    private static final int GET_FROM_GALLERY = 0;
    private static final int GET_FROM_CAMERA = 1;

    private int hours = 1;
    private int days;
    private int weeks;
    private int seconds;
    private TextView fileName;
    private ImageButton clearButton;
    private Reward oldReward;
    private PictureLocationDialog uploadDialog;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reward);

        oldReward = getIntent().getParcelableExtra(MainActivity.REWARD_EXTRA);
        
        uploadDialog = new PictureLocationDialog(this);
        uploadDialog.setCameraButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        uploadDialog.setGalleryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , GET_FROM_GALLERY);
            }
        });
        final Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDialog.show();
            }
        });

        fileName = findViewById(R.id.fileName);
        clearButton = findViewById(R.id.removeImageButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                fileName.setText("");
                clearButton.setVisibility(View.INVISIBLE);
            }
        });

        final Button durationButton = findViewById(R.id.durationPicker);
        durationButton.setText(getString(R.string.duration, hours, days, weeks));

        final DurationDialog dialog = new DurationDialog(this, getString(R.string.missing_input,getString(R.string.missing_duration)), new DurationDialog.OnDurationSetListener() {
            @Override
            public void onDurationSet(int hourSet, int daySet, int weekSet, int secondsSet) {
                hours = hourSet;
                days = daySet;
                weeks = weekSet;
                seconds = secondsSet;
                durationButton.setText(getString(R.string.duration, hours, days, weeks));
            }
        });

        durationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setHours(hours);
                dialog.setDays(days);
                dialog.setWeeks(weeks);
                dialog.show();
            }
        });

        final EditText name = findViewById(R.id.nameEditText);
        final EditText price = findViewById(R.id.priceEditText);

        final EditText link = findViewById(R.id.linkEditText);
        final SwitchMaterial notification = findViewById(R.id.notificationSwitch);

        FloatingActionButton fab = findViewById(R.id.modifyFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link.setText(buildLink(link.getText().toString()));
                if(name.getText().length() == 0)
                    name.setError(getString(R.string.missing_input,getString(R.string.missing_name)));
                else if(link.getText().length() > 0 && !Patterns.WEB_URL.matcher(link.getText().toString()).matches()) {
                    link.setError(getString(R.string.invalid_link));
                }else {
                    if(oldReward != null)
                        modReward(name.getText(), price.getText(), link.getText(), notification.isChecked());
                    else
                        createReward(name.getText(), price.getText(), link.getText(), notification.isChecked());
                }

            }
        });

        if(oldReward != null){
            durationButton.setVisibility(View.GONE);
            TextView durationLabel = findViewById(R.id.durationLabel);
            durationLabel.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.ic_save);
            name.setText(oldReward.getName());
            price.setText(String.format(Locale.getDefault(), "%.2f", oldReward.getPrice()));
            link.setText(oldReward.getLink());
            notification.setChecked(oldReward.isNotificationSet());
            if(!oldReward.getImagePath().isEmpty()) {
                imageUri = Uri.parse(oldReward.getImagePath());
                setImageUri(imageUri, getFileName(imageUri));
            }
            ActionBar ab = getSupportActionBar();
            if(ab != null)
                ab.setTitle(R.string.title_activity_modify_reward);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.kyle.patiencetraining",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, GET_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getName();
        return image;
    }
    
    public void modReward(Editable name,  Editable price, Editable link, boolean notification){
        float priceFloat = 0f;
        if(price.length() != 0)
            priceFloat = Float.parseFloat(price.toString());
        oldReward.setPrice(priceFloat);
        oldReward.setName(name.toString());
        oldReward.setLink(link.toString());
        oldReward.setNotificationSet(notification);

        String image = "";
        if(imageUri != null)
            image = imageUri.toString();

        oldReward.setImagePath(image);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.REWARD_EXTRA, oldReward);
        setResult(RESULT_OK,intent);
        finish();
    }

    public String buildLink(String link){
        if(!link.startsWith(getString(R.string.http)) && !link.startsWith(getString(R.string.https)) && !link.isEmpty()){
            link = getString(R.string.http_link, link);
        }
        return link;
    }

    public void createReward(Editable name, Editable price, Editable link, boolean notification){
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, seconds);
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        Date endDate = calendar.getTime();

        float priceFloat = 0f;
        if(price.length() != 0)
            priceFloat = Float.parseFloat(price.toString());

        String image = "";
        if(imageUri != null)
            image = imageUri.toString();

        Reward reward = new Reward(name.toString(),priceFloat,now.getTime(),endDate.getTime(), link.toString(),image,notification);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.REWARD_EXTRA, reward);
        setResult(RESULT_OK,intent);
        finish();
    }

    public String getFileName(Uri uri) {
        String content = "content";
        if (content.equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            }
        }
        String result = uri.getPath();
        if(result == null)
            return null;
        int cut = result.lastIndexOf('/');
        if (cut != -1)
            result = result.substring(cut + 1);

        return result;
    }

    public void setImageUri(Uri imageUri, String fileName){
        this.imageUri = imageUri;
        this.fileName.setText(fileName);
        clearButton.setVisibility(View.VISIBLE);
        uploadDialog.hide();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GET_FROM_GALLERY ){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                setImageUri(uri,getFileName(uri));
            }
        }else if(requestCode == GET_FROM_CAMERA){
            if(resultCode == RESULT_OK){
                setImageUri(photoURI,mCurrentPhotoPath);
            }
        }
    }
}
