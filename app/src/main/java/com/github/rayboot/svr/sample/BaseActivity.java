package com.github.rayboot.svr.sample;

import android.app.Activity;
import android.os.Bundle;

import com.github.rayboot.svr.INetWork;
import com.github.rayboot.svr.SvrVolley;

/**
 * Created by rayboot on 15/4/30.
 */
public class BaseActivity extends Activity implements INetWork {
    protected String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = ((Object) this).getClass().getSimpleName();
    }

    @Override
    protected void onDestroy() {
        SvrVolley.getInstance().cancelPendingRequests(getVolleyTag());
        super.onDestroy();
    }

    @Override
    public String getVolleyTag() {
        return TAG +  this.hashCode();
    }
}
