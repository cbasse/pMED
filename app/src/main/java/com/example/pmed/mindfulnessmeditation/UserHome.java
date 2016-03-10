package com.example.pmed.mindfulnessmeditation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class UserHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        String username = getIntent().getStringExtra("Username");

        TextView tv = (TextView) findViewById(R.id.TVuser_name);
        tv.setText(username);
    }
}
