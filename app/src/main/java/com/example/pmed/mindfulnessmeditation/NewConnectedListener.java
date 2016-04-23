package com.example.pmed.mindfulnessmeditation;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ConnectListenerImpl;
import zephyr.android.BioHarnessBT.ConnectedEvent;
import zephyr.android.BioHarnessBT.PacketTypeRequest;
import zephyr.android.BioHarnessBT.ZephyrPacketArgs;
import zephyr.android.BioHarnessBT.ZephyrPacketEvent;
import zephyr.android.BioHarnessBT.ZephyrPacketListener;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

public class NewConnectedListener extends ConnectListenerImpl {

    private Handler _OldHandler;
    private Handler _aNewHandler;
    final int GP_MSG_ID = 0x20;
    final int BREATHING_MSG_ID = 0x21;
    final int ECG_MSG_ID = 0x22;
    final int RtoR_MSG_ID = 0x24;
    final int ACCEL_100mg_MSG_ID = 0x2A;
    final int SUMMARY_MSG_ID = 0x2B;


    private int GP_HANDLER_ID = 0x20;

    private final int HEART_RATE = 0x100;
    private final int RESPIRATION_RATE = 0x101;
    private final int SKIN_TEMPERATURE = 0x102;
    private final int POSTURE = 0x103;
    private final int PEAK_ACCLERATION = 0x104;
    /*Creating the different Objects for different types of Packets*/
    private GeneralPacketInfo GPInfo = new GeneralPacketInfo();
    private ECGPacketInfo ECGInfoPacket = new ECGPacketInfo();
    private BreathingPacketInfo BreathingInfoPacket = new  BreathingPacketInfo();
    private RtoRPacketInfo RtoRInfoPacket = new RtoRPacketInfo();
    private AccelerometerPacketInfo AccInfoPacket = new AccelerometerPacketInfo();
    private SummaryPacketInfo SummaryInfoPacket = new SummaryPacketInfo();



    // pmed Stuff
    public enum ExperimentState {
        Pre(0), During(1), Post(2);
        
        private final int value;
        private ExperimentState(int value) { this.value = value; }
        public int getValue() { return value; }
    }
    public ExperimentState experimentState;

    public enum DataType {
        HearRate(0), HRV(1);
        
