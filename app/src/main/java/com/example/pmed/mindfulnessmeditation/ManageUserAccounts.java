package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ManageUserAccounts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_accounts);
    }

    public void onClickButton(View v) {
        if(v.getId() == R.id.button_add_user) {
            Intent i = new Intent(ManageUserAccounts.this, AddUser.class);
            startActivity(i);
        }
    }
}
