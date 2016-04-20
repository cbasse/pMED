package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.HttpConnection;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddUser extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    String url = "http://meagherlab.co/create_user.php";
    String TAG_SUCCESS = "success";
    EditText uname;
    EditText pass1;
    EditText pass2;
    String uname_str;
    String pass1_str;
    String pass2_str;


    DatabaseHelper helper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
    }

    @Override
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
            Intent i = new Intent(AddUser.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickButton(View v) {

        if(v.getId() == R.id.button_user_added) {
            uname = (EditText)findViewById(R.id.TFuname);
            pass1 = (EditText)findViewById(R.id.TFpass1);
            pass2 = (EditText)findViewById(R.id.TFpass2);

            uname_str = uname.getText().toString();
            pass1_str = pass1.getText().toString();
            pass2_str = pass2.getText().toString();

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

                //helper.insertSubjects(s); // Lincoln - took out for testing

                //EditText a = (EditText)findViewById(R.id.TFuser_name);
                //String str = a.getText().toString();


                new CreateNewUser().execute();

                // commented out by lincoln - testing json methods
                //Intent i = new Intent(AddUser.this, ManageUserAccounts.class);
                //i.putExtra("Username", str);
                //startActivity(i);
            }
        }
    }


    class CreateNewUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname_str));
            params.add(new BasicNameValuePair("global_user_password", pass1_str));
            params.add(new BasicNameValuePair("admin_password", ""));
            params.add(new BasicNameValuePair("id", "0"));
            params.add(new BasicNameValuePair("experiment_id", "0"));
            params.add(new BasicNameValuePair("questionnaire_id", "0"));
            params.add(new BasicNameValuePair("is_admin", "0"));

            // getting JSON Object
            // Note that create product url accepts POST method
            Log.w("JSON", "Test1");
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(AddUser.this, ManageUserAccounts.class);
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
