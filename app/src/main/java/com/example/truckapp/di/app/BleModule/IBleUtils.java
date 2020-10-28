package com.example.truckapp.di.app.BleModule;

import android.bluetooth.BluetoothDevice;

public interface IBleUtils {
    public void connectGatt(BluetoothDevice dev);
    public void writeValue(String value);
    public byte[] readValue();
    public boolean isConnected();
}
