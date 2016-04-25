package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeUserPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_password);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent i = new Intent(ChangeUserPassword.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickButton(View v)
    {
        if(v.getId() == R.id.button_change_user_password)
        {
            String pass1 = ((EditText)findViewById(R.id.userPass1)).getText().toString();
            String pass2 = ((EditText)findViewById(R.id.userPass2)).getText().toString();

            if(!pass1.equals(pass2))
            {
                Toast pass = Toast.makeText(ChangeUserPassword.this, "Passwords don't match", Toast.LENGTH_SHORT);
                pass.show();
            }
            else {
                Intent i = new Intent(ChangeUserPassword.this, AdminHome.class);
                startActivity(i);
            }
        }

    }
}
