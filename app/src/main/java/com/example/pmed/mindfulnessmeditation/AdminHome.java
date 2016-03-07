package com.example.pmed.mindfulnessmeditation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        String username = getIntent().getStringExtra("AdminUsername");

        TextView tv = (TextView)findViewById(R.id.TVadmin_name);
        tv.setText(username);
    }
}
