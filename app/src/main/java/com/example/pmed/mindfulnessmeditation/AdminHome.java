package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
    }

    public void onClickButton(View v) {

        if(v.getId() == R.id.icon_participtants) {
            Intent i = new Intent(AdminHome.this, ManageUserAccounts.class);
            startActivity(i);
        }

        if(v.getId() == R.id.icon_update) {
            Intent i = new Intent(AdminHome.this, UpdateExperiment.class);
            startActivity(i);
        }

        if(v.getId() == R.id.icon_logout) {
            Intent i = new Intent(AdminHome.this, MainActivity.class);
            startActivity(i);
        }

    }

}
