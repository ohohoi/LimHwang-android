package com.example.a2020limhwang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MypageActivity extends AppCompatActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        back = findViewById(R.id.back);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }
}
