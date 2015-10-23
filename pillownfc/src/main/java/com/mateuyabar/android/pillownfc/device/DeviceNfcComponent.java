package com.mateuyabar.android.pillownfc.device;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;

import com.mateuyabar.android.cleanbase.BaseActivityComponent;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;
import com.mateuyabar.android.pillownfc.device.NFCWriteException.NFCErrorType;

import java.io.IOException;

public class DeviceNfcComponent extends BaseActivityComponent implements NFCComponent{
	NfcAdapter nfcAdapter;
	Activity activity;
	PendingIntent pendingIntent;

	TagReadListener onTagReadListener;
	TagWriteListener onTagWriteListener;


	String writeText = null;

	
	public DeviceNfcComponent(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	public void setOnTagReadListener(TagReadListener onTagReadListener) {
		this.onTagReadListener = onTagReadListener;
	}

	@Override
	public void setOnTagWriteListener(TagWriteListener onTagWriteListener) {
		this.onTagWriteListener = onTagWriteListener;
	}



	/**
	 * Indicates that we want to write the given text to the next tag detected
	 */
	public void writeText(String writeText) {
		this.writeText = writeText;
	}

	/**
	 * Stops a writeText operation
	 */
	public void undoWriteText() {
		this.writeText = null;
	}

	@Override
	public void onCreate() {
		nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		pendingIntent = PendingIntent.getActivity(activity, 0,
				new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
				//new Intent(activity, activity.getClass()), 0);
	}

	@Override
	public void onResume() {
		if (nfcAdapter != null) {
			if (!nfcAdapter.isEnabled()) {
				//TODO indicate that wireless should be opened
			}
			nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
		}
	}

	@Override
	public void onPause() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(activity);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Check if the following line has any use 
		// activity.setIntent(intent);
		if (writeText == null)
			readTagFromIntent(intent);
		else {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String id = bytesToHex(tag.getId());
			try {
				writeTag(activity, tag, writeText);
				onTagWriteListener.onTagWritten(id, null);
			} catch (NFCWriteException exception) {
				onTagWriteListener.onTagWritten(id, exception);
			} finally {
				writeText = null;
			}
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Reads a tag for a given intent and notifies listeners
	 * @param intent
	 */
	private void readTagFromIntent(Intent intent) {
		String action = intent.getAction();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
			Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String id = bytesToHex(myTag.getId());
			String text = null;
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
				Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
				if (rawMsgs != null) {
					NdefRecord[] records = ((NdefMessage) rawMsgs[0]).getRecords();
					text = ndefRecordToString(records[0]);
				}
			}
			onTagReadListener.onTagRead(id, text);
		}
	}

	public String ndefRecordToString(NdefRecord record) {
		byte[] payload = record.getPayload();
		return new String(payload);
	}

	/**
	 * Writes a text to a tag
	 * @param context
	 * @param tag
	 * @param data
	 * @throws NFCWriteException
	 */
	protected void writeTag(Context context, Tag tag, String data) throws NFCWriteException {
		// Record with actual data we care about
		NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, data.getBytes());


		// Complete NDEF message with both records
		NdefMessage message = new NdefMessage(new NdefRecord[] { relayRecord });

		Ndef ndef = Ndef.get(tag);
		if (ndef != null) {
			// If the tag is already formatted, just write the message to it
			try {
				ndef.connect();
			} catch (IOException e) {
				throw new NFCWriteException(NFCErrorType.unknownError);
			}
			// Make sure the tag is writable
			if (!ndef.isWritable()) {
				throw new NFCWriteException(NFCErrorType.ReadOnly);
			}

			// Check if there's enough space on the tag for the message
			int size = message.toByteArray().length;
			if (ndef.getMaxSize() < size) {
				throw new NFCWriteException(NFCErrorType.NoEnoughSpace);
			}

			try {
				// Write the data to the tag
				ndef.writeNdefMessage(message);
			} catch (TagLostException tle) {
				throw new NFCWriteException(NFCErrorType.tagLost, tle);
			} catch (IOException ioe) {
				throw new NFCWriteException(NFCErrorType.formattingError, ioe);// nfcFormattingErrorTitle
			} catch (FormatException fe) {
				throw new NFCWriteException(NFCErrorType.formattingError, fe);
			}
		} else {
			// If the tag is not formatted, format it with the message
			NdefFormatable format = NdefFormatable.get(tag);
			if (format != null) {
				try {
					format.connect();
					format.format(message);
				} catch (TagLostException tle) {
					throw new NFCWriteException(NFCErrorType.tagLost, tle);
				} catch (IOException ioe) {
					throw new NFCWriteException(NFCErrorType.formattingError, ioe);
				} catch (FormatException fe) {
					throw new NFCWriteException(NFCErrorType.formattingError, fe);
				}
			} else {
				throw new NFCWriteException(NFCErrorType.noNdefError);
			}
		}

	}




}
