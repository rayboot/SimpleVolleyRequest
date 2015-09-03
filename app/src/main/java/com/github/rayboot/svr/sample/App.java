package com.github.rayboot.svr.sample;

import android.app.Application;

import com.github.rayboot.svr.IJsonParser;
import com.github.rayboot.svr.SvrVolley;
import com.google.gson.Gson;

/**
 * Created by rayboot on 15/4/30.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SvrVolley.Init(this, new IJsonParser() {
            @Override
            public <T> T parseJson(String jsonString, Class<T> mClazz) {
                return new Gson().fromJson(jsonString, mClazz);
            }
        }, new OkHttpStack());
    }
}
