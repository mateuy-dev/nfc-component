package com.mateuyabar.android.pillownfc.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import com.mateuyabar.android.cleanbase.BaseComponent;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;

import java.io.UnsupportedEncodingException;
import java.util.Set;


public class BluetoothNfcComponent extends BaseComponent implements TagReadListener, NFCComponent{
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "BluetoothNFC";

    // String buffer for outgoing messages
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    TagReadListener onTagReadListener;
    TagWriteListener onTagWriteListener;


    BluetoothNotAvailableListener blueToothNotAvailableListener;

    Activity activity;
    BluetoothChatServiceHandler mHandler;

    //This NFC does not have writing capabilities. We just use it as a single item read mode.
    boolean writing = false;

    public BluetoothNfcComponent(Activity activity){
        super(activity);
        this.activity = activity;
        mHandler = new BluetoothChatServiceHandler(this);
    }

    public void setBluetoothConnectionChangedListener(BluetoothConnectionChangedListener bluetoothConnectionChangedListener) {
        mHandler.setBluetoothConnectionChangedListener(bluetoothConnectionChangedListener);
    }

    public void setBlueToothNotAvailableListener(BluetoothNotAvailableListener blueToothNotAvailableListener) {
        this.blueToothNotAvailableListener = blueToothNotAvailableListener;
    }

    @Override
    public void onCreate(){
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            blueToothNotAvailableListener.blueToothNotAvailable();
        }
    }

    @Override
    public void onStart(){
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public void onResume() {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        mChatService = new BluetoothChatService(activity, mHandler);

        //-------------------------------
        BluetoothDevice myDevice = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView

                if(device.getName().startsWith("HC"))
                    myDevice = device;
            }
        }
        //---------------------------

//		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(myDevice, true);
    }

    private void ensureDiscoverable() {
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message)
            throws UnsupportedEncodingException {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
//					.show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = hexStr2Bytes(message.toUpperCase());
            mChatService.write(send);
        }
    }

    @Override
    public void onDestroy() {
        if (mChatService != null)
            mChatService.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
//				String address = data.getExtras()
//                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//				// Get the BLuetoothDevice object
//				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
//				mChatService.connect(device);
                }
                break;
            case BluetoothNfcComponent.REQUEST_ENABLE_BT:
//			if (resultCode == Activity.RESULT_OK) {
//				setupChat();
//			} else {
//				Log.d(TAG, "BT not enabled");
//				Toast.makeText(this, R.string.bt_not_enabled_leaving,
//						Toast.LENGTH_SHORT).show();
//				finish();
//			}
        }
    }

    private static byte[] hexStr2Bytes(String s)
    {
        s = s.toString().trim().replace(" ", "");
        int i = s.length() / 2;
        System.out.println(i);
        byte abyte0[] = new byte[i];
        int j = 0;
        do
        {
            if (j >= i)
                return abyte0;
            int k = j * 2 + 1;
            int l = k + 1;
            StringBuilder stringbuilder = new StringBuilder("0x");
            int i1 = j * 2;
            String s1 = s.substring(i1, k);
            StringBuilder stringbuilder1 = stringbuilder.append(s1);
            String s2 = s.substring(k, l);
            byte byte0 = (byte)(Integer.decode(stringbuilder1.append(s2).toString()).intValue() & 0xff);
            abyte0[j] = byte0;
            j++;
        } while (true);
    }

    @Override
    public void onTagRead(String id, String tagRead) {
        if(writing){
            onTagWriteListener.onTagWritten(id, new UnsupportedOperationException());
        } else {
            onTagReadListener.onTagRead(id, null);
        }

    }

    @Override
    public void writeText(String text) {
        writing = true;
    }

    @Override
    public void undoWriteText() {
        writing = false;
    }

    @Override
    public void setOnTagReadListener(TagReadListener onTagReadListener) {
        this.onTagReadListener = onTagReadListener;
    }

    @Override
    public void setOnTagWriteListener(TagWriteListener onTagWriteListener) {
        this.onTagWriteListener = onTagWriteListener;
    }

    public interface BluetoothNotAvailableListener {
        public void blueToothNotAvailable();
    }

}
