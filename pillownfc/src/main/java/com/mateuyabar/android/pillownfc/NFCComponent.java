package com.mateuyabar.android.pillownfc;

/**
 * Created by mateuyabar on 20/10/15.
 */
public interface NFCComponent {
    public void writeText(String text);
    public void undoWriteText();

    public void setOnTagReadListener(TagReadListener onTagReadListener);

    /**
     * Sets the listener to write events
     */
    public void setOnTagWriteListener(TagWriteListener onTagWriteListener);

}
