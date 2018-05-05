package com.example.user.fingertips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText nameInput;
    Button makeDB;
    private SharedPreferences user_Name;
    private SharedPreferences.Editor editor_user;
    String dbName;

    private final String pattern = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-z0-9]*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameInput = (EditText)findViewById(R.id.editText);
        makeDB = (Button)findViewById(R.id.makeDB);

        makeDB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dbName = nameInput.getText().toString();
                boolean flag = Pattern.matches(pattern, dbName);
                user_Name = getSharedPreferences("already", Context.MODE_PRIVATE);
                if((dbName.length() != 0) && flag) {
                    editor_user = user_Name.edit();
                    editor_user.putString("name", dbName);
                    editor_user.apply();
                    Intent intent = new Intent(MainActivity.this, TouchWebView.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "한글, 영어, 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                    nameInput.setText(null);
                }
            }
        });
    }
}
