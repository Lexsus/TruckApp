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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.truckapp.DeviceProfile.DEVICE_ADDRESS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int UPDATE_IMAGE = 1;
    private static final int UPDATE_FIND = 2;
    private static final int UPDATE_RECV = 3;
    BluetoothAdapter mBluetoothAdapter;
    private String TAG = "BLE";
    int BLUETOOTH_REQUEST_CODE = 1;
    boolean isStartSearch = false;
    LoraLink loraLink;
    boolean isConnected = false;
    private Set<String> deviceNames = new HashSet<>();
    private Set<BluetoothDevice> devices = new LinkedHashSet<>();
    String m_prevRecvMessage="";
    Button startScanButton;
    Calendar calendar;
    Timer timer;
    TextView textViewA;
    TextView editTextLog;
    TextView editTextRSSI;
    String m_NameRC;
    private TextView textViewStatus;
    private Spinner spinner;
    ArrayList<String> dataRCrus = new ArrayList<>();
    ArrayList<String> dataRC = new ArrayList<>();

    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBoxCircle;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //StopWatch.time.setText(formatIntoHHMMSS(elapsedTime)); //this is the textview
            Log.d(TAG, "Event handler");
            if (msg.what == UPDATE_IMAGE) {

            } else if (msg.what == UPDATE_FIND) {
                textViewStatus.setText("Найден " + DEVICE_ADDRESS);
            } else if (msg.what == UPDATE_RECV) {
                String str = (String) msg.obj;
                try {
                    JSONObject json = new JSONObject(str);
                    String source = (String) json.get("source");
                    String command = (String) json.get("command");
                    if ("ESP32".equals(source)) {
                        Log.d(TAG, "Event handler json=" + json.toString());
                        if ("RECV".equals(command)) {
                            onCommandRecv(json);
                        }
                        if ("SF".equals(command)) {
                            onCommandSF(json);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            imageViewGreenB.setVisibility(View.INVISIBLE);
        }

        private void onCommandRecv(JSONObject json) throws JSONException {
            String data = (String) json.get("data");
            int time = (int) json.get("ms");
            String channel = "неизв.";
            if (json.getString("channel").equals("ch1"))
                channel = "канал 1";
            else if (json.getString("channel").equals("ch2"))
                channel = "канал 2";

            int ID = (int) json.get("ID");
            int global_ID = (int) json.get("GlobalID");
            int RSSI = (int) json.get("RSSI");
            int SNR = (int) json.get("SNR");
            byte array[] = data.getBytes();

            calendar = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss");
            String datetime = dateformat.format(calendar.getTime());


            String log = String.format("[%s]%s global_ID=%d data=%s rssi=%d snr=%d ms=%d",datetime, channel, global_ID, data,RSSI,SNR,time);
            textViewA.setText(data.substring(0,3)+" В");
            StringBuilder strRSSI = new StringBuilder();
            appendRSSI(strRSSI,json);


            //String strRSSI =
            editTextRSSI.setText(strRSSI);
            editTextLog.append(log + System.getProperty("line.separator"));
        }

        private void onCommandSF(JSONObject json) throws JSONException {
            String data = (String) json.get("data");
            String channel = "неизв.";
            if (json.getString("channel").equals("ch1"))
                channel = "канал 1";
            else if (json.getString("channel").equals("ch2"))
                channel = "канал 2";



            String log = String.format("SF смена%s: %s", channel, data);
            //String strRSSI =
            editTextLog.append(log + System.getProperty("line.separator"));
        }
    };


    private void appendRSSI(StringBuilder strRSSI,JSONObject json) throws JSONException {
        if ((byte)json.getInt("RSSI5")!=32)
            strRSSI.append("RSSI5=" + (-(byte)json.getInt("RSSI5"))+System.getProperty("line.separator"));
        else
            strRSSI.append("RSSI5=--" + System.getProperty("line.separator"));

        if ((byte)json.getInt("RSSI6")!=32)
            strRSSI.append("RSSI6=" + (-(byte)json.getInt("RSSI6"))+System.getProperty("line.separator"));
        else
            strRSSI.append("RSSI6=--" + System.getProperty("line.separator"));

        if ((byte)json.getInt("RSSI7")!=32)
            strRSSI.append("RSSI7=" + (-(byte)json.getInt("RSSI7"))+System.getProperty("line.separator"));
        else
            strRSSI.append("RSSI7=--" + System.getProperty("line.separator"));

        if ((byte)json.getInt("RSSI8")!=32)
            strRSSI.append("RSSI8=" + (-(byte)json.getInt("RSSI8"))+System.getProperty("line.separator"));
        else
            strRSSI.append("RSSI8=--" + System.getProperty("line.separator"));
    }

    private void createSpinner() {
        spinner = findViewById(R.id.spinnerRC);
        dataRCrus.add("1П");
        dataRCrus.add("2П");
        dataRCrus.add("3П");
        dataRCrus.add("4П");

        dataRC.add("1P");
        dataRC.add("2P");
        dataRC.add("3P");
        dataRC.add("4P");
//        spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,data);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spinner.setAdapter(spinnerAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dataRCrus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewA = (TextView) findViewById(R.id.Text_A);
        textViewStatus = (TextView) findViewById(R.id.Text_Status);
        editTextLog = (TextView) findViewById(R.id.editTextLog);
        editTextRSSI = (TextView) findViewById(R.id.editTextRSSI);
        startScanButton = (Button) findViewById(R.id.buttonScan);
        checkBox1 = (CheckBox) findViewById(R.id.checkBoxChannel1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBoxChannel2);
        checkBoxCircle = (CheckBox) findViewById(R.id.checkBoxCircle);
//        btnScan = (Button) findViewById(R.id.buttonScan);
//        btnScan.setOnClickListener(OnClickListener);

        mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

//        statusTextView = (TextView) findViewById(R.id.statusTextView);

        loraLink = new LoraLink(this);

        editTextLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        createSpinner();
        m_NameRC = dataRC.get(0);
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
                loraLink.connectGatt(dev);
                SystemClock.sleep(1500);
                //loraLink.writeValue("RC");
                //SystemClock.sleep(500);
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
                                if (!m_prevRecvMessage.equals(str)) {
                                    Message msg = mHandler.obtainMessage();
                                    m_prevRecvMessage = str;
                                    msg.obj = str;
                                    msg.what = UPDATE_RECV;
                                    msg.arg1 = 1;
                                    mHandler.sendMessage(msg);

                                    Log.d(TAG, "recv: " + str);
                                }
                                else
                                    Log.d(TAG, "recv duplicate : " + str);
                            } else
                                Log.d(TAG, "empty read");
                        }
                    }
                };
                timer.schedule(t, 0, 5000);
