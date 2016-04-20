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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);


        // Use this code to pass the file location in by a string
        //Bundle bundle = getIntent().getExtras();
        //String fileLoc = bundle.getString("AudioFileName");

        // use this code to use the known path of an audio file
        //String fileLoc = Environment.getExternalStorageDirectory().getPath() + "/Experiments/__current__/audio.mp3";

        // this is for testing
        String fileLoc = "file:///sdcard/Experiments/__current__/Eminem - Despicable.mp3";
        Uri audioUri = Uri.parse(fileLoc);
        mp.reset();
        try {
            mp.setDataSource(getApplicationContext(), audioUri);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer _mp) {
                    final Button nextBtn = (Button) findViewById(R.id.button_audio_next);
                    nextBtn.setVisibility(View.VISIBLE);
                    finish();
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
        }
        else if(v.getId() == R.id.button_audio_next)
        {
            // hey caleb, you should totally change this, you know, if you feel like it and stuff
        }

    }
}