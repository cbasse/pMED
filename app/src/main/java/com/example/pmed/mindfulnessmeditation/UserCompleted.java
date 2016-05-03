package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserCompleted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_completed);
    }


    public void onClickButton(View v) {
        if (v.getId() == R.id.button_logout) {
            Intent i = new Intent(UserCompleted.this, MainActivity.class);
            startActivity(i);
        }
    }
}
