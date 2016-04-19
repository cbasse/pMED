package com.example.pmed.mindfulnessmeditation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userId = getIntent().getStringExtra("com.example.pmed.USER_ID");
        runSession();
    }
    
    public void runSession() {
        //get info from database with user_id
        int day = START_DAY;
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
            //FormResultsManager results = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");
            //Intent i = new Intent(this, RecordPhysData.class);

        }
    }
    
    
}
