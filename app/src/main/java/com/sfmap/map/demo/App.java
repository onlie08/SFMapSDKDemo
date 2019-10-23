package com.sfmap.map.demo;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {

    public static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        instance = this;
    }
}
