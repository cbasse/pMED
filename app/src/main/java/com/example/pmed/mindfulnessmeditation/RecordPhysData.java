package com.example.pmed.mindfulnessmeditation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

public class RecordPhysData extends AppCompatActivity {

    /** Called when the activity is first created. */
    BluetoothAdapter adapter = null;
    BTClient _bt;
    ZephyrProtocol _protocol;
    NewConnectedListener _NConnListener;
    private final int HEART_RATE = 0x100;
    private final int RESPIRATION_RATE = 0x101;
    private final int SKIN_TEMPERATURE = 0x102;
    private final int POSTURE = 0x103;
    private final int PEAK_ACCLERATION = 0x104;

    LinearLayout layout;
    LinearLayout.LayoutParams layoutParms;

    CountDownTimer timer;
    TextView timerText;
    TextView descText;
    TextView connectText;

    Integer physioDuration;

    Boolean isPre;
    String resultId;
    String dir;
    public String dirPath;

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connected_listener);
        /*Sending a message to android that we are going to initiate a pairing request*/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);


        this.resultId = getIntent().getStringExtra("com.example.pmed.RESULT_ID");
        this.isPre = (getIntent().getStringExtra("com.example.pmed.IS_PRE").equals("true"));
        this.dir = getIntent().getStringExtra("com.example.pmed.DIRECTORY");
        this.physioDuration = Integer.parseInt(getIntent().getStringExtra("com.example.pmed.PHYSIO_DURATION"));

        //Obtaining the handle to act on the CONNECT button
        TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
        String ErrorText  = "Not Connected to BioHarness";
        tv.setText(ErrorText);

        final ImageView btnConnect = (ImageView) findViewById(R.id.ButtonConnect);
        final ImageView imgBluetooth = (ImageView) findViewById(R.id.icon_bluetooth);
        final Button nextBtn = (Button) findViewById(R.id.ButtonNext);
        //btnConnect != null
        if (((MindfulnessMeditation)getApplication())._bt == null)
        {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String BhMacID = "00:07:80:9D:8A:E8";
                    //String BhMacID = "00:07:80:88:F6:BF";
                    adapter = BluetoothAdapter.getDefaultAdapter();

                    Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

                    if (pairedDevices.size() > 0)
                    {
                        for (BluetoothDevice device : pairedDevices)
                        {
                            if (device.getName().startsWith("BH"))
                            {
                                BluetoothDevice btDevice = device;
                                BhMacID = btDevice.getAddress();
                                break;
                            }
                        }

                    }

                    BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
                    String DeviceName = Device.getName();

                    ((MindfulnessMeditation)getApplication())._bt = new BTClient(adapter, BhMacID);
                    _bt = ((MindfulnessMeditation)getApplication())._bt;
                    //_NConnListener = new NewConnectedListener(Newhandler,Newhandler);
                    _NConnListener = ((MindfulnessMeditation)getApplication()).listener;
                    dirPath = _NConnListener.directory.getAbsolutePath();


                    _bt.addConnectedEventListener(_NConnListener);

                    if (_bt.IsConnected())
                    {
                        TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                        String ErrorText = "Connected to BioHarness "+ DeviceName;
                        tv.setText(ErrorText);

                        _bt.start();
                        //TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                        //ErrorText  = "Connected to BioHarness "+ DeviceName;
                        //tv.setText(ErrorText);

                        //Reset all the values to 0s

                        //!!!!!!!!!!!!!CONECTED

                        final Button startBtn = (Button) findViewById(R.id.ButtonStart);
                        imgBluetooth.setVisibility(View.GONE);
                        btnConnect.setVisibility(View.GONE);
                        startBtn.setVisibility(View.VISIBLE);
                        connectText = (TextView)findViewById(R.id.text_connect);
                        connectText.setVisibility((View.GONE));
                        timerText = (TextView)findViewById(R.id.CountdownText);
                        timerText.setVisibility((View.VISIBLE));

                        Integer min = physioDuration / 60;
                        Integer sec = physioDuration - (min * 60);
                        timerText.setText(min + ":" + String.format("%02d", sec));
                        timer = new CountDownTimer(physioDuration *1000, 1000){
                            public void onTick(long millisUntilFinished) {
                                descText = (TextView)findViewById(R.id.text_description);
                                descText.setVisibility((View.VISIBLE));
                                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                                long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                                timerText.setText(minutes + ":" + String.format("%02d", seconds));
                            }
                            public void onFinish(){
                                descText.setVisibility((View.GONE));
                                startBtn.setVisibility(View.GONE);
                                timerText.setVisibility(View.GONE);
                                nextBtn.setVisibility(View.VISIBLE);
                                _NConnListener.transmitData = false;
                                //timeStampExperimentState(); //Caleb's method
                            }
                        };
                        startBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timer.start();
                                _NConnListener.transmitData = true;
                                startBtn.setVisibility(View.GONE);
                            }
                        });
                        nextBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Intent i = new Intent(RecordPhysData.this, Audio.class);
                                //startActivity(i);

                                new PostPhysioResults().execute();
                                //setResult(1, getIntent()); //CALEB did this
                                //finish(); //and this
                            }
                        });

                    }
                    else
                    {
                        TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                        String ErrorText  = "Unable to Connect";
                        tv.setText(ErrorText);
                    }

                }
            });
        } else if (((MindfulnessMeditation)getApplication())._bt.IsConnected())
        {
            String BhMacID = "00:07:80:9D:8A:E8";
            //String BhMacID = "00:07:80:88:F6:BF";
            adapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    if (device.getName().startsWith("BH"))
                    {
                        BluetoothDevice btDevice = device;
                        BhMacID = btDevice.getAddress();
                        break;
                    }
                }

            }

            //_bt.start();
            BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
            String DeviceName = Device.getName();
            tv = (TextView) findViewById(R.id.labelStatusMsg);
            ErrorText  = "Connected to BioHarness "+ DeviceName;
            tv.setText(ErrorText);

            //_bt.start();
            //TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
            //ErrorText  = "Connected to BioHarness ";
            //tv.setText(ErrorText);

            //Reset all the values to 0s

            //!!!!!!!!!!!!!CONECTED
            Log.w("record_phys_data", "bluetooth is connected dawg");
            _NConnListener = ((MindfulnessMeditation)getApplication()).listener;
            dirPath = _NConnListener.directory.getAbsolutePath();

            final Button startBtn = (Button) findViewById(R.id.ButtonStart);
            imgBluetooth.setVisibility(View.GONE);
            btnConnect.setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);
            connectText = (TextView)findViewById(R.id.text_connect);
            connectText.setVisibility((View.GONE));
            timerText = (TextView)findViewById(R.id.CountdownText);
            timerText.setVisibility((View.VISIBLE));

            Integer min = physioDuration / 60;
            Integer sec = physioDuration - (min * 60);
            timerText.setText(min + ":" + String.format("%02d", sec));
            timer = new CountDownTimer(physioDuration *1000, 1000){
                public void onTick(long millisUntilFinished) {
                    descText = (TextView)findViewById(R.id.text_description);
                    descText.setVisibility((View.VISIBLE));
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    timerText.setText(minutes + ":" + String.format("%02d", seconds));
                }
                public void onFinish(){
                    descText.setVisibility((View.GONE));
                    startBtn.setVisibility(View.GONE);
                    timerText.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                    _NConnListener.transmitData = false;
                    //timeStampExperimentState(); //Caleb's method
                }
            };
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.start();
                    _NConnListener.transmitData = true;
                    startBtn.setVisibility(View.GONE);
                }
            });
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(RecordPhysData.this, Audio.class);
                    //startActivity(i);


                    //setResult(1, getIntent()); //CALEB did this
                    //finish(); //and this
                    new PostPhysioResults().execute();
                }
            });

        }
        else
        {
            //TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
            ErrorText  = "Unable to Connect";
            tv.setText(ErrorText);
        }

        /*Obtaining the handle to act on the DISCONNECT button*/
        Button btnDisconnect = (Button) findViewById(R.id.ButtonDisconnect);
        if (btnDisconnect != null)
        {
            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
				/*Functionality to act if the button DISCONNECT is touched*/
                public void onClick(View v) {
                    // TODO Auto-generated method stub
					/*Reset the global variables*/
                    TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                    String ErrorText  = "Disconnected from BioHarness";
                    tv.setText(ErrorText);

					/*This disconnects listener from acting on received messages*/
                    _bt.removeConnectedEventListener(_NConnListener);
					/*Close the communication with the device & throw an exception if failure*/
                    _bt.Close();

                }
            });
        }
    }
    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("Bond state", "BOND_STATED = " + device.getBondState());
        }
    }
    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[] {String.class} );
                byte[] pin = (byte[])m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*listener.experimentState will be Pre, During, Post depending on what part of the
      session the user is doing. */
    private void timeStampExperimentState() {
        System.out.println(((MindfulnessMeditation)getApplication()).listener.experimentState);
        switch (((MindfulnessMeditation)getApplication()).listener.experimentState) {
            case Pre:
                //timestamp?
                break;
            case During:
                //timestamp?
                break;
            case Post:
                //timestamp?
                if (((MindfulnessMeditation)getApplication())._bt != null) {
                    /*This disconnects listener from acting on received messages*/
                    ((MindfulnessMeditation) getApplication())._bt.removeConnectedEventListener(((MindfulnessMeditation) getApplication()).listener);
                    /*Close the communication with the device & throw an exception if failure*/
                    ((MindfulnessMeditation) getApplication())._bt.Close();
                }
                break;
            default:
                break;

        }
    }


    final Handler Newhandler = new Handler(){
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case HEART_RATE:
                    String HeartRatetext = msg.getData().getString("HeartRate");
                    System.out.println("Heart Rate Info is "+ HeartRatetext);
                    break;

                case RESPIRATION_RATE:
                    String RespirationRatetext = msg.getData().getString("RespirationRate");

                    break;

                case SKIN_TEMPERATURE:
                    String SkinTemperaturetext = msg.getData().getString("SkinTemperature");

                    break;

                case POSTURE:
                    String PostureText = msg.getData().getString("Posture");


                    break;

                case PEAK_ACCLERATION:
                    String PeakAccText = msg.getData().getString("PeakAcceleration");

                    break;


            }
        }

    };

    public void NextActivity(View view)
    {
        //Intent intent = new Intent(this);

    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.ButtonNext) {
            //Intent i = new Intent(this, FormActivity.class);
            setResult(1, getIntent());
            finish();
            //startActivity(i);
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
            ex.printStackTrace();
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


    class PostPhysioResults extends AsyncTask<String, String, String>
    {
        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            Log.w("physio", "about to post data");
            // Building Parameters
            String url = "http://meagherlab.co/update_physio_for_questionnaire_result.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            int avgHR = 1;
            int avgHRV = 1;
            File hrFile;
            File hrvFile;
            if(isPre)
            {
                hrFile = new File(dirPath +  "/PhysioHRpre.txt");
                hrvFile = new File(dirPath +  "/PhysioHRVpre.txt");
                while(!hrFile.exists() || !hrvFile.exists()) {
                    Log.w("record phys", "where the files at");
                    System.out.println(hrFile);
                    System.out.println(hrFile.exists());
                    System.out.println(hrvFile.exists());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                avgHR = getAvgFromFile(new File(dirPath +  "/PhysioHRpre.txt"));
                avgHRV = getAvgFromFile(new File(dirPath +  "/PhysioHRVpre.txt"));

            }
            else
            {
                hrFile = new File(dirPath +  "/PhysioHRpost.txt");
                hrvFile = new File(dirPath +  "/PhysioHRVpost.txt");
                while(!hrFile.exists() || !hrvFile.exists()) {
                    Log.w("record phys", "where the files at");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                avgHR = getAvgFromFile(new File(dirPath +  "/PhysioHRpost.txt"));
                avgHRV = getAvgFromFile(new File(dirPath +  "/PhysioHRVpost.txt"));
                Log.w("record phys", "its NOT pre and hr is " + Integer.toString(avgHR));
            }


            params.add(new BasicNameValuePair("id", resultId));
            params.add(new BasicNameValuePair("heart_rate", Integer.toString(avgHR)));
            params.add(new BasicNameValuePair("heart_rate_variability", Integer.toString(avgHRV)));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    Log.w("PHYSIO", "upload physio success!");

                } else {
                    Log.w("PHYSIO", "upload physio failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    setResult(1, getIntent()); //CALEB did this
                    finish(); //and this

                }
            });

        }


    }


}
