package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pmed.formparser.Form;
import com.example.pmed.formparser.StudyManifest;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by harri on 4/5/2016.
 * For uploading a file in admin view
 * Admin -> update experiment -> select a file -> CONFIRM PARSING PAGE
 */


public class ConfirmExpParse extends AppCompatActivity {

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
            Intent i = new Intent(ConfirmExpParse.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_exp_parse);

        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("folderName");

        TextView txt = (TextView)findViewById(R.id.ConfirmFilesText);
        final File expDir = new File( Environment.getExternalStorageDirectory().getPath() + "/Experiments/" + fileName);
        List<File> folders = GetListFiles(expDir);
        int id = R.id.SelectFileText;
        for(File folder : folders) {
            Button btn = new Button(this);
            String name = folder.getName();
            txt.append(name + "\n");
        }

        Button b = (Button) findViewById(R.id.ConfirmParseBtn);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TextView error = (TextView) findViewById(R.id.ErrorMessage);
                    error.setText("Syncing...");
                    StudyManifest manifest = new StudyManifest(expDir);

                    //do something


                    error.setText("Done!");


                    //Intent i = new Intent(ConfirmExpParse.this, AdminHome.class);
                    //startActivity(i);
                    finish();


                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        TextView error = (TextView) findViewById(R.id.ErrorMessage);
                        error.setText("File error: " + e.getMessage());
                    }
                }

            }
        });

    }

    public void onClickButton(View v) {

        if (v.getId() == R.id.CancelParseBtn) {
            Intent i = new Intent(ConfirmExpParse.this, UpdateExperiment.class);
            startActivity(i);
        }
    }

    private List<File> GetListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(GetListFiles(file));
            }
            else {
                //if(file.getName().endsWith(".csv")){
                //    inFiles.add(file);
                inFiles.add(file);
            }
        }
        return inFiles;
    }
}
