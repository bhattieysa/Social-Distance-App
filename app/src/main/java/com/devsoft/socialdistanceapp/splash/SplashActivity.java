package com.devsoft.socialdistanceapp.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.devsoft.socialdistanceapp.MapsActivity;
import com.devsoft.socialdistanceapp.R;
import com.devsoft.socialdistanceapp.helper.SessionManager;
import com.devsoft.socialdistanceapp.loginsignup.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    SessionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        manager  = new SessionManager(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (manager.isLogIn()){
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                }else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                finish();
            }
        },3000);
    }
}