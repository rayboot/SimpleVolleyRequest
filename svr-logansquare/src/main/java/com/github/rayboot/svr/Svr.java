package com.github.rayboot.svr;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.github.rayboot.svr.stateview.StateView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author rayboot
 */
public class Svr<T> {
    int mMethod = Request.Method.POST;
    String mUrl;
    String mTag;
    Class<T> mClazz;
    Response.Listener<T> mSuccessListener;
    Response.ErrorListener mErrorListener;
    View mClickView = null;
    Map<String, String> mParams = new HashMap<String, String>();
    VolleyRequest.FinishListener<T> mFinishListener;
    Map<String, String> mHeaders;
    StateView mErrorView = null;
    final int WIFI_TIMEOUT_TIME = 15 * 1000;
    final int MOBILE_TIMEOUT_TIME = 60 * 1000;
    int mCustomTimeOut = -1;

    final boolean DEBUG = true;

    public static <T> Svr<T> builder(Context context, Class<T> classOfT) {
        return new Svr<T>().with(context).gsonClass(classOfT);
    }

    public Svr<T> method(int method) {
        this.mMethod = method;
        return this;
    }

    public Svr<T> timeout(int timeout) {
        this.mCustomTimeOut = timeout;
        return this;
    }

    public Svr<T> url(String url) {
        this.mUrl = url;
        return this;
    }

    private Svr<T> gsonClass(Class<T> mClazz) {
        this.mClazz = mClazz;
        return this;
    }

    public Svr<T> successListener(Response.Listener<T> listener) {
        this.mSuccessListener = listener;
        return this;
    }

    public Svr<T> finishListener(VolleyRequest.FinishListener<T> listener) {
        this.mFinishListener = listener;
        return this;
    }

    public Svr<T> errorListener(Response.ErrorListener listener) {
        this.mErrorListener = listener;
        return this;
    }

    public Svr<T> clickView(View clickView) {
        this.mClickView = clickView;
        return this;
    }

    public Svr<T> requestParams(Map<String, String> params) {
        this.mParams = params;
        return this;
    }

    public Svr<T> setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
        return this;
    }

    public Svr<T> stateView(StateView view) {
        this.mErrorView = view;
        return this;
    }

    public Svr<T> tag(String tag) {
        this.mTag = tag;
        return this;
    }

    private Svr<T> with(Context context) {
        if (context instanceof INetWork) {
            mTag = ((INetWork) context).getVolleyTag();
        }
        return this;
    }

    public void post2Queue() {
        post2Queue(mTag);
    }

    public void post2Queue(String tag) {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url must not be null.");
        }
        if (mClazz == null) {
            throw new IllegalArgumentException("class must not be null.");
        }
        if (tag == null) {
            throw new IllegalArgumentException(
                    "tag must not be null.you can use .tag() or .with(INetWork context) to add tag");
        }


        VolleyRequest<T> volleyRequest = new VolleyRequest<T>(this.mMethod, this.mUrl,
                this.mClazz, this.mSuccessListener,
                this.mErrorListener, mParams, mHeaders,
                mCustomTimeOut > 0 ? mCustomTimeOut : NetworkUtil.isWifiConnected(SvrVolley.getMainContext()) ? WIFI_TIMEOUT_TIME : MOBILE_TIMEOUT_TIME);

        volleyRequest.oneClickView(mClickView);
        volleyRequest.finishListener(mFinishListener);
        volleyRequest.errorView(mErrorView);
        volleyRequest.setShouldCache(false);
        if (DEBUG) {
            StringBuilder params = new StringBuilder(10);
            params.append("?");
            try {
                if (volleyRequest.getParams() != null
                        && volleyRequest.getParams().size() > 0) {
                    Iterator iter = volleyRequest.getParams().entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        params.append(entry.getKey());
                        params.append("=");
                        params.append(entry.getValue());
                        params.append("&");
                    }
                }
                params.deleteCharAt(params.length() - 1);
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
            System.out.println("volley req url = " + mUrl + params.toString());
        }
        SvrVolley.getInstance().addToRequestQueue(volleyRequest, tag);
    }
}
