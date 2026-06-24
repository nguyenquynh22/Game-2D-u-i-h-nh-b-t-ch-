package com.example.towerstack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.towerstack.Database.DatabaseHelper;
import com.example.towerstack.Model.UserModel;

public class MainActivity extends AppCompatActivity {
    Button btnStart, btnScore;
    ImageButton btnSettings;
    DatabaseHelper databaseHelper;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        databaseHelper= new DatabaseHelper(this);
        btnStart = findViewById(R.id.btnStart);
        btnScore = findViewById(R.id.btnScore);
        btnSettings = findViewById(R.id.ibtnSettings);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        UserModel userModel = databaseHelper.getUserInfo();
        if (userModel != null) {
            btnScore.setText(String.valueOf(userModel.getTotalCoin()));
        }
    }
}