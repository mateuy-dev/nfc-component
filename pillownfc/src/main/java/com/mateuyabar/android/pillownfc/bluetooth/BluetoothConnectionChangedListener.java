package com.mateuyabar.android.pillownfc.bluetooth;

public interface BluetoothConnectionChangedListener {
	void onConnected(String deviceName);
	void onConnectionLost();
	void onConnectionFailed();
}