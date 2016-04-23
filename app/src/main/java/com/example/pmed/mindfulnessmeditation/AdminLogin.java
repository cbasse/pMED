package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminLogin extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static final String url = "http://meagherlab.co/authenticate_admin.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_LOGIN_SUCCESS = "login_success";
    private static final String TAG_ADMIN = "admin";
    private static final String TAG_MESSAGE = "message";
    private String username;
    private String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.button_admin_login) {

            EditText uname = (EditText)findViewById(R.id.TFadmin_name);
            username = uname.getText().toString();

            EditText upass = (EditText)findViewById(R.id.TFadmin_password);
            password = upass.getText().toString();

            new JSONAdminLogin().execute();

            //Intent i = new Intent(AdminLogin.this, AdminHome.class);
            //i.putExtra("AdminUsername",str);
            //startActivity(i);
        }
    }


    class JSONAdminLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("admin_password", password));

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

                    Intent i = new Intent(AdminLogin.this, AdminHome.class);
                    i.putExtra("AdminUsername",username);
                    startActivity(i);

                    // closing this screen
                    finish();





                } else if (success == 0){
                    // successfully created product
                    String message = json.getString(TAG_MESSAGE);

                    Log.w("ADMINLOGIN", message);

                    if(message.equals(TAG_PASSWORD))
                    {
                        // pass was wrong
                        Toast temp = Toast.makeText(AdminLogin.this, "Password error", Toast.LENGTH_SHORT);
                        temp.show();
                    }
                    else
                    {
                        Toast temp = Toast.makeText(AdminLogin.this, "Username error", Toast.LENGTH_SHORT);
                        temp.show();
                    }

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
