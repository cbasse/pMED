package com.example.pmed.mindfulnessmeditation;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harri on 4/5/2016.
 * For uploading a file in admin view
 * Admin -> update experiment -> select a file -> CONFIRM PARSING PAGE
 */
public class ConfirmExpParse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_exp_parse);

        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("folderName");

        TextView txt = (TextView)findViewById(R.id.ConfirmFilesText);
        File expDir = new File( Environment.getExternalStorageDirectory().getPath() + "/Experiments/" + fileName);
        List<File> folders = GetListFiles(expDir);
        int id = R.id.SelectFileText;
        for(File folder : folders) {
            Button btn = new Button(this);
            String name = folder.getName();
            txt.append(name + "\n");
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
