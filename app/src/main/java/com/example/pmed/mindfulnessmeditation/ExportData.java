package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by harri on 4/26/2016.
 */
public class ExportData extends AppCompatActivity {
    String url_getExps = "http://meagherlab.co/read_all_experiments.php";
    private static final String TAG_EXPERIMENTS = "experiments";
    private static final String TAG_EXP_ID = "id";
    private static final String TAG_EXP_NAME = "name";
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> expsList;
    JSONArray experiments = null;

    String selectedExperimentId;


    JSONParser jsonParser = new JSONParser();
    String url = "http://meagherlab.co/build_response_csv.php";
    String TAG_SUCCESS = "success";

    /*

    EditText pass1;
    EditText pass2;
    String pass1_str;
    String pass2_str;

     */


    DatabaseHelper helper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        expsList = new ArrayList<HashMap<String, String>>();

        new GetExperiments().execute();
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
            Intent i = new Intent(ExportData.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickButton(View v) {

        if(v.getId() == R.id.button_export_data) {

            Spinner spinner = (Spinner)findViewById(R.id.ExportExpNum);
            String selectedSpinner = spinner.getSelectedItem().toString();
            selectedExperimentId = selectedSpinner.substring(0, selectedSpinner.indexOf("'"));

            new GetExportData().execute();

            /*
            pass1 = (EditText)findViewById(R.id.TFpass1);
            pass2 = (EditText)findViewById(R.id.TFpass2);
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
            */
        }
    }

    private void writeToSDFile(ArrayList<String> strings){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/Experiments");
        dir.mkdirs();
        File file = new File(dir, "Experiment_" + selectedExperimentId + "_data" + ".csv");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);

            for(String line : strings)
            {
                pw.println(line);
            }

            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class GetExportData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", selectedExperimentId));

            // getting JSON Object
            // Note that create product url accepts POST method
            Log.w("JSON", "the exp id is " + selectedExperimentId);
            JSONObject json = jsonParser.makeHttpRequest("http://meagherlab.co/build_response_csv.php","GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.w("build csv", "starting to build da csv");
                    ArrayList<String> lines = new ArrayList<String>();

                    JSONArray headers = json.getJSONArray("question_headers");
                    String qHeader = "User Id";
                    for(int i = 0; i < headers.length(); i++)
                    {
                        System.out.println(headers.getString(i));
                        System.out.println(headers.getString(i).replace(',', ' '));
                        qHeader = qHeader + ", " + headers.getString(i).replace(',', ' ');
                        /*
                        JSONArray line = headers.getJSONArray(i);
                        for(int j = 0; j < line.length(); j++)
                        {
                            qHeader = qHeader + ", " + line.getString(j);
                        }
                        */
                    }
                    lines.add(qHeader);

                    JSONArray users = json.getJSONArray("users");
                    for(int i = 0; i < users.length(); i++)
                    {
                        JSONObject user = users.getJSONObject(i);

                        String userLine = "";
                        userLine = user.getString("user_id");
                        JSONArray answers = user.getJSONArray("responses");
                        for(int j = 0; j < answers.length(); j++)
                        {
                            userLine = userLine + ", " + answers.getString(j).replace(',', ' ');
                        }

                        lines.add(userLine);
                    }


                    writeToSDFile(lines);


                    // successfully created product
                    Intent i = new Intent(ExportData.this, AdminHome.class);
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

    class GetExperiments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_getExps, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    experiments = json.getJSONArray(TAG_EXPERIMENTS);

                    // looping through All Products
                    for (int i = 0; i < experiments.length(); i++) {
                        JSONObject c = experiments.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_EXP_ID);
                        String name = "NO NAME";
                        if(c.has(TAG_EXP_NAME))
                        {
                            name = c.getString(TAG_EXP_NAME);
                        }

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_EXP_ID, id);
                        map.put(TAG_EXP_NAME, name);

                        // adding HashList to ArrayList
                        expsList.add(map);
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

            // closing this screen
            //finish();

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    Spinner spinner = (Spinner) findViewById(R.id.ExportExpNum);
                    List<String> expList = new ArrayList<String>();

                    for(HashMap<String, String> exp : expsList)
                    {
                        String selectValue = exp.get(TAG_EXP_ID) +
                                " '" + exp.get(TAG_EXP_NAME) + "'" ;
                        expList.add(selectValue);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, expList );

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(adapter);
                }
            });

        }


    }
}
