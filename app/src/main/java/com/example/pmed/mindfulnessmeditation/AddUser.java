package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddUser extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
    }


    public void onClickButton(View v) {

        if(v.getId() == R.id.button_user_added) {
            EditText uname = (EditText)findViewById(R.id.TFuname);
            EditText pass1 = (EditText)findViewById(R.id.TFpass1);
            EditText pass2 = (EditText)findViewById(R.id.TFpass2);

            String uname_str = uname.getText().toString();
            String pass1_str = pass1.getText().toString();
            String pass2_str = pass2.getText().toString();

            if(!pass1_str.equals(pass2_str)) {
                //popup msg
                Toast pass = Toast.makeText(AddUser.this, "Passwords don't match", Toast.LENGTH_SHORT);
                pass.show();
            }
            else {
                //insert the details in database
                Subjects s = new Subjects();
                s.setUname(uname_str);
                s.setPass(pass1_str);

                helper.insertSubjects(s);

                //EditText a = (EditText)findViewById(R.id.TFuser_name);
                //String str = a.getText().toString();

                Intent i = new Intent(AddUser.this, ManageUserAccounts.class);
                //i.putExtra("Username", str);
                startActivity(i);
            }
        }
    }
}
