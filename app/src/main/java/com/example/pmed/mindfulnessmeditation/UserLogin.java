package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UserLogin extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.button_user_login) {

            EditText a = (EditText)findViewById(R.id.TFuser_name);
            String str = a.getText().toString();
            EditText b = (EditText)findViewById(R.id.TFuser_password);
            String pass = b.getText().toString();

            String password = helper.searchPass(str);

            if(pass.equals(password)) {
                //Intent i = new Intent(UserLogin.this, RecordPhysData.class);
                Intent i = new Intent(UserLogin.this, FormActivity.class);
                //i.putExtra("Username",str);
                startActivity(i);
            }
            else {
                //popup msg
                Toast temp = Toast.makeText(UserLogin.this, "Username and Password don't match", Toast.LENGTH_SHORT);
                temp.show();
            }
        }
    }
}
