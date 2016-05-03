package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserLogin extends AppCompatActivity {

    //DatabaseHelper helper = new DatabaseHelper(this);
    String uname;
    String pass;
    String message;
    Boolean isCorrect = false;
    JSONParser jsonParser = new JSONParser();
    private static final String url = "http://meagherlab.co/authenticate_user.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.button_user_login) {

            EditText a = (EditText)findViewById(R.id.TFuser_name);
            uname = a.getText().toString();
            EditText b = (EditText)findViewById(R.id.TFuser_password);
            pass = b.getText().toString();

            new JSONUserLogin().execute();
            //String password = helper.searchPass(str);
            //String password = helper.globalPassword();


            /*if(pass.equals(TAG_PASSWORD)) {
                Intent i = new Intent(UserLogin.this, SessionManager.class);
                i.putExtra("com.example.pmed.USER_ID", "someUserId");
                startActivity(i);
                //startActivity(i);
            }
            else {
                //popup msg
                Toast temp = Toast.makeText(UserLogin.this, "Username and Password don't match", Toast.LENGTH_SHORT);
                temp.show();
            }*/
        }
    }


    class JSONUserLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("password", pass));

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

                    String qId = json.getString("questionnaire_id");
                    String eId = json.getString("experiment_id");
                    String uId = json.getString("id");
                    String pDur = json.getString("physio_duration");
                    String fName = json.getString("intervention_filename");
                    String qType = json.getString("questionnaire_type");
                    String dayNum = json.getString("day_number");
                    String totDays = json.getString("number_of_days");

                    Log.w("stuff", "questionnaire id is " + qId);

                    isCorrect = true;
                    Intent i = new Intent(UserLogin.this, SessionManager.class);

                    i.putExtra("com.example.pmed.USER_ID", uId);
                    i.putExtra("com.example.pmed.EXPERIMENT_ID", eId);
                    i.putExtra("com.example.pmed.QUESTIONNAIRE_ID", qId);
                    i.putExtra("com.example.pmed.PHYSIO_DURATION", pDur);
                    i.putExtra("com.example.pmed.FILENAME", fName);
                    i.putExtra("com.example.pmed.QUESTIONNAIRE_TYPE", qType);
                    i.putExtra("com.example.pmed.DAY_NUMBER", dayNum);
                    i.putExtra("com.example.pmed.TOTAL_DAYS", totDays);

                    Log.w("stuf", "day num is " + dayNum + " and tot days is " + totDays );

                    startActivity(i);

                    Log.w("login", "succeed");

                    // closing this screen
                    finish();


                } else if (success == 0){
                    isCorrect = false;
                    // successfully created product
                    message = json.getString(TAG_MESSAGE);

                    //finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if(isCorrect)
                    {
                        return;
                    }
                    if(message.equals(TAG_PASSWORD))
                    {
                        // pass was wrong
                        Toast temp = Toast.makeText(UserLogin.this, "Password error", Toast.LENGTH_SHORT);
                        temp.show();
                    }
                    else
                    {
                        Toast temp = Toast.makeText(UserLogin.this, "Username error", Toast.LENGTH_SHORT);
                        temp.show();
                    }

                }
            });

        }


    }


}
