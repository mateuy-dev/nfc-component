package com.mateuyabar.android.pillownfc.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mateuyabar.android.pillownfc.NFCComponent;
import com.mateuyabar.android.pillownfc.R;

public abstract class BaseViewRenderer implements ViewRederer {
	int dialogViewId = R.layout.write_nfc_dialog_view;
	Context context;
	AlertDialog dialog;


	public BaseViewRenderer(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void showWritingInProgress() {
		dialog = createWaitingDialog();
		dialog.show();
	}

	@Override
	public void writingFinished(String id) {
		dialog.dismiss();
		displayWritingFinishedMessage(id);

	}

	@Override
	public void tagRead(String id, String tagRead) {
		displayTagReadMessage(id, tagRead);
	}

	/**
	 * Default behaviour does nothing. Overwrite in subclasses.
	 */
	protected void displayWritingFinishedMessage(String id){
	}

	/**
	 * Default behaviour does nothing. Overwrite in subclasses.
	 */
	protected void displayTagReadMessage(String id, String tagRead){
	}
	protected abstract NFCComponent getNfcComponent();

	/**
	 * Creates a dialog while waiting for the tag
	 * @return
	 */
	private AlertDialog createWaitingDialog(){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(dialogViewId, null, false);
		ImageView image = new ImageView(context);
		image.setImageResource(R.drawable.ic_nfc_black_48dp);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.wait_write_dialog_title)
				.setView(view)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						getNfcComponent().undoWriteText();
					}
				});
		return builder.create();
	}


}