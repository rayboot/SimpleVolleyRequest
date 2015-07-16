package com.github.rayboot.svr.sample;

import android.app.Application;

import com.github.rayboot.svr.SvrVolley;

/**
 * Created by rayboot on 15/4/30.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SvrVolley.Init(this);
    }
}
