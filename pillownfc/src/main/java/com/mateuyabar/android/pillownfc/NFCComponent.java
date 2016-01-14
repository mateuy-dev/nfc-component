package com.mateuyabar.android.pillownfc;

import com.mateuyabar.android.cleanbase.Component;


public interface NFCComponent extends Component{
    public void writeText(String text);
    public void undoWriteText();

    public void setOnTagReadListener(TagReadListener onTagReadListener);

    /**
     * Sets the listener to write events
     */
    public void setOnTagWriteListener(TagWriteListener onTagWriteListener);

}
