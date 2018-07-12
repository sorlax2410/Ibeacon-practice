package com.kibuno.kenshi.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.axaet.ibeacon.beans.iBeaconClass;
import com.axaet.ibeacon.service.BluetoothLeService;
import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.kibuno.kenshi.beacon.Adapter.DeviceAdapter;

public class MainActivity extends Activity {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private iBeaconClass iBeaconClass;
    private DeviceAdapter deviceAdapter;
    private BluetoothLeService bluetoothLeService;
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //TODO:
            }
        }
    };

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


    /**
     * @Description:
     * @return return the filter
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_ONGETOTHER);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_ONGETTH);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_ONSEND);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_ONGETUUID);
        intentFilter.addAction(BluetoothLeService.ACTION_PASSWORD_ERROR);
        intentFilter.addAction(BluetoothLeService.ACTION_PASSWORD_SUCCESS);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBluetooth();
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

    public void initBluetooth() {
        showEnergySupport();
        openBluetooth();

        iBeaconClass = com.axaet.ibeacon.beans.iBeaconClass.getInstance();
        EstimoteSDK.initialize(
                this,
                "",
                ""
        );
        EstimoteSDK.enableDebugLogging(true);
    }

    private void showEnergySupport() {

        if(isLowPowerSupport()) {
            Toast.makeText(
                    this,
                    "ble(bluetooth low energy) not supported",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void openBluetooth() {

        bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null)
            bluetoothAdapter = bluetoothManager.getAdapter();

        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();

    }

    private boolean isLowPowerSupport() {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            return true;
        return false;
    }

    private void bindBroadcastService() {
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //TODO: initiate services
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                //TODO: kill services
            }
        };
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }
}
