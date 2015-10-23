package com.mateuyabar.android.pillownfc.sampleapp;

import android.widget.Toast;

import com.mateuyabar.android.cleanbase.Component;
import com.mateuyabar.android.cleanbase.PresenterActivity;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.bluetooth.BluetoothConnectionChangedListener;
import com.mateuyabar.android.pillownfc.bluetooth.BluetoothNfcComponent;

/**
 *
 * The jelly bean bluetooth stack is markedly different from the other versions.

 This might help: http://wiresareobsolete.com/wordpress/2010/11/android-bluetooth-rfcomm/

 In gist: The UUID is a value that must point to a published service on your embedded device, it is not just randomly generated. The RFCOMM SPP connection you want to access has a specific UUID that it publishes to identify that service, and when you create a socket it must match the same UUID.

 If you are targeting 4.0.3 device and above , use fetchUuidsWithSdp() and getUuids() to find all the published services and their associated UUID values. For backward compatibility read the article
 *
 *
 */

public class BluetoothReaderDemoActivity extends PresenterActivity implements BluetoothConnectionChangedListener, TagReadListener, BluetoothNfcComponent.BluetoothNotAvailableListener {
	BluetoothNfcComponent presenter;

	public BluetoothReaderDemoActivity(){
		presenter = new BluetoothNfcComponent(this);
		presenter.setBluetoothConnectionChangedListener(this);
		presenter.setOnTagReadListener(this);
		presenter.setBlueToothNotAvailableListener(this);
	}

	@Override
	public Component getPresenter() {
		return presenter;
	}

	@Override
	public void onConnected(String deviceName) {
		Toast.makeText(BluetoothReaderDemoActivity.this, "Connected to " + deviceName, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onConnectionLost() {
		Toast.makeText(BluetoothReaderDemoActivity.this, "Connection lost", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnectionFailed() {
		Toast.makeText(BluetoothReaderDemoActivity.this, "Could not connect", Toast.LENGTH_LONG).show();
	}

	@Override
	public void blueToothNotAvailable() {
		Toast.makeText(BluetoothReaderDemoActivity.this, "BlueTooth not available", Toast.LENGTH_LONG).show();
	}


	@Override
	public void onTagRead(String id, String tagRead) {
		Toast.makeText(BluetoothReaderDemoActivity.this, "Rfid recieved: "+id, Toast.LENGTH_LONG).show();
	}
}