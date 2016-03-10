package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.button_user) {

            Intent i = new Intent(MainActivity.this, UserLogin.class);
            startActivity(i);
        }
        if(v.getId() == R.id.button_admin) {

            Intent i = new Intent(MainActivity.this, AdminLogin.class);
            startActivity(i);
        }
    }

}
