package com.kibuno.kenshi.beacon.Adapter;

import java.util.Collections;
import java.util.Comparator;

import com.axaet.ibeacon.beans.iBeaconClass.iBeacon;
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
			viewHolder.distanceMac = view.findViewById(R.id.txt_mac);
			viewHolder.deviceName = view.findViewById(R.id.txt_deviceName);
			viewHolder.deviceUUID = view.findViewById(R.id.txt_uuid);
			viewHolder.deviceMajor = view.findViewById(R.id.txt_major);
			viewHolder.deviceMinor = view.findViewById(R.id.txt_minor);
			viewHolder.deviceRssi = view.findViewById(R.id.txt_rssi);
			view.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) view.getTag();

		iBeacon device = list.get(position);
		String devicename = R.string.devicename + device.deviceName,
				deviceMAC = R.string.deviceMAC + device.deviceAddress,
				deviceuuid = R.string.deviceUUID + device.proximityUuid,
				devicemajor = String.valueOf(R.string.deviceMajor + device.major),
				deviceminor = String.valueOf(R.string.deviceMinor + device.minor),
				devicerssi = String.valueOf(R.string.deviceRSSI + device.rssi);

		viewHolder.deviceName.setText(devicename);
		viewHolder.distanceMac.setText(deviceMAC);
		viewHolder.deviceUUID.setText(deviceuuid);
		viewHolder.deviceMajor.setText(devicemajor);
		viewHolder.deviceMinor.setText(deviceminor);
		viewHolder.deviceRssi.setText(devicerssi);
		return view;
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceUUID;
		TextView distanceMac;
		TextView deviceMajor;
		TextView deviceMinor;
		TextView deviceRssi;
	}

}
