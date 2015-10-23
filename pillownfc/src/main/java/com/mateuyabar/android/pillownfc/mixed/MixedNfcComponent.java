package com.mateuyabar.android.pillownfc.mixed;

import android.app.Activity;

import com.mateuyabar.android.cleanbase.BaseActivityComponent;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;
import com.mateuyabar.android.pillownfc.bluetooth.BluetoothNfcComponent;
import com.mateuyabar.android.pillownfc.device.DeviceNfcComponent;

/**
 * Created by mateuyabar on 23/10/15.
 */
public class MixedNfcComponent extends BaseActivityComponent implements NFCComponent {
    TagReadListener onTagReadListener;
    TagWriteListener onTagWriteListener;

    BluetoothNfcComponent btNfc;
    DeviceNfcComponent deviceNfc;

    public MixedNfcComponent(Activity activity) {
        super(activity);

        btNfc = new BluetoothNfcComponent(activity);
        btNfc.setOnTagWriteListener(new TagWriteListener() {
            @Override
            public void onTagWritten(String id, Exception exception) {
                //we cancell the other write
                deviceNfc.undoWriteText();
                onTagWriteListener.onTagWritten(id, exception);
            }
        });
        addComponent(btNfc);

        deviceNfc = new DeviceNfcComponent(activity);
        deviceNfc.setOnTagWriteListener(new TagWriteListener() {
            @Override
            public void onTagWritten(String id, Exception exception) {
                //we cancell the other write
                btNfc.undoWriteText();
                onTagWriteListener.onTagWritten(id, exception);
            }
        });
        addComponent(deviceNfc);
    }


    @Override
    public void writeText(String text) {
        btNfc.writeText(text);
        deviceNfc.writeText(text);
    }

    @Override
    public void undoWriteText() {
        btNfc.undoWriteText();
        deviceNfc.undoWriteText();
    }

    @Override
    public void setOnTagReadListener(TagReadListener onTagReadListener) {
        this.onTagReadListener = onTagReadListener;
        btNfc.setOnTagReadListener(onTagReadListener);
        deviceNfc.setOnTagReadListener(onTagReadListener);
    }

    @Override
    public void setOnTagWriteListener(TagWriteListener onTagWriteListener) {
        this.onTagWriteListener = onTagWriteListener;
    }
}
