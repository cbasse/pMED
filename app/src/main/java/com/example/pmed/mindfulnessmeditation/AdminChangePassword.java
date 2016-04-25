package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harri on 4/22/2016.
 */
public class AdminChangePassword  extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    String url = "http://meagherlab.co/update_admin_password.php";
    String TAG_SUCCESS = "success";
    String newPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("admin-pass", "TEST 1!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_password_layout);
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
            Intent i = new Intent(AdminChangePassword.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickButton(View v)
    {
        if(v.getId() == R.id.button_change_admin_password)
        {
            String pass1 = ((EditText)findViewById(R.id.adminPass1)).getText().toString();
            String pass2 = ((EditText)findViewById(R.id.adminPass2)).getText().toString();

            if(!pass1.equals(pass2))
            {
                Toast pass = Toast.makeText(AdminChangePassword.this, "Passwords don't match", Toast.LENGTH_SHORT);
                pass.show();
            }
            else {
                newPass = pass1;

                new UpdateAdminPassword().execute();
            }
        }

    }

    class UpdateAdminPassword extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("admin_password", newPass));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(AdminChangePassword.this, AdminHome.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
