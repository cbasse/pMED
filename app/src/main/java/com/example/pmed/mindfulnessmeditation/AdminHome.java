package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    public void onClickButton(View v) {
        if(v.getId() == R.id.button_user_accounts) {
            Intent i = new Intent(AdminHome.this, ManageUserAccounts.class);
            startActivity(i);
        }

        if(v.getId() == R.id.button_update_experiment) {
            Intent i = new Intent(AdminHome.this, UpdateExperiment.class);
            startActivity(i);
        }
    }

}
