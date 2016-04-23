package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageUserAccounts extends AppCompatActivity {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper helper;
    Cursor cursor;
    ListDataAdapter listDataAdapter;

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
            Intent i = new Intent(ManageUserAccounts.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }




    JSONParser jParser = new JSONParser();
    private static final String url = "http://meagherlab.co/read_all_users.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ID = "id";
    private static final String TAG_EXP_ID = "experiment_id";
    private static final String TAG_Q_ID = "questionnaire_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_ADMIN_PASS = "admin_password";
    private static final String TAG_GLOBAL_PASS = "global_user_password";
    private static final String TAG_IS_ADMIN = "is_admin";
    private static final String TAG_USERS = "users";
    JSONArray users = null;
    ArrayList<HashMap<String, String>> usersList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_accounts);
        listView = (ListView)findViewById(R.id.list_view);
        listDataAdapter = new ListDataAdapter(getApplicationContext(),R.layout.row_layout);
        listView.setAdapter(listDataAdapter);


        usersList = new ArrayList<HashMap<String, String>>();
        new LoadAllUsers().execute();




        /*
        helper = new DatabaseHelper(getApplicationContext());
        sqLiteDatabase = helper.getReadableDatabase();
        cursor = helper.getInformation(sqLiteDatabase);

        if(cursor.moveToFirst()) { //checks if data is available on the cursor object
            do {

                Integer id;
                String uname, pass;
                //id = cursor.getString(0);
                id = cursor.getInt(0);
                uname = cursor.getString(1);
                pass = cursor.getString(2);
                Subjects s = new Subjects();
                s.setId(id);
                s.setUname(uname);
                s.setPass(pass);
                listDataAdapter.add(s); //pass each row of data into the adapter

            } while(cursor.moveToNext()); //will return true if another row is avaiable
        }
        */





    }

    public void onClickButton(View v) {
        if(v.getId() == R.id.button_add_user) {
            Intent i = new Intent(ManageUserAccounts.this, AddUser.class);
            startActivity(i);
        }
    }


    class LoadAllUsers extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    users = json.getJSONArray(TAG_USERS);

                    // looping through All Products
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String uname = c.getString(TAG_USERNAME);
                        String uPass = c.getString(TAG_GLOBAL_PASS);
                        String uExpId = c.getString(TAG_EXP_ID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_USERNAME, uname);
                        map.put(TAG_GLOBAL_PASS, uPass);
                        map.put(TAG_EXP_ID, uExpId);

                        // adding HashList to ArrayList
                        usersList.add(map);
                    }
                } else {
                    /*
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
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
                    Log.w("USERRSS", "count is " + usersList.size());
                    for (HashMap<String, String> user: usersList)
                    {
                        Log.w("USERRSS", user.get(TAG_ID).toString());
                        Subjects s = new Subjects();
                        s.setId(Integer.parseInt(user.get(TAG_ID).toString()));
                        s.setUname(user.get(TAG_USERNAME).toString());
                        s.setPass(user.get(TAG_EXP_ID).toString());
                        listDataAdapter.add(s);
                    }
                }
            });

        }


    }

}
