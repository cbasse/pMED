package com.example.pmed.mindfulnessmeditation;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

import zephyr.android.BioHarnessBT.BTClient;

/**
 * Created by calebbasse on 4/21/16.
 */
public class MindfulnessMeditation extends Application {

    public NewConnectedListener listener;
    BluetoothAdapter adapter = null;
    BTClient _bt;

}
