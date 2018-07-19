package com.kibuno.kenshi.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.axaet.ibeacon.beans.iBeaconClass;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.axaet.ibeacon.beans.iBeaconClass.iBeacon;
import com.kibuno.kenshi.beacon.Adapter.DeviceAdapter;

public class MainActivity extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private iBeaconClass iBeaconClass;
    private DeviceAdapter deviceAdapter;
    private ListView listView;

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(bluetoothDevice != null && rssi != 127) {
                iBeaconClass.iBeacon beacon = iBeaconClass.formToiBeacon(
                                bluetoothDevice,
                                rssi,
                                scanRecord
                        );
                deviceAdapter.addData(beacon);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void initIbeaconView() {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_startscan:
                deviceAdapter.clearData();
                bluetoothAdapter.stopLeScan(leScanCallback);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { Log.e("MAINACTIVITY", e.getMessage()); }
                bluetoothAdapter.startLeScan(leScanCallback);
                break;

            case R.id.action_stopscan:
                bluetoothAdapter.stopLeScan(leScanCallback);
                break;
        }
        return true;
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

        if(bluetoothManager != null)
            bluetoothAdapter = bluetoothManager.getAdapter();

        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();

    }

    /**
     * @Description: check if device support low power bluetooth
     * @return true if supported and vice versa
     */
    private boolean isLowPowerSupport() {
        return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

}
