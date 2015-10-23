package com.mateuyabar.android.pillownfc.bluetooth;

import android.os.Handler;
import android.os.Message;

import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.device.Constants;

public class BluetoothChatServiceHandler extends Handler{
	String zzc="";
	BluetoothConnectionChangedListener bluetoothConnectionChangedListener;
	TagReadListener readListener;


	public BluetoothChatServiceHandler(TagReadListener readListener) {
		this.readListener = readListener;

	}

	public void setBluetoothConnectionChangedListener(BluetoothConnectionChangedListener bluetoothConnectionChangedListener) {
		this.bluetoothConnectionChangedListener = bluetoothConnectionChangedListener;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case Constants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
					case BluetoothChatService.STATE_CONNECTED:
						//bluetoothConnectionChangedListener.onConnected();
						break;
					case BluetoothChatService.STATE_CONNECTING:
						break;
					case BluetoothChatService.STATE_LISTEN:
					case BluetoothChatService.STATE_NONE:
						break;
				}
				break;
			case Constants.MESSAGE_WRITE:
				//Write not possible
				break;
			case Constants.MESSAGE_READ:
				String s3 = String.valueOf(byte2HexStr((byte[]) msg.obj, msg.arg1));
				String readMessage = (new StringBuilder(s3)).toString();

				zzc += readMessage;
				if(zzc.length()>4){
					readListener.onTagRead(zzc, null);
					zzc = "";
				}
				break;
			case Constants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
				if(bluetoothConnectionChangedListener!=null)
					bluetoothConnectionChangedListener.onConnected(deviceName);
				break;
			case Constants.MESSAGE_CONNECTION_LOST:
				if(bluetoothConnectionChangedListener!=null)
					bluetoothConnectionChangedListener.onConnectionLost();
				break;
			case Constants.MESSAGE_COULD_NOT_CONNECT:
				if(bluetoothConnectionChangedListener!=null)
					bluetoothConnectionChangedListener.onConnectionFailed();
				break;
		}
	}

	public static String byte2HexStr(byte abyte0[], int i)
	{
		StringBuilder stringbuilder = new StringBuilder("");
		int j = 0;
		do
		{
			if (j >= i)
				return stringbuilder.toString().toUpperCase().trim();
			String s = Integer.toHexString(abyte0[j] & 0xff);
			String s1;
			if (s.length() == 1)
				s1 = (new StringBuilder("0")).append(s).toString();
			else
				s1 = s;
			stringbuilder.append(s1);
			//	stringbuilder.append(" ");
			j++;
		} while (true);
	}
}