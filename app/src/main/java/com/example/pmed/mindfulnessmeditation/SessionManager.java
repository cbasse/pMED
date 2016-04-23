package com.example.pmed.mindfulnessmeditation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.pmed.formmanager.FormResultsManager;

/**
 * Created by calebbasse on 4/17/16.
 */
public class SessionManager extends Activity {
    public final static int START_DAY = 0;
    public final static int STANDARD_DAY = 1;
    public final static int LAST_DAY = 2;
    public String userId;
    public FormResultsManager formAResults;
    NewConnectedListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userId = getIntent().getStringExtra("com.example.pmed.USER_ID");
        final Handler Newhandler = new Handler(){
            public void handleMessage(Message msg)
            {
            }
        };

        ((MindfulnessMeditation)getApplication()).listener = new NewConnectedListener(Newhandler,Newhandler);
        listener = ((MindfulnessMeditation)getApplication()).listener;
        runSession();
    }
    
    public void runSession() {
        //get info from database with user_id
        int day = STANDARD_DAY;
        Intent i;
        switch (day) {
            case START_DAY:
                i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                startActivityForResult(i, 0);
                break;
            case STANDARD_DAY:
                i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                startActivityForResult(i, 1);
                break;
            case LAST_DAY:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 && resultCode == 1) {
            FormResultsManager results = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");

        } else if (requestCode == 1 && resultCode == 1) {
            formAResults = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");
            Intent i = new Intent(this, RecordPhysData.class);
            listener.experimentState = NewConnectedListener.ExperimentState.Pre;
            System.out.println(listener.experimentState);
            startActivityForResult(i, 2);

        } else if (requestCode == 2 && resultCode == 1) {
            Intent i = new Intent(this, Audio.class);
            listener.experimentState = NewConnectedListener.ExperimentState.During;
            startActivityForResult(i,3);

        } else if (requestCode == 3 && resultCode == 1) {
            Intent i = new Intent(this, FormActivity.class);
            i.putExtra("com.example.pmed.FORM_RESULTS_B", formAResults);
            i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
            startActivityForResult(i,4);

        } else if (requestCode == 4 && resultCode == 1) {
            formAResults = data.getParcelableExtra("com.example.pmed.FORM_RESULTS_B");
            listener.experimentState = NewConnectedListener.ExperimentState.Post;
            Intent i = new Intent(this, RecordPhysData.class);
            startActivityForResult(i, 5);

        } else if (requestCode == 5 && resultCode == 1) {
            Intent i = new Intent(this, ListViewBarChartActivity.class);
            startActivityForResult(i, 6);

        } else if (requestCode == 6 && resultCode == 1) {
            finish();
        }
    }
    
    
}
