package com.mateuyabar.android.cleanbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public abstract class PresenterActivity extends AppCompatActivity {
    protected abstract Component getPresenter();

    @Override
    protected void onPause() {
        getPresenter().onPause();
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getPresenter().onCreate();
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    protected void onStop() {
        getPresenter().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        getPresenter().onNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPresenter().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
