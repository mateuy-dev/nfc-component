package com.mateuyabar.android.pillownfc.view.presenters;


import android.app.Activity;

import com.mateuyabar.android.cleanbase.BaseActivityComponent;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;
import com.mateuyabar.android.pillownfc.mixed.MixedNfcComponent;
import com.mateuyabar.android.pillownfc.view.ViewRederer;


/**
 * Presenter for an Activity that reads/writes NFC tags
 */
public class NfcActivityPresenter extends BaseActivityComponent implements TagWriteListener, TagReadListener {
	MixedNfcComponent nfcComponent;
	ViewRederer view;

	
	public NfcActivityPresenter(Activity activity, ViewRederer view) {
		super(activity);
		this.view = view;
		this.nfcComponent = new MixedNfcComponent(activity);
		addComponent(nfcComponent);
		nfcComponent.setOnTagReadListener(this);
		nfcComponent.setOnTagWriteListener(this);
	}

	public NFCComponent getNfcComponent() {
		return nfcComponent;
	}

	/**
	 * Write the given text to a tag.
	 * @param text
	 */
	public void writeText(String text){
		view.showWritingInProgress();
		nfcComponent.writeText(text);
	}

	@Override
	public void onTagWritten(String id, Exception exception) {
		view.writingFinished(id);
	}

	@Override
	public void onTagRead(String id, String tagRead) {
		view.tagRead(id, tagRead);
	}
}
