package com.example.towerstack;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.example.towerstack.Database.DatabaseHelper;
import com.example.towerstack.Model.UserModel;

public class SettingActivity extends AppCompatActivity {

    ImageButton btnBack;
    Switch swSound, swVibrate;
    DatabaseHelper databaseHelper;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.btnBack);
        swSound = findViewById(R.id.swSound);
        swVibrate = findViewById(R.id.swVibrate);

        databaseHelper = new DatabaseHelper(this);
        userModel = databaseHelper.getUserInfo();

        if (userModel != null) {
            swSound.setChecked(userModel.getIsSound() == 1);
            swVibrate.setChecked(userModel.getIsVibrate() == 1);
        }

        btnBack.setOnClickListener(v -> finish());

        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int sound = isChecked ? 1 : 0;
            int vibrate = swVibrate.isChecked() ? 1 : 0;

            databaseHelper.updateSettings(sound, vibrate);
        });

        swVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int sound = swSound.isChecked() ? 1 : 0;
            int vibrate = isChecked ? 1 : 0;

            databaseHelper.updateSettings(sound, vibrate);
        });
    }
}