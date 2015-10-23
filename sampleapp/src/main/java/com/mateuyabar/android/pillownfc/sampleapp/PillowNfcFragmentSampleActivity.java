package com.mateuyabar.android.pillownfc.sampleapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mateuyabar.android.cleanbase.Component;
import com.mateuyabar.android.cleanbase.PresenterActivity;
import com.mateuyabar.android.cleanbase.PresenterFragment;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.view.BaseViewRenderer;
import com.mateuyabar.android.pillownfc.view.presenters.NfcActivityDelegatePresenter;
import com.mateuyabar.android.pillownfc.view.presenters.NfcFragmentPresenter;

import java.util.Date;

public class PillowNfcFragmentSampleActivity extends PresenterActivity {
    public static final int FRAGMENT_CONTENT_ID = R.id.content;
    NfcActivityDelegatePresenter presenter = new NfcActivityDelegatePresenter(this, FRAGMENT_CONTENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillow_nfc_fragment_sample);
        getSupportFragmentManager().beginTransaction().add(FRAGMENT_CONTENT_ID, new InnerFragment()).commit();
    }

    @Override
    public Component getPresenter() {
        return presenter;
    }


    /**
     * Inner fragment
     */
    public static class InnerFragment extends PresenterFragment {
        NfcViewRenderer nfcViewRenderer;
        NfcFragmentPresenter presenter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            nfcViewRenderer = new NfcViewRenderer(getContext());
            presenter = new NfcFragmentPresenter((PresenterActivity) getActivity(), nfcViewRenderer);
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_sample, container, false);

            Button writeButton = (Button) view.findViewById(R.id.write_button);
            writeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = new Date().toString();
                    getPresenter().writeText(text);
                }
            });
            return view;
        }


        @Override
        public NfcFragmentPresenter getPresenter() {
            return presenter;
        }


        public class NfcViewRenderer extends BaseViewRenderer {
            public NfcViewRenderer(Context context) {
                super(context);
            }
            @Override
            protected void displayWritingFinishedMessage(String id) {
                String string = getString(com.mateuyabar.android.pillownfc.R.string.tag_written_toast)+": "+id;
                Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
            }
            @Override
            protected void displayTagReadMessage(String id, String tagRead) {
                Toast.makeText(getActivity(), "tag read:"+id+" - "+tagRead, Toast.LENGTH_LONG).show();
            }

            @Override
            protected NFCComponent getNfcComponent() {
                return null;
            }


        }
    }



}
