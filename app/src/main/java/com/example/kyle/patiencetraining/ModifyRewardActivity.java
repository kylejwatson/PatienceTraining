package com.example.kyle.patiencetraining;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Date;

public class ModifyRewardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reward);
        Toolbar toolbar = findViewById(R.id.modifyToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.modifyFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**TODO
                 * -Need to do checks on fields
                 * -Create proper upload image button
                 * -Create proper way to put duration in (side by side number text boxes?)
                 * -Parse date properly
                 * -Make reward a parceble
                 */

                EditText name = findViewById(R.id.nameEditText);
                EditText price = findViewById(R.id.priceEditText);
                EditText duration = findViewById(R.id.durationEditText);
                //Image image = uploadButton.onclicklistener.uploaded image?
                EditText link = findViewById(R.id.linkEditText);
                Switch notification = findViewById(R.id.notificationSwitch);
                Reward reward = new Reward(name.getText().toString(),Float.parseFloat(price.getText().toString())
                        ,new Date(),new Date(),link.getText().toString(),"",notification.isChecked());
                Intent intent = new Intent();
                intent.putExtra(MainActivity.REWARD_EXTRA, reward);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

}
