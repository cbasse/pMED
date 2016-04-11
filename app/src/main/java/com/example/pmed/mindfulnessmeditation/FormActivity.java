package com.example.pmed.mindfulnessmeditation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.pmed.formmanager.FormManager;
import com.example.pmed.formparser.Form;

import java.io.File;

public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout group = (LinearLayout) findViewById(R.id.prompts);

        //Log.d("WORKING DIRECTORY", System.getProperty("user.dir"));
        String formsDirectoryPath = Environment.getExternalStoragePublicDirectory("Forms").getAbsolutePath();
        Log.d("WORKING DIRECTORY", formsDirectoryPath);
        Form form = new Form(new File(formsDirectoryPath + "/text_q.xml"));

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        FormManager choice = new FormManager(form, inflater, group);



        /*

        RadioGroup.OnCheckedChangeListener multListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("RADIOGROUP", "checked: "+ checkedId);
            }
        };


        ((RadioGroup)mult.getChildAt(1)).setOnCheckedChangeListener(multListener);
        ((RadioGroup)mult1.getChildAt(1)).setOnCheckedChangeListener(multListener);

        mult.setId(View.generateViewId());
        LinearLayout group = (LinearLayout) findViewById(R.id.prompts);

        group.addView(mult);
        group.addView(mult1);*/




        //LinearLayout mult2 = (LinearLayout) findViewById(R.id.prompt_mult);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
