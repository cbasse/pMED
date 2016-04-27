package com.example.pmed.mindfulnessmeditation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                    String ErrorText = "Connected to BioHarness "+ DeviceName;
                    TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                    tv.setText(ErrorText);
                    ((MindfulnessMeditation)getApplication())._bt = new BTClient(adapter, BhMacID);
                    _bt = ((MindfulnessMeditation)getApplication())._bt;
                    //_NConnListener = new NewConnectedListener(Newhandler,Newhandler);
                    _NConnListener = ((MindfulnessMeditation)getApplication()).listener;

                    _bt.addConnectedEventListener(_NConnListener);

                    if (_bt.IsConnected())
                    {
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
                                timeStampExperimentState(); //Caleb's method
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
                                setResult(1, getIntent()); //CALEB did this
                                finish(); //and this
                            }
                        });

                    }
                    else
                    {
                        //TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                        ErrorText  = "Unable to Connect";
                        tv.setText(ErrorText);
                    }

                }
            });
        } else if (((MindfulnessMeditation)getApplication())._bt.IsConnected())
        {
            //_bt.start();
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
            timer = new CountDownTimer(physioDuration*100, 1000){
                public void onTick(long millisUntilFinished) {
                    descText = (TextView)findViewById(R.id.text_description);
                    descText.setVisibility((View.VISIBLE));
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    long seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    timerText.setText(minutes + ":" + String.format("%02d", seconds));
                    Log.w("test", "test baby");
                }
                public void onFinish(){
                    descText.setVisibility((View.GONE));
                    startBtn.setVisibility(View.GONE);
                    timerText.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.VISIBLE);
                    timeStampExperimentState(); //Caleb's method
                }
            };
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.start();
                    startBtn.setVisibility(View.GONE);
                }
            });
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(RecordPhysData.this, Audio.class);
                    //startActivity(i);
                    setResult(1, getIntent()); //CALEB did this
                    finish(); //and this
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
        if (view.getId() == R.id.button_test) {
            //Intent i = new Intent(RecordPhysData.this, ListViewBarChartActivity.class);
            setResult(1,getIntent());
            finish();
            //startActivity(i);
            timeStampExperimentState();
        }
    }
}
