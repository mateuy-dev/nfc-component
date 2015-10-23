package com.mateuyabar.android.pillownfc.sampleapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mateuyabar.android.cleanbase.PresenterActivity;
import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.view.BaseViewRenderer;
import com.mateuyabar.android.pillownfc.view.presenters.NfcActivityPresenter;

import java.util.Date;


public class PillowNfcSample extends PresenterActivity {
	NfcViewRenderer nfcViewRenderer = new NfcViewRenderer(this);
	NfcActivityPresenter presenter = new NfcActivityPresenter(this, nfcViewRenderer);

    @Override
    public NfcActivityPresenter getPresenter() {
        return presenter;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sample);

		Button writeButton = (Button) findViewById(R.id.write_button);
		writeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = new Date().toString();
				getPresenter().writeText(text);
			}
		});
	}

	public class NfcViewRenderer extends BaseViewRenderer {

		public NfcViewRenderer(Context context) {
			super(context);
		}

		@Override
		protected void displayWritingFinishedMessage(String id) {
			String string = getString(com.mateuyabar.android.pillownfc.R.string.tag_written_toast)+": "+id;
			Toast.makeText(getContext(), string, Toast.LENGTH_LONG).show();
		}

		@Override
		protected void displayTagReadMessage(String id, String tagRead) {
			Toast.makeText(getContext(), "tag read:"+id+" - "+tagRead, Toast.LENGTH_LONG).show();
		}

		@Override
		protected NFCComponent getNfcComponent() {
			return getPresenter().getNfcComponent();
		}


	}

}
