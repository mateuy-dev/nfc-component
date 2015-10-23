package com.mateuyabar.android.cleanbase;

import android.content.Intent;

public interface Component {
    void onStart();
    void onResume();
    void onStop();
    void onDestroy();
    void onPause();
    void onCreate();
    void onNewIntent(Intent intent);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
