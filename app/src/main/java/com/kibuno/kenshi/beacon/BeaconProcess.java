package com.kibuno.kenshi.beacon;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.axaet.ibeacon.service.BluetoothLeService;

import static android.content.Context.BIND_AUTO_CREATE;

public class BeaconProcess {

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

    public void bindBroadcastService(Context context) {
        context.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
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
        context.bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public String calculateDistance(int rssi) {
        String result;
        result = String.valueOf(rssi);
        return result;
    }
}
