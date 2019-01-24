package com.example.kyle.patiencetraining.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import com.example.kyle.patiencetraining.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    /** Todo: make settings menu get all options needed
     *       - Change name on firestore not just preferences
     *       - notification noise
     *       - pre-emptive notification
     *       - feedback/suggestions
     *       **/


    private SharedPreferences sharedPref;
    private SwitchMaterial dataSaver;
    private SwitchMaterial delete;
    private EditText name;

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
        String username = "username";
        if(fUser != null)
            username = fUser.getDisplayName();
        String displayName = sharedPref.getString(getString(R.string.name_key), username);
        name = findViewById(R.id.nameEditText);
        name.setText(displayName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.data_saver_key), dataSaver.isChecked());
        editor.putBoolean(getString(R.string.delete_key), delete.isChecked());
        editor.putString(getString(R.string.name_key), name.getText().toString());
        editor.apply();
    }
}
