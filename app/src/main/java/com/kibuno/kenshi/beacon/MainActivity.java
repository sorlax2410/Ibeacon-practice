package com.kibuno.kenshi.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.axaet.ibeacon.beans.iBeaconClass;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.axaet.ibeacon.beans.iBeaconClass.iBeacon;
import com.kibuno.kenshi.beacon.Adapter.DeviceAdapter;
import com.kibuno.kenshi.beacon.Adapter.MinewBeaconListAdapter;
import com.minew.beaconset.MinewBeaconManager;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView mRecycle;
    private MinewBeaconManager mMinewBeaconManager;
    private MinewBeaconListAdapter mAdapter;
    private iBeaconClass iBeaconClass;
    private DeviceAdapter deviceAdapter;
    private ListView listView;
    private Button scanButton,
            sendButton;
    private ArrayList<iBeacon>beacons = new ArrayList<>();

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(bluetoothDevice != null /*&& rssi != 127*/) {
                iBeaconClass.iBeacon beacon = iBeaconClass.formToiBeacon(
                                bluetoothDevice,
                                rssi,
                                scanRecord
                        );
                beacons.add(beacon);
                deviceAdapter.addData(beacon);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = findViewById(R.id.btnScan);
        sendButton = findViewById(R.id.btnSend);

        initIbeaconView();
    }

    public void initIbeaconView() {

        listView = findViewById(R.id.listview);
        deviceAdapter = new DeviceAdapter(this);
        iBeaconClass = com.axaet.ibeacon.beans.iBeaconClass.getInstance();

        listView.setAdapter(deviceAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                final iBeacon device = deviceAdapter.getItem(itemIndex);
                if(device.deviceName.contains("_n")) {
                    Intent intent = new Intent(
                            MainActivity.this,
                            ModifyBeaconActivity.class
                    );
                    intent.putExtra("address", device.deviceAddress);
                    intent.putExtra("name", device.deviceName);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(
                            MainActivity.this,
                            "Cannot connect to device. " +
                                    "Are you sure the device(s) are working perfectly?",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        initBluetooth();
    }

    public void initMineBeaconView() {

        mRecycle = findViewById(R.id.main_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new MinewBeaconListAdapter();
        mRecycle.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothAdapter.startLeScan(leScanCallback);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    /**
     * @Description: initialize bluetooth low power
     */
    public void initBluetooth() {
        showEnergySupport();
        openBluetooth();
        EstimoteSDK.initialize(
                this,
                "",
                ""
        );
        EstimoteSDK.enableDebugLogging(true);
    }

    public void scanBluetooth(final View view) {
        deviceAdapter.clearData();
        bluetoothAdapter.stopLeScan(leScanCallback);
        try {
            Toast.makeText(
                    this,
                    "Scanning .",
                    Toast.LENGTH_SHORT
            ).show();
            Thread.sleep(100);
            Toast.makeText(
                    this,
                    "Scanning . .",
                    Toast.LENGTH_SHORT
            ).show();
            Thread.sleep(100);
            Toast.makeText(
                    this,
                    "Scanning . . .",
                    Toast.LENGTH_SHORT
            ).show();
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Log.e("MAINACTIVITY",
                    "\n\tInterrupted String: " + e.toString() +
                            "\n\tInterrupted Message: " + e.getMessage()
            );
        }
        bluetoothAdapter.startLeScan(leScanCallback);

        Toast.makeText(
                this,
                "Scan complete",
                Toast.LENGTH_SHORT
        ).show();

        if(!beacons.isEmpty()) {
            scanButton.setText(R.string.stop);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { stopBluetooth(view); }
            });
            Log.d("BEFORE", String.valueOf(beacons.size()));
            for(int index = 0; index < beacons.size(); index++)
                beacons.remove(index);
            Log.d("AFTER", String.valueOf(beacons.size()));
        }

        else {
            Toast.makeText(
                    this,
                    "no device found! Please try open this program without" +
                            " turning on bluetooth",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }
    }

    public void stopBluetooth(final View view) {
        bluetoothAdapter.stopLeScan(leScanCallback);
        Toast.makeText(
                this,
                "Scanning stopped successfully ^_^",
                Toast.LENGTH_SHORT
        ).show();
        scanButton.setText(R.string.start);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { scanBluetooth(view); }
        });
    }

    /**
     * @Description: destroy if low power not supported
     */
    private void showEnergySupport() {

        if(isLowPowerSupport()) {
            Toast.makeText(
                    this,
                    "ble(bluetooth low energy) not supported",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * @Description: check if bluetooth is enabled
     */
    private void openBluetooth() {

        BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();

            Toast.makeText(
                    this,
                    "initializing bluetooth",
                    Toast.LENGTH_SHORT
            ).show();
        }

        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();

            Toast.makeText(
                    this,
                    "bluetooth enabled",
                    Toast.LENGTH_SHORT
            ).show();
        }

        else {
            Toast.makeText(
                    this,
                    "Bluetooth is already on and enabled",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * @Description: check if device support low power bluetooth
     * @return true if supported and vice versa
     */
    private boolean isLowPowerSupport() {
        return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

}
