package com.kibuno.kenshi.beacon;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.axaet.ibeacon.service.BluetoothLeService;

import java.util.ArrayList;
import java.util.List;

public class BeaconInformation extends Activity {

    private EditText textUuid;
    private TextView textState;
    private EditText textMajor;
    private EditText textMinor;
    private EditText textPeriod;
    private TextView textTemp;
    private TextView textHumidity;
    private TextView textDistance;
    private Spinner spinner;
    private EditText textName;
    private Button btnCalculateDistance;
    private Button btnReadTH;

    private String address = "";
    private String deviceName = "";

    private BluetoothLeService mBluetoothLeService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_information);
        initInterface();
        getIntentData();
        initSpinner();
    }

    private void getIntentData() {
        address = getIntent().getStringExtra("address");
        deviceName = getIntent().getStringExtra("name");
    }

    private void initSpinner() {
        List<String>list = new ArrayList<>();
        list.add("-23");
        list.add("-6");
        list.add("0");
        list.add("4");
    }

    private void initInterface() {
        textDistance = findViewById(R.id.text_distance);
        textHumidity = findViewById(R.id.text_humidity);
        textPeriod = findViewById(R.id.text_Period);
        textUuid = findViewById(R.id.text_uuid);
        textState = findViewById(R.id.text_state);
        textMajor = findViewById(R.id.text_Major);
        textMinor = findViewById(R.id.text_Minor);
        textTemp = findViewById(R.id.text_temp);
        spinner = findViewById(R.id.spinner);
        textName = findViewById(R.id.text_Name);
        btnCalculateDistance = findViewById(R.id.btn_calculateDistance);
        btnReadTH = findViewById(R.id.btn_readth);
    }
}
