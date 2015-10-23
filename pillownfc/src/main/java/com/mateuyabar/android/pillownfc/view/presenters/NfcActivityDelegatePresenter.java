package com.mateuyabar.android.pillownfc.view.presenters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.mateuyabar.android.cleanbase.BaseActivityComponent;
import com.mateuyabar.android.cleanbase.Component;
import com.mateuyabar.android.cleanbase.PresenterFragment;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;
import com.mateuyabar.android.pillownfc.mixed.MixedNfcComponent;


/**
 * Presenter for an Activity that reads/writes NFC tags, but it sends the data to a fragment.
 * To be attached to the Activity. It starts the NFCComponent, and redirects the events to the current fragment.
 */
public class NfcActivityDelegatePresenter extends BaseActivityComponent implements TagWriteListener, TagReadListener {
	MixedNfcComponent nfcComponent;
	FragmentActivity activity;
	int fragmentId;

	public NfcActivityDelegatePresenter(FragmentActivity activity, int fragmentId) {
		super(activity);
		this.activity = activity;
		this.fragmentId = fragmentId;
		this.nfcComponent = new MixedNfcComponent(activity);
		addComponent(nfcComponent);
		nfcComponent.setOnTagReadListener(this);
		nfcComponent.setOnTagWriteListener(this);
	}

	public NFCComponent getNfcComponent() {
		return nfcComponent;
	}

	@Override
	public void onTagWritten(String id, Exception exception) {
		Fragment fragment = getFragment();
		if(fragment instanceof PresenterFragment) {
			Component presenter = ((PresenterFragment) fragment).getPresenter();
			if (presenter instanceof TagWriteListener) {
				((TagWriteListener) presenter).onTagWritten(id, exception);
			}
		}
	}

	@Override
	public void onTagRead(String id, String tagRead) {
		Fragment fragment = getFragment();
		if(fragment instanceof PresenterFragment){
			Component presenter = ((PresenterFragment)fragment).getPresenter();
			if(presenter instanceof TagReadListener){
				((TagReadListener)presenter).onTagRead(id, tagRead);
			}
		}
	}

	protected Fragment getFragment(){
		FragmentManager manager = activity.getSupportFragmentManager();
		return manager.findFragmentById(fragmentId);
	}
}