//                timer = new Timer();
//                TimerTask t = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (loraLink.isConnected()) {
//                            byte[] val = loraLink.readValue();
//
//                            if (val != null) {
//                                String str = new String(val);
//                                Message msg = mHandler.obtainMessage();
//                                msg.obj = str;
//                                msg.what = UPDATE_IMAGE;
//                                msg.arg1 = 1;
//                                mHandler.sendMessage(msg);
//                                Log.d(TAG, "recv: " + str);
//
//                            } else
//                                Log.d(TAG, "empty read");
//                        }
//                    }
//                };
//                timer.scheduleAtFixedRate(t, 1000, 1000);
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
                startScanButton.setText("СТОП");
                startBleScan();
            } else {
                startScanButton.setText("СКАН");
                stopBleScan();
            }

        }
    }

    public void onSend(View view) {
        JSONObject json = new JSONObject();
        //Log.d(TAG, "Event handler json="+json.toString());
        try {
            String channel="";
            if (checkBox1.isChecked())
                channel = "ch1";
            else if (checkBox2.isChecked())
                channel = "ch2";

            if (checkBox1.isChecked() && checkBox2.isChecked())
                channel = "all";

            String circle = "false";
            if (checkBoxCircle.isChecked())
                circle = "true";

            json.put("circle",circle);
            json.put("channel",channel);
            json.put("data",m_NameRC);
            json.put("command","SEND");
            json.put("source","android");
            if (loraLink.isConnected())
                loraLink.writeValue(json.toString());
            Log.d(TAG, "send json="+json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //m_NameRC = spinner.getSelectedItem().toString();
        m_NameRC = dataRC.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onSetSF(View view) {
        JSONObject json = new JSONObject();
        try {
            String channel="";
            if (checkBox1.isChecked())
                channel = "ch1";
            else if (checkBox2.isChecked())
                channel = "ch2";

            if (checkBox1.isChecked() && checkBox2.isChecked())
                channel = "all";

            json.put("channel",channel);
            json.put("command","SF");
            json.put("source","android");
            if (loraLink.isConnected())
                loraLink.writeValue(json.toString());
            Log.d(TAG, "send json="+json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
