package com.example.pmed.mindfulnessmeditation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.pmed.formmanager.FormResultsManager;
import com.example.pmed.formparser.AudioSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by calebbasse on 4/17/16.
 */
public class SessionManager extends Activity {
    public final static int START_DAY = 0;
    public final static int STANDARD_DAY = 1;
    public final static int LAST_DAY = 2;
    public String userId;
    public String experimentId;
    public String questionnaireId;
    public String physioDuration;
    public FormResultsManager formAResults;
    public FormResultsManager formBResults;
    public FormResultsManager formCResults;
    NewConnectedListener listener;
    public int day;
    String soundclipPath;
    String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("sessionmanager", "test 0000");

        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        this.userId = i.getStringExtra("com.example.pmed.USER_ID");
        this.experimentId = i.getStringExtra("com.example.pmed.EXPERIMENT_ID");
        this.questionnaireId = i.getStringExtra("com.example.pmed.QUESTIONNAIRE_ID");
        this.physioDuration = i.getStringExtra("com.example.pmed.PHYSIO_DURATION");
        this.fileName = i.getStringExtra("com.example.pmed.FILENAME");
        Log.w("ses man", "q id is " + this.questionnaireId);


        final Handler Newhandler = new Handler(){
            public void handleMessage(Message msg)
            {
            }
        };

        AudioSync as = new AudioSync();
        soundclipPath = AudioSync.tabletPath + "somefile";
        if (!as.checkForAudioFileOnTablet(soundclipPath)) {
            System.out.println("GET THE FILE");
            System.out.println(soundclipPath);
            as.execute("download", "/" + this.fileName, soundclipPath);
        }



        ((MindfulnessMeditation)getApplication()).listener.directory = new File(Environment.getExternalStorageDirectory().getPath() + "/BioHarness/" + "DirName");
        System.out.println(((MindfulnessMeditation)getApplication()).listener.directory.getPath());
        ((MindfulnessMeditation)getApplication()).listener = new NewConnectedListener(Newhandler,Newhandler);
        ((MindfulnessMeditation)getApplication()).listener.directory.mkdir();

        listener = ((MindfulnessMeditation)getApplication()).listener;

        runSession();
    }
    
    public void runSession() {
        //get info from database with user_id
        Log.w("sessionmanager", "test 1");
        day = LAST_DAY ;
        Intent i;
        switch (day) {
            case START_DAY:
                i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                i.putExtra("com.example.pmed.REQUEST_CODE", "0");
                i.putExtra("com.example.pmed.USER_ID", this.userId);
                i.putExtra("com.example.pmed.EXPERIMENT_ID", this.experimentId);
                i.putExtra("com.example.pmed.QUESTIONNAIRE_ID", this.questionnaireId);
                i.putExtra("com.example.pmed.REQUEST_CODE", "0");
                startActivityForResult(i, 0);
                break;
            case STANDARD_DAY:
                i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                i.putExtra("com.example.pmed.REQUEST_CODE", "1");
                i.putExtra("com.example.pmed.USER_ID", this.userId);
                i.putExtra("com.example.pmed.EXPERIMENT_ID", this.experimentId);
                i.putExtra("com.example.pmed.QUESTIONNAIRE_ID", this.questionnaireId);
                i.putExtra("com.example.pmed.REQUEST_CODE", "0");
                startActivityForResult(i, 1);
                break;
            case LAST_DAY:
                i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                i.putExtra("com.example.pmed.REQUEST_CODE", "1");
                i.putExtra("com.example.pmed.USER_ID", this.userId);
                i.putExtra("com.example.pmed.EXPERIMENT_ID", this.experimentId);
                i.putExtra("com.example.pmed.QUESTIONNAIRE_ID", this.questionnaireId);
                i.putExtra("com.example.pmed.REQUEST_CODE", "0");
                startActivityForResult(i, 1);

                //i = new Intent(this, RecordPhysData.class);
                //listener.experimentState = NewConnectedListener.ExperimentState.Pre;
                //System.out.println(listener.experimentState);
                //startActivityForResult(i, 2);
                break;
            default:
                i = new Intent(this, FormActivity.class);
                break;
        }
        Log.w("sessionmanager", "test 2");
        i.putExtra("com.example.pmed.USER_ID", this.userId);
        i.putExtra("com.example.pmed.EXPERIMENT_ID", this.experimentId);
        i.putExtra("com.example.pmed.QUESTIONNAIRE_ID", this.questionnaireId);
        i.putExtra("com.example.pmed.REQUEST_CODE", "0");
        //startActivityForResult(i, 0);

        Log.w("sessionmanager", "test 3");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.w("sessionmanager", "test 4!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


        if (requestCode == 0 && resultCode == 1) {
            FormResultsManager results = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");

        } else if (requestCode == 1 && resultCode == 1) {
            formAResults = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");
            Intent i = new Intent(this, RecordPhysData.class);
            i.putExtra("com.example.pmed.PHYSIO_DURATION", this.physioDuration);
            listener.experimentState = NewConnectedListener.ExperimentState.Pre;
            System.out.println(listener.experimentState);
            startActivityForResult(i, 2);

        } else if (requestCode == 2 && resultCode == 1) {
            Intent i = new Intent(this, Audio.class);
            i.putExtra("com.example.pmed.SOUNDCLIP_PATH", soundclipPath);
            listener.experimentState = NewConnectedListener.ExperimentState.During;
            startActivityForResult(i,3);

        } else if (requestCode == 3 && resultCode == 1) {
            Intent i = new Intent(this, FormActivity.class);
            i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
            startActivityForResult(i,4);

        } else if (requestCode == 4 && resultCode == 1) {
            formBResults = data.getParcelableExtra("com.example.pmed.FORM_RESULTS");
            listener.experimentState = NewConnectedListener.ExperimentState.Post;
            Intent i = new Intent(this, RecordPhysData.class);
            startActivityForResult(i, 5);

        } else if (requestCode == 5 && resultCode == 1) {
            if (day != LAST_DAY) {

                int avgPreHR = getAvgFromFile(new File(listener.directory, "PhysioHRpre.txt"));


                Intent i = new Intent(this, ListViewBarChartActivity.class);
                startActivityForResult(i, 6);
            } else {
                Intent i = new Intent(this, FormActivity.class);
                i.putExtra("com.example.pmed.FORM_NAME", "TestStudy/bl_q.xml");
                startActivityForResult(i, 6);
            }

        } else if (requestCode == 6 && resultCode == 1) {
            if (day != LAST_DAY) {
                finish();
            } else {
                Intent i = new Intent(this, ListViewBarChartActivity.class);
                startActivityForResult(i, 7);
            }

        } else if (requestCode == 7 && resultCode == 1) {

            finish();

        }
    }

    private int getAvgFromFile(File f) {
        try {
            String fileText = "0";
            String line;
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(f);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                fileText += line;
            }

            // Always close files.
            bufferedReader.close();

            String[] valueStrings = fileText.split("\\s*,\\s*");

            int accum = 0;
            for (String val : valueStrings) {
                accum += Integer.parseInt(val);
            }
            return accum/(valueStrings.length+1);
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            f + "'");
            System.exit(1);
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + f + "'");
            // Or we could just do this:
            // ex.printStackTrace();
            System.exit(1);
        }
        return 0;
    }
}
