package com.example.androidapp.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bg_splash);
        int intentFragment = getIntent().getIntExtra("fragmentSelect", -1);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if (intentFragment != -1) {
                        intent.putExtra("fragmentSelect", intentFragment);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread.start();
    }
}