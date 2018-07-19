package com.kibuno.kenshi.beacon.Adapter;

import java.util.Collections;
import java.util.Comparator;

import com.axaet.ibeacon.beans.iBeaconClass.iBeacon;
import com.kibuno.kenshi.beacon.Core.DistanceCalculation;
import com.kibuno.kenshi.beacon.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeviceAdapter extends CommonBaseAdapter<iBeacon> {

	public DeviceAdapter(Context context) {
		super(context);
	}

	private Comparator<iBeacon> comparator = new Comparator<iBeacon>() {
		@Override
		public int compare(iBeacon h1, iBeacon h2) {
			return h2.rssi - h1.rssi;
		}
	};

	public synchronized void addData(iBeacon device) {
		if (device == null)
			return;

		boolean existAddress = false;

		for (iBeacon iBeacon : list) {
			existAddress = iBeacon.deviceAddress.equals(device.deviceAddress);
			if (existAddress) {
				list.remove(iBeacon);
				list.add(device);
				break;
			}
		}

		if (!existAddress)
			list.add(device);

		Collections.sort(this.list, comparator);
		notifyDataSetChanged();
	}


	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if (view == null) {
			view = inflater.inflate(R.layout.listitem_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceInfo = view.findViewById(R.id.txt_deviceInfo);
			viewHolder.deviceMeasurement = view.findViewById(R.id.txt_deviceMeasurement);
			view.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) view.getTag();

		iBeacon device = list.get(position);/*
		device.distance = String.valueOf(
				DistanceCalculation.calculateDistance(
						device.txPower,
						device.rssi)
		);*/
		String calculateDist = String.valueOf(
				DistanceCalculation.calculateDist(
						device.txPower,
						(double)device.rssi
				)
		);
		String calculateDistance = String.valueOf(
				DistanceCalculation.calculateDistance(
						device.txPower,
						device.rssi
				)
		);
		String computeAccuracy = String.valueOf(
				DistanceCalculation.computeAccuracy(
						device.txPower,
						(double)device.rssi
				)
		);
		String deviceInfo = "Device name: " + device.deviceName + "\n" +
				"MAC: " + device.deviceAddress,

				deviceMeasurement =
						"UUID: " + device.proximityUuid + "\n" +
						"Major: " + String.valueOf( device.major) + "\n" +
						"Minor: " + String.valueOf( device.minor) + "\n" +
						"RSSI: " + String.valueOf(device.rssi) + "\n" +
						"Distance 1: " + calculateDist + "\n" +
						"Distance 2: " + calculateDistance + "\n" +
						"Distance 3: " + computeAccuracy;

		viewHolder.deviceInfo.setText(deviceInfo);
		viewHolder.deviceMeasurement.setText(deviceMeasurement);
		return view;
	}

	static class ViewHolder {
		TextView deviceInfo;
		TextView deviceMeasurement;
	}

}
