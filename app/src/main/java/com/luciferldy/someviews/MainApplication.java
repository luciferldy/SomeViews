package com.luciferldy.someviews;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainApplication extends Application {

    private static final String LOG_TAG = MainApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}
