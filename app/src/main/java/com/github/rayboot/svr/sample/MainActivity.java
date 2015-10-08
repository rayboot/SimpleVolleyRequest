package com.github.rayboot.svr.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.rayboot.svr.IFinishListener;
import com.github.rayboot.svr.Svr;
import com.github.rayboot.svr.SvrVolley;
import com.github.rayboot.svr.VolleyRequest;
import com.github.rayboot.svr.stateview.StateView;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateView stateView = (StateView) findViewById(R.id.stateView);
        stateView.setOnRetryListener(new StateView.RetryListener() {
            @Override
            public void onRetry() {
                Toast.makeText(MainActivity.this, "test retry", Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, String> params = new HashMap<>(10);
        params.put("type", "1");
        params.put("lastedId", "");
        params.put("imgType", "2");//接口返回imageobj
        Svr.builder(this, BaseModule.class)
                .requestParams(params)
                .stateView(stateView)
                .url("http://timefaceapi.timeface.cn/timefaceapi/v2/time/timelist")
                .finishListener(new IFinishListener<BaseModule>() {
                    @Override
                    public void onFinishResponse(boolean success, BaseModule response, VolleyError error) {

                        Log.e(TAG, "onFinishResponse do finish response");
                        Toast.makeText(MainActivity.this, "do finish response.", Toast.LENGTH_SHORT).show();
                        if (success) {
                            //do something with response
                        } else {
                            //deal some error info
                        }
                    }
                }).post2Queue();
    }

}