        private final int value;
        private DataType(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    private File[][] files;
    private FileOutputStream[][] outputStreams;

    private PacketTypeRequest RqPacketType = new PacketTypeRequest();

    public NewConnectedListener(Handler handler,Handler _NewHandler) {
        super(handler, null);
        _OldHandler= handler;
        _aNewHandler = _NewHandler;

        // TODO Auto-generated constructor stub


        // pmed Stuff
        File dir = new File(Environment.getExternalStorageDirectory(), "Experiments");
        if(!dir.exists())
        {
            dir.mkdirs();
        }

        files = new File[ExperimentState.values().length][DataType.values().length];
        outputStreams = new FileOutputStream[ExperimentState.values().length][DataType.values().length];

        files[ExperimentState.Pre.getValue()][DataType.HearRate.getValue()] = new File(dir, "PhysioHRpre.txt");
        files[ExperimentState.Pre.getValue()][DataType.HRV.getValue()] = new File(dir, "PhysioHRVpre.txt");
        files[ExperimentState.During.getValue()][DataType.HearRate.getValue()] = new File(dir, "PhysioHRduring.txt");
        files[ExperimentState.During.getValue()][DataType.HRV.getValue()] = new File(dir, "PhysioHRVduring.txt");
        files[ExperimentState.Post.getValue()][DataType.HearRate.getValue()] = new File(dir, "PhysioHRpost.txt");
        files[ExperimentState.Post.getValue()][DataType.HRV.getValue()] = new File(dir, "PhysioHRVpost.txt");

        for (ExperimentState state : ExperimentState.values() )
        {
            for (DataType type : DataType.values() )
            {
                if(files[state.getValue()][type.getValue()].exists())
                {
                    files[state.getValue()][type.getValue()].delete();
                }
                try {
                    files[state.getValue()][type.getValue()].createNewFile();
                    outputStreams[ExperimentState.Pre.getValue()][DataType.HearRate.getValue()] = new FileOutputStream(files[state.getValue()][type.getValue()]);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }



    public void Connected(ConnectedEvent<BTClient> eventArgs) {
        System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));
		/*Use this object to enable or disable the different Packet types*/
        RqPacketType.GP_ENABLE = true;
        RqPacketType.BREATHING_ENABLE = true;
        RqPacketType.LOGGING_ENABLE = true;
        //RqPacketType.SUMMARY_ENABLE = true;


        //Creates a new ZephyrProtocol object and passes it the BTComms object]]
        ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), RqPacketType);
        //ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
        _protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
            public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
                ZephyrPacketArgs msg = eventArgs.getPacket();
                byte CRCFailStatus;
                byte RcvdBytes;



                CRCFailStatus = msg.getCRCStatus();
                RcvdBytes = msg.getNumRvcdBytes() ;
                int MsgID = msg.getMsgID();
                byte [] DataArray = msg.getBytes();
                switch (MsgID)
                {

                    case GP_MSG_ID:

                        //***************Displaying the Heart Rate********************************
                        int HRate =  GPInfo.GetHeartRate(DataArray);

                        // mindful meditation stuff
                        try{
                            String st = String.valueOf(HRate) + ", ";
                            System.out.println(experimentState.getValue());
                            outputStreams[experimentState.getValue()][DataType.HearRate.getValue()].write(st.getBytes());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        Message text1 = _aNewHandler.obtainMessage(HEART_RATE);
                        Bundle b1 = new Bundle();
                        b1.putString("HeartRate", String.valueOf(HRate));
                        text1.setData(b1);
                        _aNewHandler.sendMessage(text1);
                        //System.out.println("Heart Rate is "+ HRate);

                        //***************Displaying the Respiration Rate********************************
                        double RespRate = GPInfo.GetRespirationRate(DataArray);

                        text1 = _aNewHandler.obtainMessage(RESPIRATION_RATE);
                        b1.putString("RespirationRate", String.valueOf(RespRate));
                        text1.setData(b1);
                        _aNewHandler.sendMessage(text1);
                        //System.out.println("Respiration Rate is "+ RespRate);

                        //***************Displaying the Skin Temperature*******************************


                        double SkinTempDbl = GPInfo.GetSkinTemperature(DataArray);
                        text1 = _aNewHandler.obtainMessage(SKIN_TEMPERATURE);
                        //Bundle b1 = new Bundle();
                        b1.putString("SkinTemperature", String.valueOf(SkinTempDbl));
                        text1.setData(b1);
                        _aNewHandler.sendMessage(text1);
                        //System.out.println("Skin Temperature is "+ SkinTempDbl);

                        //***************Displaying the Posture******************************************

                        int PostureInt = GPInfo.GetPosture(DataArray);
                        text1 = _aNewHandler.obtainMessage(POSTURE);
                        b1.putString("Posture", String.valueOf(PostureInt));
                        text1.setData(b1);
                        _aNewHandler.sendMessage(text1);
                        //System.out.println("Posture is "+ PostureInt);
                        //***************Displaying the Peak Acceleration******************************************

                        double PeakAccDbl = GPInfo.GetPeakAcceleration(DataArray);
                        text1 = _aNewHandler.obtainMessage(PEAK_ACCLERATION);
                        b1.putString("PeakAcceleration", String.valueOf(PeakAccDbl));
                        text1.setData(b1);
                        _aNewHandler.sendMessage(text1);
                        //System.out.println("Peak Acceleration is "+ PeakAccDbl);

                        byte ROGStatus = GPInfo.GetROGStatus(DataArray);
                        //System.out.println("ROG Status is "+ ROGStatus);


                        break;
                    case BREATHING_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
                        System.out.println("Breathing Packet Sequence Number is "+BreathingInfoPacket.GetSeqNum(DataArray));
                        break;
                    case ECG_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
                        System.out.println("ECG Packet Sequence Number is "+ECGInfoPacket.GetSeqNum(DataArray));
                        break;
                    case RtoR_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
                        System.out.println("R to R Packet Sequence Number is "+RtoRInfoPacket.GetSeqNum(DataArray));
                        break;
                    case ACCEL_100mg_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
                        System.out.println("Accelerometry Packet Sequence Number is "+AccInfoPacket.GetSeqNum(DataArray));
                        break;
                    case SUMMARY_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
                        System.out.println("Summary Packet Sequence Number is " + SummaryInfoPacket.GetSeqNum(DataArray));
                        int hrv = SummaryInfoPacket.GetHearRateVariability(DataArray);

                        // mindful meditation stuff
                        try{
                            String st = String.valueOf(hrv) + ", ";
                            outputStreams[experimentState.getValue()][DataType.HRV.getValue()].write(st.getBytes());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        break;

                }
            }
        });
    }

}
