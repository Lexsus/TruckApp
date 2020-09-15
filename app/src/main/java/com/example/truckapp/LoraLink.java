package com.example.truckapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.truckapp.DeviceProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoraLink {
    BluetoothGatt bluetoothGattConnection;
    private final Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattCallback bluetoothGattCallback;
    private final String TAG = "LoraLink";
    BluetoothGattCharacteristic m_characteristicWrite = null;
    BluetoothGattCharacteristic m_characteristicRead = null;
    BluetoothGattService serviceGatt;
    boolean mIsConnected = false;

    public LoraLink(final Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.d(TAG, "onConnectionStateChange");
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Connected to GATT server connect");
                    Log.i(TAG, "Attempting to start service discovery:" +
                            bluetoothGattConnection.discoverServices());
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Server disconnect");
                }

            }

            private void DumpServices(List<BluetoothGattService> services)
            {
                for (BluetoothGattService service:services
                     ) {
                    Log.d(TAG,"service uuid"+service.getUuid().toString());
                }
            }
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.d(TAG, "onServicesDiscovered");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    mIsConnected = true;
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    List<BluetoothGattService> gattServices = gatt.getServices();
                    DumpServices(gattServices);
                    serviceGatt = gatt.getService(DeviceProfile.SERVICE_UUID);
                    //statusTextView.setText("Connected");
                    //BluetoothGattCharacteristic characteristic = service.getCharacteristic(DeviceProfile.CHARACTERISTIC_STATE_UUID_TX);
                    //m_characteristicWrite = getCharacteristic(service, DeviceProfile.CHARACTERISTIC_STATE_UUID_TX.toString());

//                characteristic.setValue("COVID-19");
//                gatt.writeCharacteristic(characteristic);
//                gatt.setCharacteristicNotification(characteristic,true);
                    // characteristic.set
                    //gatt.readCharacteristic(characteristic);
                    if (serviceGatt != null) {

                        m_characteristicWrite = getCharacteristic(serviceGatt, 0x8);
                        m_characteristicRead = getCharacteristic(serviceGatt, 0x10);
                        gatt.setCharacteristicNotification(m_characteristicRead,true);
                        Log.d(TAG, "char write uuid=" + m_characteristicWrite.getUuid().toString());
                        Log.d(TAG, "char read uuid=" + m_characteristicRead.getUuid().toString());
                    }
//                    else
//                        Toast.makeText(context,"not find service ",Toast.LENGTH_LONG).show();
//                    m_characteristicWrite.setValue("STATUS");
//                    gatt.writeCharacteristic(m_characteristicWrite);

                    Log.d(TAG, "onServicesDiscovered");
                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            private BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service) {
                final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic character : characteristics) {
                    Log.d(TAG, "UUID=" + character.getUuid().toString());
                    //character.getProperties()
                    if ((character.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0)
                        return character;

                }
                return null;

            }

            private BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, int Property) {
                final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic character : characteristics) {
                    Log.d(TAG, "UUID=" + character.getUuid().toString());
                    int prop = character.getProperties();
                    int mask = prop & Property;
                    //character.getProperties()
                    if (mask > 0)
                        return character;

                }
                return null;

            }

            private BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String Characteristic) {
                final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic character : characteristics) {
                    Log.d(TAG, "UUID=" + character.getUuid().toString());
                    //character.getProperties()
                    Log.d(TAG, "char=" + Characteristic);
                    if (character.getUuid().toString().equalsIgnoreCase(Characteristic))
                        return character;

                }
                return null;

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Log.d(TAG, "onCharacteristicRead");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG, "onCharacteristicWrite");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.d(TAG, "onCharacteristicChanged");
            }
        };
    }

    public void connectGatt(BluetoothDevice dev) {
        bluetoothGattConnection = dev.connectGatt(context, true, bluetoothGattCallback);
    }

    public void writeValue(String value) {
        if (m_characteristicWrite != null) {
            m_characteristicWrite.setValue(value);
            bluetoothGattConnection.writeCharacteristic(m_characteristicWrite);
        }
//        bluetoothGattConnection.readCharacteristic(m_characteristicRead);
//        byte[] val;
//        val = m_characteristicRead.getValue();
    }

    public byte[] readValue() {

        bluetoothGattConnection.readCharacteristic(m_characteristicRead);
        byte[] val;
        SystemClock.sleep(500);
        val = m_characteristicRead.getValue();
        return val;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public    List<String> getCharacteristic(UUID serviceUUID){
        List<String> charaStrings = new ArrayList<>();
        BluetoothGattService service = bluetoothGattConnection.getService(serviceUUID);
        List<BluetoothGattCharacteristic> chaGattCharacteristics  = service.getCharacteristics();
        for (int i=0;i<chaGattCharacteristics.size();i++)
            charaStrings.add(chaGattCharacteristics.get(i).getUuid().toString());
        return charaStrings;
    }

    public    List<String>  getServices(){
        List<String> servicesStrings = new ArrayList<>();
        List<BluetoothGattService> services = bluetoothGattConnection.getServices();
        for (int i=0;i<services.size();i++)
            servicesStrings.add(services.get(i).getUuid().toString());
        return servicesStrings;
    }
}

