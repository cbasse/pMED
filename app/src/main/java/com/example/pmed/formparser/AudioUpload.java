package com.example.pmed.formparser;

/**
 * Created by harri on 4/23/2016.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.util.Config;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.internal.http.multipart.MultipartEntity;
import com.example.pmed.mindfulnessmeditation.MainActivity;
import com.example.pmed.mindfulnessmeditation.R;

/*
public class UploadAudio extends Activity {

    private static final int SELECT_AUDIO = 2;
    public static String selectedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openGalleryAudio();
    }

    public void openGalleryAudio() {

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio "), SELECT_AUDIO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_AUDIO) {
                System.out.println("SELECT_AUDIO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                new SaveImageTask().execute(selectedPath);
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void doFileUpload() {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "rn";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";
        Log.d("function", "this is the value of selected path" + selectedPath);
        String urlString = "http://10.0.2.2/Upload_App/WebService/upload_audio.php";
        try {
            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(selectedPath));
            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + selectedPath + "" + lineEnd);

            //dos.writeBytes("Content-Disposition: form-data; name="uploadedfile";filename="" + selectedPath + """ + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.e("Debug", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        //------------------ read the SERVER RESPONSE
        try {
            inStream = new DataInputStream(conn.getInputStream());
            String str;

            while ((str = inStream.readLine()) != null) {
                Log.e("Debug", "Server Response " + str);
            }
            inStream.close();

        } catch (IOException ioex) {
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class SaveImageTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            doFileUpload();
            return null;
        }

        protected void onPostExecute(Long result) {
            Log.d("File Uploading", "File is uploaded");
        }

    }
}
*/