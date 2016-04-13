package com.example.pmed.mindfulnessmeditation;

/**
 * Created by harris on 4/5/2016.
 * FOR UPLOADING FILES IN ADMIN VIEW
 * Admin -> update experiment -> SELECT A FILE
 *
 */

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateExperiment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_experiment);

        File expDir = new File( Environment.getExternalStorageDirectory().getPath() + "/Experiments");
        List<File> folders = GetDirectories(expDir);
        int id = R.id.SelectFileText;
        for(File folder : folders) {
            Button btn = new Button(this);
            String name = folder.getName();
            int pos = name.lastIndexOf(".");
            if(pos > 0){
                name = name.substring(0, pos);
            }
            btn.setText(name);

            RelativeLayout layout = (RelativeLayout)findViewById(R.id.activity_update_experiment_id);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, id);

            if(id == R.id.SelectFileText)
                id = 2000;
            else
                id = id + 1;

            btn.setId(id);
            btn.setOnClickListener(SelectedFolder(btn));
            layout.addView(btn, params);
        }
    }


    private List<File> GetDirectories(File parentDir) {
        ArrayList<File> dirs = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                dirs.add(file);
            }
        }
        return dirs;
    }
    private View.OnClickListener SelectedFolder(final Button btn) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(UpdateExperiment.this, ConfirmExpParse.class);
                i.putExtra("folderName", btn.getText().toString());
                startActivity(i);
            }
        };
    }

}
