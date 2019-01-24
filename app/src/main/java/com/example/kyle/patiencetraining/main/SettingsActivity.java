package com.example.kyle.patiencetraining.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;

import com.example.kyle.patiencetraining.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    /** Todo: make settings menu get all options needed
     *       - Data saver (dont get thumbnails of sites)
     *       - Confirmation before deleting
     *       - Display name
     *       - notification noise
     *       - pre-emptive notification
     *       - feedback/suggestions
     *       **/


    private SharedPreferences sharedPref;
    private SwitchMaterial dataSaver;
    private SwitchMaterial delete;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.data_saver_key), dataSaver.isChecked());
        editor.putBoolean(getString(R.string.delete_key), delete.isChecked());
        editor.apply();
    }
}
