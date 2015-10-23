package com.mateuyabar.android.pillownfc.view;

public interface ViewRederer {
	void showWritingInProgress();
	void writingFinished(String id);
	void tagRead(String id, String tagRead);
}