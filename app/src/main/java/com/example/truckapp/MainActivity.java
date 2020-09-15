package com.example.truckapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.truckapp.DeviceProfile.DEVICE_ADDRESS;

public class MainActivity extends AppCompatActivity {
    private static final int UPDATE_IMAGE = 1;
    private static final int UPDATE_FIND = 2;
    BluetoothAdapter mBluetoothAdapter;
    private String TAG = "BLE";
    int BLUETOOTH_REQUEST_CODE = 1;
    boolean isStartSearch = false;
    LoraLink loraLink;
    boolean isConnected = false;
    private Set<String> deviceNames = new HashSet<>();
    private Set<BluetoothDevice> devices = new LinkedHashSet<>();
    Button startScanButton;
    Timer timer;
    TextView textViewA;
    TextView textViewB;
    TextView textViewC;
    ImageView imageViewRedA;
    ImageView imageViewGreenA;
    ImageView imageViewRedB;
    ImageView imageViewGreenB;
    ImageView imageViewRedC;
    ImageView imageViewGreenC;
    private TextView textViewStatus;
    InfoRC infoRC1, infoRC2, infoRC3;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //StopWatch.time.setText(formatIntoHHMMSS(elapsedTime)); //this is the textview
            Log.d(TAG, "Event handler");
            if (msg.what == UPDATE_IMAGE) {
                try {
                    String str = (String) msg.obj;
                    //imageViewGreenA.setVisibility(View.INVISIBLE);
                    String result[] = str.split(";");
//                String StringA[] = result[0].split(" ");
//                String StringB[] = result[1].split(" ");
//                String StringC[] = result[2].split(" ");

                    String[] res = result[0].split(" ");
                    int busy = Integer.parseInt(res[1]);
                    infoRC1.update(busy, res[2]);

                    res = result[1].split(" ");
                    busy = Integer.parseInt(res[1]);
                    infoRC2.update(busy, res[2]);

                    res = result[2].split(" ");
                    busy = Integer.parseInt(res[1]);
                    infoRC3.update(busy, res[2]);
                } catch (Exception ex) {

                }
            } else if (msg.what == UPDATE_FIND) {
                textViewStatus.setText("Найден " + DEVICE_ADDRESS);
            }
//            imageViewGreenB.setVisibility(View.INVISIBLE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewA = (TextView) findViewById(R.id.Text_A);
        textViewB = (TextView) findViewById(R.id.Text_B);
        textViewC = (TextView) findViewById(R.id.Text_C);
        textViewStatus = (TextView) findViewById(R.id.Text_Status);
        imageViewRedA = (ImageView) findViewById(R.id.RC_A_RED);
        imageViewGreenA = (ImageView) findViewById(R.id.RC_A_Green);
        imageViewRedB = (ImageView) findViewById(R.id.RC_B_RED);
        imageViewGreenB = (ImageView) findViewById(R.id.RC_B_Green);
        imageViewRedC = (ImageView) findViewById(R.id.RC_C_RED);
        imageViewGreenC = (ImageView) findViewById(R.id.RC_C_Green);
        infoRC1 = new InfoRC(imageViewRedA, imageViewGreenA, textViewA);
        infoRC2 = new InfoRC(imageViewRedB, imageViewGreenB, textViewB);
        infoRC3 = new InfoRC(imageViewRedC, imageViewGreenC, textViewC);
        startScanButton = (Button) findViewById(R.id.buttonScan);
//        btnScan = (Button) findViewById(R.id.buttonScan);
//        btnScan.setOnClickListener(OnClickListener);

        mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

//        statusTextView = (TextView) findViewById(R.id.statusTextView);

        loraLink = new LoraLink(this);
    }

    private ScanCallback newCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "on Scan result");
            processScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "on Scan Batch");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "on Scan Failed");
        }
    };

    private void stopBleScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(newCallBack);
    }

    private void processScanResult(ScanResult result) {
        BluetoothDevice bluetoothDevice = result.getDevice();
        Log.i(TAG, "device name " + bluetoothDevice.getName() + " with address " + bluetoothDevice.getAddress());
        deviceNames.add(bluetoothDevice.getName() + " " + bluetoothDevice.getAddress());
        devices.add(bluetoothDevice);
        if (bluetoothDevice.getAddress().equals(DEVICE_ADDRESS)) {
            Message msg = mHandler.obtainMessage();
            msg.what = UPDATE_FIND;
            msg.arg1 = 1;
            mHandler.sendMessage(msg);
        }
//        stopBleScan();
    }

    private void startBleScan() {

        //devices = new HashSet<>();
        ScanFilter filter = new ScanFilter.Builder().build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        Log.i(TAG, "start ble scan");
        mBluetoothAdapter.getBluetoothLeScanner().startScan(
                filters,
                settings, newCallBack);

    }

    public void onConnect(View view) {
        for (BluetoothDevice dev : devices
        ) {
            if (dev.getAddress().equals(DEVICE_ADDRESS)) {
                Log.d(TAG, "Connect device " + dev.getName());
                //bluetoothGatt = dev.connectGatt(av.getContext(), true, gattCallback);
                loraLink.connectGatt(dev);
                SystemClock.sleep(1500);
//                if (loraLink.isConnected()) {
//                    isConnected = true;

                loraLink.writeValue("RC");
                SystemClock.sleep(500);
                if (loraLink.isConnected())
                    textViewStatus.setText("Соединение установлено");
                Log.d(TAG, "isConnected = true");
                timer = new Timer();
                TimerTask t = new TimerTask() {
                    @Override
                    public void run() {
                        if (loraLink.isConnected()) {
                            byte[] val = loraLink.readValue();

                            if (val != null) {
                                String str = new String(val);
//                                textViewA.setText(StringA[2]+ " B");
//                                textViewB.setText(StringB[2]+ " B");
//                                mHandler.obtainMessage(1).sendToTarget();
                                Message msg = mHandler.obtainMessage();
                                msg.obj = str;
                                msg.what = UPDATE_IMAGE;
                                msg.arg1 = 1;
                                mHandler.sendMessage(msg);
                                Log.d(TAG, "recv: " + str);

                            } else
                                Log.d(TAG, "empty read");
                        }
                    }
                };
                timer.scheduleAtFixedRate(t, 1000, 1000);
//                } else
//                {
//                    isConnected = false;
//                    Log.d(TAG,"isConnected = false");
//                }
                return;
            }

        }
        //listPairedDevices();
    }

    public void onScan(View view) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);
        } else {
            isStartSearch = !isStartSearch;
            if (isStartSearch) {
                startScanButton.setText("STOP");
                startBleScan();
            } else {
                startScanButton.setText("SCAN");
                stopBleScan();
            }

        }
    }
}
