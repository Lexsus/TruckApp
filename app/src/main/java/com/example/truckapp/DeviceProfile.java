package com.example.truckapp;

import java.util.UUID;

public class DeviceProfile {
    //public final static UUID SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final static UUID SERVICE_UUID = UUID.fromString("ac3b72ea-b933-11ea-b3de-0242ac130004");

    //Read-Wrote only characteristic providing the state of the lamp
    public final static UUID CHARACTERISTIC_STATE_UUID_TX = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9A");

    public final static UUID CHARACTERISTIC_STATE_UUID_RX = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    public final static String DEVICE_ADDRESS = "24:0A:C4:82:7C:BA";
}
