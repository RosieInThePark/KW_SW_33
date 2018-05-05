package com.example.user.fingertips;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends Activity {
    private SharedPreferences user_Name;
    private SharedPreferences.Editor editor_user;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_Name = getSharedPreferences("already", Context.MODE_PRIVATE);
        name = user_Name.getString("name", "None");

        if(name.equals("None")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(this, TouchWebView.class));
            finish();
        }
    }
}
