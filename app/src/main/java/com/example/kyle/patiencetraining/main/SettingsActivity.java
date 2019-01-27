package com.example.kyle.patiencetraining.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    /** Todo: make settings menu get all options needed
     *       - pre-emptive notification
     *       - feedback/suggestions
     *       **/


    private SharedPreferences sharedPref;
    private SwitchMaterial dataSaver;
    private SwitchMaterial delete;
    private EditText name;
    private TextView notificationTone;
    private static final int NOTIFICATION_REQUEST_CODE = 5;
    private Uri notificationUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        boolean dataSaved = sharedPref.getBoolean(getString(R.string.data_saver_key),false);
        dataSaver = findViewById(R.id.dataSaverSwitch);
        dataSaver.setChecked(dataSaved);
        boolean deleteConfirm = sharedPref.getBoolean(getString(R.string.delete_key),true);
        delete = findViewById(R.id.deleteSwitch);
        delete.setChecked(deleteConfirm);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String username = "";
        String displayName = sharedPref.getString(getString(R.string.name_key), username);
        if(fUser != null && displayName.isEmpty())
            displayName = fUser.getDisplayName();
        name = findViewById(R.id.nameEditText);
        name.setText(displayName);
        String notificationName = sharedPref.getString(getString(R.string.notification_title_key),"");
        notificationUri = Uri.parse(sharedPref.getString(getString(R.string.notification_uri_key),""));
        notificationTone = findViewById(R.id.notificationLabel);
        notificationTone.setText(notificationName);
        Button notifButton = findViewById(R.id.notificationSound);
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNotificationIntent();
            }
        });
    }

    private void launchNotificationIntent(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, notificationUri);
        this.startActivityForResult(intent, NOTIFICATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == NOTIFICATION_REQUEST_CODE) {
            notificationUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (notificationUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, notificationUri);
                String notificationTitle = ringtone.getTitle(this);
                notificationTone.setText(notificationTitle);
            }else
                notificationTone.setText("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.data_saver_key), dataSaver.isChecked());
        editor.putBoolean(getString(R.string.delete_key), delete.isChecked());
        editor.putString(getString(R.string.name_key), name.getText().toString());
        if(notificationUri != null)
            editor.putString(getString(R.string.notification_uri_key), notificationUri.toString());
        else
            editor.putString(getString(R.string.notification_uri_key), "");
        editor.putString(getString(R.string.notification_title_key), notificationTone.getText().toString());
        editor.apply();
    }
}
