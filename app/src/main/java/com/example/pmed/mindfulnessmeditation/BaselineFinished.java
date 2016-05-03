package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BaselineFinished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline_finished);
    }

    public void onClickButton(View v) {
        if (v.getId() == R.id.button_logout) {
            Intent i = new Intent(BaselineFinished.this, MainActivity.class);
            startActivity(i);
        }
    }
}
