package com.mateuyabar.android.pillownfc.view.presenters;


import com.mateuyabar.android.cleanbase.BaseComponent;
import com.mateuyabar.android.cleanbase.PresenterActivity;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.TagReadListener;
import com.mateuyabar.android.pillownfc.TagWriteListener;
import com.mateuyabar.android.pillownfc.view.ViewRederer;

/**
 * Presenter of a Fragment that recieves Nfc data from its Activity (with a presenter of type NfcRWActivityDelegatePresenter)
 */
public class NfcFragmentPresenter extends BaseComponent implements TagWriteListener, TagReadListener {
    ViewRederer view;
    NfcActivityDelegatePresenter activityPresenter;

    public NfcFragmentPresenter(PresenterActivity activity, ViewRederer view) {
        super(activity);
        this.view = view;
        activityPresenter = ((NfcActivityDelegatePresenter)activity.getPresenter());
    }

    /**
     * Write the given text to a tag.
     * @param text
     */
    public void writeText(String text){
        view.showWritingInProgress();
        getNfcComponent().writeText(text);
    }

    @Override
    public void onTagWritten(String id, Exception exception) {
        view.writingFinished(id);
    }

    @Override
    public void onTagRead(String id, String tagRead) {
        view.tagRead(id, tagRead);
    }

    public NFCComponent getNfcComponent(){
        return activityPresenter.getNfcComponent();
    }
}
