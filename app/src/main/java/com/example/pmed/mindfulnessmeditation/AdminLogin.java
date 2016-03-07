package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AdminLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.button_admin_login) {

            EditText a = (EditText)findViewById(R.id.TFadmin_name);
            String str = a.getText().toString();

            Intent i = new Intent(AdminLogin.this, AdminHome.class);
            i.putExtra("AdminUsername",str);
            startActivity(i);
        }
    }

}
