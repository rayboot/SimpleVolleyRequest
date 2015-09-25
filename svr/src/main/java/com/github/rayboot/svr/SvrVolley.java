package com.github.rayboot.svr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * adb shell setprop log.tag.Svr VERBOSE
 * @author rayboot
 */
public class SvrVolley {
    public static final String TAG = "Svr";
    private static SvrVolley sgrVolley = new SvrVolley();
    private static Context mainContext;
    private IJsonParser jsonParser;
    private HttpStack httpStack;

    public static void Init(Application application, IJsonParser jsonParser, HttpStack httpStack) {
        mainContext = application;
        //adb shell setprop log.tag.Svr VERBOSE
        VolleyLog.setTag(TAG);
        getInstance().setJsonParser(jsonParser);
        getInstance().setHttpStack(httpStack);
    }

    public void setJsonParser(IJsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public IJsonParser getJsonParser() {
        return jsonParser;
    }

    public void setHttpStack(HttpStack httpStack) {
        this.httpStack = httpStack;
    }

    public static Context getMainContext() {
        if (mainContext == null) {
            throw new IllegalArgumentException("you must use SgrVolley.Init(application) in your Application's onCreate function.");
        }
        return mainContext;
    }

    public static SvrVolley getInstance() {
        return sgrVolley;
    }

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            if (httpStack == null) {
                mRequestQueue = Volley.newRequestQueue(SvrVolley.getMainContext());
            } else {
                mRequestQueue = Volley.newRequestQueue(SvrVolley.getMainContext(), httpStack);
            }
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        addToRequestQueue(req, TAG);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * set the common headers params.
     * the common headers params always post to server.
     *
     * @param headers
     */
    public void setCommonHeaders(Map<String, String> headers) {
        SharedPreferences sp = mainContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                editor.putString(header.getKey(), header.getValue());
            }
        }
        editor.apply();
    }

    public void setCommonHeader(String key, String value) {
        SharedPreferences sp = mainContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Map<String, String> getCommonHeaders() {
        SharedPreferences sp = mainContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Map<String, ?> headers = sp.getAll();
        Map<String, String> result = new HashMap<>(10);
        for (Map.Entry<String, ?> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                result.put(entry.getKey(), null);
            } else {
                result.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return result;
    }

    public void clearCaches() {
        getInstance().getRequestQueue().getCache().clear();
    }

    public void removeCache(String key) {
        getInstance().getRequestQueue().getCache().remove(key);
    }
}
