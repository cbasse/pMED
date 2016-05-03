package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.IOException;

public class Audio extends AppCompatActivity {

    MediaPlayer mp = new MediaPlayer();
    NewConnectedListener _NConnListener;

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        _NConnListener = ((MindfulnessMeditation)getApplication()).listener;

        // Use this code to pass the file location in by a string
        //Bundle bundle = getIntent().getExtras();
        //String fileLoc = bundle.getString("AudioFileName");

        // use this code to use the known path of an audio file
        //String fileLoc = Environment.getExternalStorageDirectory().getPath() + "/Experiments/__current__/audio.mp3";

        // this is for testing
        //String fileLoc = "file:///sdcard/Experiments/__current__/Eminem - Despicable.mp3";
        String fileLoc = getIntent().getStringExtra("com.example.pmed.SOUNDCLIP_PATH");
        //System.out.println(fileLoc);
        Uri audioUri = Uri.parse(fileLoc);
        mp.reset();
        try {
            mp.setDataSource(getApplicationContext(), audioUri);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer _mp) {
                    final Button nextBtn = (Button) findViewById(R.id.button_audio_next);
                    nextBtn.setVisibility(View.VISIBLE);
                    _NConnListener.transmitData = false;

                }
            });
            mp.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickButton(View v) {
        if (v.getId() == R.id.image_audio) {
            mp.start();
            v.setEnabled(false);
            _NConnListener.transmitData = true;
        }
        else if(v.getId() == R.id.button_audio_next)
        {
            setResult(1,getIntent());
            finish();
        }

    }
}