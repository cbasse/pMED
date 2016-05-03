package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.pmed.formparser.AudioSync;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //don't change
        AudioSync.tabletPath = Environment.getExternalStorageDirectory().getPath() + "/AudioInterventions/";
    }

    public void onButtonClick(View v) {

        if(v.getId() == R.id.button_user) {

            Intent i = new Intent(MainActivity.this, UserLogin.class);

            startActivity(i);
        }
        if(v.getId() == R.id.button_admin) {

            Intent i = new Intent(MainActivity.this, AdminLogin.class);
            startActivity(i);
        }

        if(v.getId() == R.id.button_results) {

            Intent i = new Intent(MainActivity.this, ListViewBarChartActivity.class);
            startActivity(i);
        }

      /*  if(v.getId() == R.id.test) {
            final Handler Newhandler = new Handler(){
                public void handleMessage(Message msg)
                {
                }
            };
            ((MindfulnessMeditation)getApplication()).listener = new NewConnectedListener(Newhandler,Newhandler);
            Intent i = new Intent(this, RecordPhysData.class);
            ((MindfulnessMeditation)getApplication()).listener.experimentState = NewConnectedListener.ExperimentState.Post;
            System.out.println(((MindfulnessMeditation)getApplication()).listener.experimentState);
            startActivityForResult(i, 2);
        } */
    }

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521,PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
